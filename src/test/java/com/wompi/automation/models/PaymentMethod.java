package com.wompi.automation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethod {

    private String id;
    private String type;

    @Builder.Default
    private Integer installments = 1;

    public static PaymentMethod createACH() {
        return PaymentMethod.builder()
                .id("ACH")
                .type("BANK_ACCOUNT")
                .installments(1)
                .build();
    }
}
