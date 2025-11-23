package com.core.domain.model.transfer;

/**
 * Status do ciclo de vida de uma transferência de propriedade.
 *
 * Fluxo completo:
 * PENDENTE → CONFIGURANDO → AGUARDANDO_APROVACOES → CONCLUIDA
 */
public enum TransferStatus {
    /**
     * Transferência criada no banco de dados, aguardando envio para blockchain.
     * Estado inicial quando comprador solicita transferência.
     */
    PENDENTE,

    /**
     * Job CONFIGURE_TRANSFER enviado para RabbitMQ.
     * Transferência está sendo configurada na blockchain (definindo approvers).
     */
    CONFIGURANDO,

    /**
     * Transferência configurada na blockchain, aguardando aprovações.
     * Approvers (Cartório, Prefeitura, IF) precisam aprovar.
     * Comprador precisa aceitar.
     */
    AGUARDANDO_APROVACOES,

    /**
     * Transferência executada com sucesso na blockchain.
     * Tokens transferidos e proprietário atualizado.
     * Estado final de sucesso.
     */
    CONCLUIDA
}
