package com.wompi.automation.questions;

import io.restassured.response.Response;

/**
 * Question: ObtenerStatusTransaccion
 * El actor usa esta pregunta para leer el status de la última
 * respuesta recibida de WOMPI.
 */
public class ObtenerStatusTransaccion {

    private ObtenerStatusTransaccion() {}

    public static ObtenerStatusTransaccion deLa(Response respuesta) {
        return new ObtenerStatusTransaccion();
    }

    public static String desde(Response respuesta) {
        // WOMPI retorna el status dentro de data.status
        String status = respuesta.jsonPath().getString("data.status");
        return status != null ? status : respuesta.jsonPath().getString("status");
    }

    public static String idDesde(Response respuesta) {
        String id = respuesta.jsonPath().getString("data.id");
        return id != null ? id : respuesta.jsonPath().getString("id");
    }

    public static String campoDesde(Response respuesta, String campo) {
        String valor = respuesta.jsonPath().getString("data." + campo);
        return valor != null ? valor : respuesta.jsonPath().getString(campo);
    }

    public static String mensajeErrorDesde(Response respuesta) {
        // Auth/Not-found errors: {"error":{"reason":"..."}}
        String razon = respuesta.jsonPath().getString("error.reason");
        if (razon != null) return razon;
        // Validation errors: {"error":{"messages":{"field":["msg",...]}}}
        Object mensajes = respuesta.jsonPath().get("error.messages");
        if (mensajes != null) return mensajes.toString();
        return respuesta.jsonPath().getString("message");
    }
}
