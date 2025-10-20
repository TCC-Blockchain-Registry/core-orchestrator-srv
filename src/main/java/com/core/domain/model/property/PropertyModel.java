package com.core.domain.model.property;

import java.time.LocalDateTime;

/**
 * Property Domain Model
 * Represents a property registered in the system and blockchain
 * Matches the Solidity contract PropertyInfo struct
 */
public class PropertyModel {
    
    private Long id;
    private Long matriculaId;
    private Long folha;
    private String comarca;
    private String endereco;
    private Long metragem;
    private String proprietario;  // Address do proprietário (wallet address)
    private Long matriculaOrigem;
    private PropertyType tipo;
    private Boolean isRegular;
    private String blockchainTxHash;  // Hash da transação na blockchain
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public PropertyModel() {
        this.isRegular = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with all fields
    public PropertyModel(Long id, Long matriculaId, Long folha, String comarca, String endereco,
                        Long metragem, String proprietario, Long matriculaOrigem, PropertyType tipo,
                        Boolean isRegular, String blockchainTxHash, LocalDateTime createdAt, 
                        LocalDateTime updatedAt) {
        this.id = id;
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
    
    // Business methods
    public void updateProprietario(String newProprietario) {
        this.proprietario = newProprietario;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsIrregular() {
        this.isRegular = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsRegular() {
        this.isRegular = true;
        this.updatedAt = LocalDateTime.now();
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

