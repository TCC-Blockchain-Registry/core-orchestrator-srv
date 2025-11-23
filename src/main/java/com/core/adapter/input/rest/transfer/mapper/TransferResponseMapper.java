package com.core.adapter.input.rest.transfer.mapper;

import com.core.adapter.input.rest.transfer.dto.TransferResponse;
import com.core.domain.model.transfer.TransferModel;

/**
 * Transfer Response Mapper
 * Maps TransferModel (domain) to TransferResponse (REST API DTO)
 */
public class TransferResponseMapper {

    /**
     * Map TransferModel to TransferResponse
     *
     * @param transfer Domain model from the business layer
     * @return TransferResponse DTO for REST API
     */
    public static TransferResponse toResponse(TransferModel transfer) {
        return new TransferResponse(
            transfer.getId(),
            transfer.getMatriculaId(),
            transfer.getSellerId(),
            transfer.getBuyerId(),
            transfer.getStatus(),
            transfer.getBlockchainTxHash(),
            transfer.getRequestHash(),
            transfer.getCreatedAt(),
            transfer.getUpdatedAt()
        );
    }
}
