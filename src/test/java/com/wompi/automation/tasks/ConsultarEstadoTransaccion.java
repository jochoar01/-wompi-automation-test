package com.wompi.automation.tasks;

import com.wompi.automation.abilities.MakeHttpRequests;
import io.restassured.response.Response;

/**
 * Task: ConsultarEstadoTransaccion
 * El actor ejecuta esta tarea para consultar el estado actual
 * de una transacción ACH existente en WOMPI.
 */
public class ConsultarEstadoTransaccion {

    private final String transaccionId;

    private ConsultarEstadoTransaccion(String transaccionId) {
        this.transaccionId = transaccionId;
    }

    public static ConsultarEstadoTransaccion conId(String transaccionId) {
        return new ConsultarEstadoTransaccion(transaccionId);
    }

    public Response ejecutarCon(MakeHttpRequests ability) {
        return ability.asRequest()
                .get("/transactions/" + transaccionId);
    }
}
