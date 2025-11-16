package com.core.adapter.input.rest.webhook;

/**
 * Webhook Event DTO
 *
 * Represents a blockchain event received via webhook
 */
public class WebhookEvent {

    private String eventType;
    private String matriculaId;
    private String transferId;
    private String txHash;
    private String from;
    private String to;
    private String approver;
    private String buyer;
    private Long timestamp;

    // Constructors
    public WebhookEvent() {
    }

    public WebhookEvent(String eventType, String matriculaId, String txHash) {
        this.eventType = eventType;
        this.matriculaId = matriculaId;
        this.txHash = txHash;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(String matriculaId) {
        this.matriculaId = matriculaId;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "WebhookEvent{" +
                "eventType='" + eventType + '\'' +
                ", matriculaId='" + matriculaId + '\'' +
                ", transferId='" + transferId + '\'' +
                ", txHash='" + txHash + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
