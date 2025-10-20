package com.core.adapter.output.persistence.property.entity;

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
    
    @Column(name = "proprietario", nullable = false, length = 42)
    private String proprietario;  // Ethereum address (0x...)
    
    @Column(name = "matricula_origem")
    private Long matriculaOrigem;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private PropertyType tipo;
    
    @Column(name = "is_regular", nullable = false)
    private Boolean isRegular;
    
    @Column(name = "blockchain_tx_hash", length = 66)
    private String blockchainTxHash;  // Ethereum transaction hash (0x...)
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Default constructor
    public PropertyEntity() {
    }
    
    // Constructor with all fields
    public PropertyEntity(Long matriculaId, Long folha, String comarca, String endereco,
                         Long metragem, String proprietario, Long matriculaOrigem, 
                         PropertyType tipo, Boolean isRegular, String blockchainTxHash,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.matriculaId = matriculaId;
        this.folha = folha;
        this.comarca = comarca;
        this.endereco = endereco;
        this.metragem = metragem;
        this.proprietario = proprietario;
        this.matriculaOrigem = matriculaOrigem;
        this.tipo = tipo;
        this.isRegular = isRegular;
        this.blockchainTxHash = blockchainTxHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    
    public String getProprietario() {
        return proprietario;
    }
    
    public void setProprietario(String proprietario) {
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

