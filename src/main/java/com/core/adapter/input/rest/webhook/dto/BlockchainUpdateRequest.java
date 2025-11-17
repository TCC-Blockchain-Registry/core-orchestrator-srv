package com.core.adapter.input.rest.webhook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockchainUpdateRequest {

    @JsonProperty("transactionHash")
    private String transactionHash;

    @JsonProperty("blockNumber")
    private Long blockNumber;

    @JsonProperty("jobId")
    private String jobId;

    @JsonProperty("status")
    private String status;

    public BlockchainUpdateRequest() {
    }

    public BlockchainUpdateRequest(String transactionHash, Long blockNumber, String jobId, String status) {
        this.transactionHash = transactionHash;
        this.blockNumber = blockNumber;
        this.jobId = jobId;
        this.status = status;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

