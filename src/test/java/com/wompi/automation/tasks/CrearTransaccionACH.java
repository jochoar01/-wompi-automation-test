package com.wompi.automation.tasks;

import com.wompi.automation.abilities.MakeHttpRequests;
import com.wompi.automation.models.TransaccionACH;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Task: CrearTransaccionACH
 * El actor ejecuta esta tarea para enviar una solicitud de creación
 * de transacción ACH a la API de WOMPI.
 */
public class CrearTransaccionACH {

    private final TransaccionACH transaccion;

    private CrearTransaccionACH(TransaccionACH transaccion) {
        this.transaccion = transaccion;
    }

    public static CrearTransaccionACH con(TransaccionACH transaccion) {
        return new CrearTransaccionACH(transaccion);
    }

    public Response ejecutarCon(MakeHttpRequests ability) {
        Map<String, Object> paymentMethod = new HashMap<>();
        paymentMethod.put("type", "ACH");
        paymentMethod.put("user_type", 0);
        paymentMethod.put("user_legal_id", transaccion.getNumDocTitular());
        paymentMethod.put("user_legal_id_type", transaccion.getTipoDocTitular());
        paymentMethod.put("financial_institution_code", transaccion.getBanco());
        paymentMethod.put("payment_description", transaccion.getDescripcion());

        Map<String, Object> customer = new HashMap<>();
        customer.put("email", transaccion.getEmailCliente());
        customer.put("full_name", transaccion.getTitularCuenta());

        Map<String, Object> body = new HashMap<>();
        body.put("amount_in_cents", transaccion.getMonto());
        body.put("currency", transaccion.getMoneda());
        body.put("customer_email", transaccion.getEmailCliente());
        body.put("payment_method", paymentMethod);
        body.put("customer_data", customer);
        body.put("reference", transaccion.getReferencia() != null
                ? transaccion.getReferencia()
                : "REF-" + System.currentTimeMillis());

        return ability.asRequest()
                .body(body)
                .post("/transactions");
    }
}
