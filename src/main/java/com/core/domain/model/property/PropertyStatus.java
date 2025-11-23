package com.core.domain.model.property;

/**
 * Status do ciclo de vida de uma propriedade no sistema.
 *
 * Fluxo de Registro:
 * PENDENTE → PROCESSANDO_REGISTRO → OK
 *
 * Fluxo de Transferência:
 * OK → EM_TRANSFERENCIA → OK (com novo proprietário)
 */
public enum PropertyStatus {
    /**
     * Propriedade criada no banco de dados e enviada para fila de processamento.
     * Aguardando processamento pela blockchain.
     */
    PENDENTE,

    /**
     * Registro recebido e sendo processado na blockchain pelo smart contract PropertyTitleTREX.
     * Aguardando aprovações das entidades (Cartório, Prefeitura, Instituição Financeira).
     */
    PROCESSANDO_REGISTRO,

    /**
     * Propriedade em processo de transferência de propriedade.
     * Aguardando aprovações e aceitação do comprador.
     */
    EM_TRANSFERENCIA,

    /**
     * Propriedade totalmente registrada (tokens mintados) ou transferência concluída.
     * Estado final de sucesso tanto para registro quanto para transferência.
     */
    OK
}
