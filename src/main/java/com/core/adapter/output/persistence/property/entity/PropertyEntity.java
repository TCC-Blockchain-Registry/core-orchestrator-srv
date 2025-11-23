package com.core.adapter.output.persistence.property.entity;

import com.core.adapter.output.persistence.user.entity.UserEntity;
import com.core.domain.model.property.PropertyStatus;
import com.core.domain.model.property.PropertyType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Property Entity for Hibernate persistence
 * Matches the Solidity contract PropertyInfo struct
 */
@Entity
@Table(name = "properties")
public class PropertyEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "matricula_id", nullable = false, unique = true)
    private Long matriculaId;
    
    @Column(name = "folha", nullable = false)
    private Long folha;
    
    @Column(name = "comarca", nullable = false, length = 100)
    private String comarca;
    
    @Column(name = "endereco", nullable = false, length = 255)
    private String endereco;
    
    @Column(name = "metragem", nullable = false)
    private Long metragem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietario", nullable = false, foreignKey = @ForeignKey(name = "fk_property_user"))
    private UserEntity proprietario;  // FK to users table
    
    @Column(name = "matricula_origem")
    private Long matriculaOrigem;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private PropertyType tipo;
    
    @Column(name = "is_regular", nullable = false)
    private Boolean isRegular;
    
    @Column(name = "blockchain_tx_hash", length = 66)
    private String blockchainTxHash;  // Ethereum transaction hash (0x...)
    
    @Column(name = "request_hash", length = 66)
    private String requestHash;  // V2 approval system request hash (0x...)
    
    @Column(name = "approval_status", length = 20)
    private String approvalStatus;  // PENDING_APPROVALS, APPROVED, EXECUTED

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private PropertyStatus status;  // PENDENTE, PROCESSANDO_REGISTRO, EM_TRANSFERENCIA, OK
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Default constructor
    public PropertyEntity() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getMatriculaId() {
        return matriculaId;
    }
    
    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }
    
    public Long getFolha() {
        return folha;
    }
    
    public void setFolha(Long folha) {
        this.folha = folha;
    }
    
    public String getComarca() {
        return comarca;
    }
    
    public void setComarca(String comarca) {
        this.comarca = comarca;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public Long getMetragem() {
        return metragem;
    }
    
    public void setMetragem(Long metragem) {
        this.metragem = metragem;
    }
    
    public UserEntity getProprietario() {
        return proprietario;
    }
    
    public void setProprietario(UserEntity proprietario) {
        this.proprietario = proprietario;
    }
    
    public Long getMatriculaOrigem() {
        return matriculaOrigem;
    }
    
    public void setMatriculaOrigem(Long matriculaOrigem) {
        this.matriculaOrigem = matriculaOrigem;
    }
    
    public PropertyType getTipo() {
        return tipo;
    }
    
    public void setTipo(PropertyType tipo) {
        this.tipo = tipo;
    }
    
    public Boolean getIsRegular() {
        return isRegular;
    }
    
    public void setIsRegular(Boolean isRegular) {
        this.isRegular = isRegular;
    }
    
    public String getBlockchainTxHash() {
        return blockchainTxHash;
    }
    
    public void setBlockchainTxHash(String blockchainTxHash) {
        this.blockchainTxHash = blockchainTxHash;
    }
    
    public String getRequestHash() {
        return requestHash;
    }
    
    public void setRequestHash(String requestHash) {
        this.requestHash = requestHash;
    }
    
    public String getApprovalStatus() {
        return approvalStatus;
    }
    
    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    
    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

