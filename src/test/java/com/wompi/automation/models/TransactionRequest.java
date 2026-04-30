package com.wompi.automation.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa el cuerpo (request body) de una transacción ACH enviada a la API de WOMPI.
 * Construida con el patrón Builder de Lombok para ser usada en el Screenplay Pattern
 * como payload de la Task {@code CrearTransaccionACH}.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionRequest {

    @NotNull
    private String reference;

    @NotNull
    @Positive
    private Long amount_in_cents;

    @Builder.Default
    private String currency = "COP";

    @NotNull
    @Email
    private String customer_email;

    private String description;

    private Map<String, Object> payment_method;

    /**
     * Configura {@code payment_method} con los valores fijos requeridos por WOMPI para ACH:
     * {@code id = "ACH"} y {@code type = "BANK_ACCOUNT"}.
     *
     * @return la misma instancia para encadenamiento fluido
     */
    public TransactionRequest setPaymentMethodACH() {
        Map<String, Object> method = new HashMap<>();
        method.put("id", "ACH");
        method.put("type", "BANK_ACCOUNT");
        this.payment_method = method;
        return this;
    }
}
