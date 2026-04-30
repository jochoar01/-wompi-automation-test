package com.wompi.automation.questions;

import io.restassured.response.Response;

/**
 * Question: VerificarMonto
 * El actor usa esta pregunta para leer y validar el monto
 * de la transacción en la respuesta de WOMPI.
 */
public class VerificarMonto {

    private VerificarMonto() {}

    public static int desde(Response respuesta) {
        Integer monto = respuesta.jsonPath().getInt("data.amount_in_cents");
        return monto != null ? monto : respuesta.jsonPath().getInt("amount_in_cents");
    }

    public static boolean esMayorACero(Response respuesta) {
        return desde(respuesta) > 0;
    }
}
