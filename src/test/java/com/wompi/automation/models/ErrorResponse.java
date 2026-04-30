package com.wompi.automation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String error;
    private String message;
    private Integer status_code;
    private String timestamp;

    public boolean isClientError() {
        return status_code != null && status_code >= 400 && status_code < 500;
    }

    public boolean isServerError() {
        return status_code != null && status_code >= 500 && status_code < 600;
    }
}
