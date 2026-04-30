package com.wompi.automation.models;

import lombok.Builder;
import lombok.Data;

/**
 * Modelo de datos para una transacción ACH de WOMPI.
 */
@Data
@Builder
public class TransaccionACH {

    private int monto;
    private String moneda;
    private String descripcion;
    private String emailCliente;
    private String tipoCuenta;
    private String numeroCuenta;
    private String banco;
    private String titularCuenta;
    private String tipoDocTitular;
    private String numDocTitular;
    private String referencia;
}
