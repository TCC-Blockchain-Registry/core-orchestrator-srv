# Multi-stage build para otimizar a imagem final

# Estágio 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

# Instalar Maven
RUN apk add --no-cache maven

WORKDIR /app

# Copiar arquivos de configuração
COPY pom.xml .

# Baixar dependências (cache layer)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Compilar aplicação
RUN mvn clean package -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Criar usuário não-root para segurança
RUN addgroup -g 1000 app && \
    adduser -D -s /bin/sh -u 1000 -G app app

# Copiar apenas o JAR compilado do estágio anterior
COPY --from=builder /app/target/core-orchestrator-srv-1.0-SNAPSHOT.jar app.jar

# Mudar ownership para usuário app
RUN chown app:app app.jar

# Expor porta da aplicação
EXPOSE 8080

# Usar usuário não-root
USER app

# Comando para executar a aplicação
CMD ["java", "-jar", "app.jar"]
