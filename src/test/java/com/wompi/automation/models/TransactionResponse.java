package com.wompi.automation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionResponse {

    private String id;
    private String status;
    private Long amount_in_cents;
    private String currency;
    private String created_at;
    private String customer_email;
    private String reference;
    private String payment_method_type;

    public boolean isPending() {
        return "PENDING_PAYMENT".equals(status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    public boolean isDeclined() {
        return "DECLINED".equals(status);
    }
}
