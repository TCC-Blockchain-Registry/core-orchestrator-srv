-- Migration Script: Create Transfers Table
-- Created: 2025-01-19
-- Description: Creates the transfers table for property ownership transfers

-- Create transfers table
CREATE TABLE IF NOT EXISTS transfers (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL CHECK (status IN ('PENDENTE', 'CONFIGURANDO', 'AGUARDANDO_APROVACOES', 'CONCLUIDA')),
    blockchain_tx_hash VARCHAR(66),
    request_hash VARCHAR(66),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraints
    CONSTRAINT fk_transfer_seller FOREIGN KEY (seller_id) REFERENCES users(id),
    CONSTRAINT fk_transfer_buyer FOREIGN KEY (buyer_id) REFERENCES users(id),

    -- Business rule constraints
    CONSTRAINT check_different_users CHECK (seller_id != buyer_id)
);

-- Create indexes for performance
CREATE INDEX idx_transfers_matricula_id ON transfers(matricula_id);
CREATE INDEX idx_transfers_seller_id ON transfers(seller_id);
CREATE INDEX idx_transfers_buyer_id ON transfers(buyer_id);
CREATE INDEX idx_transfers_status ON transfers(status);
CREATE INDEX idx_transfers_created_at ON transfers(created_at DESC);

-- Add comment to table
COMMENT ON TABLE transfers IS 'Property ownership transfer requests and their status';

-- Add comments to columns
COMMENT ON COLUMN transfers.id IS 'Primary key';
COMMENT ON COLUMN transfers.matricula_id IS 'Property registration number being transferred';
COMMENT ON COLUMN transfers.seller_id IS 'Current owner (seller) user ID';
COMMENT ON COLUMN transfers.buyer_id IS 'New owner (buyer) user ID';
COMMENT ON COLUMN transfers.status IS 'Transfer lifecycle status: PENDENTE, CONFIGURANDO, AGUARDANDO_APROVACOES, CONCLUIDA';
COMMENT ON COLUMN transfers.blockchain_tx_hash IS 'Blockchain transaction hash (0x...)';
COMMENT ON COLUMN transfers.request_hash IS 'Approval system request hash for V2 approval tracking';
COMMENT ON COLUMN transfers.created_at IS 'Timestamp when transfer was initiated';
COMMENT ON COLUMN transfers.updated_at IS 'Timestamp of last update';

-- Migration complete!
-- Transfer flow:
-- 1. PENDENTE - Created in database, waiting to send to blockchain
-- 2. CONFIGURANDO - CONFIGURE_TRANSFER job published to queue
-- 3. AGUARDANDO_APROVACOES - Configured on blockchain, waiting for approvals + buyer acceptance
-- 4. CONCLUIDA - All approvals received, transfer executed, ownership updated
