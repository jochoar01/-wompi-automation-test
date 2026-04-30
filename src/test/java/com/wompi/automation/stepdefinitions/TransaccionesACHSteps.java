package com.wompi.automation.stepdefinitions;

import com.wompi.automation.abilities.MakeHttpRequests;
import com.wompi.automation.models.TransaccionACH;
import com.wompi.automation.questions.ObtenerStatusTransaccion;
import com.wompi.automation.questions.VerificarMonto;
import com.wompi.automation.tasks.ConsultarEstadoTransaccion;
import com.wompi.automation.tasks.CrearTransaccionACH;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step Definitions para el Screenplay Pattern de transacciones ACH en WOMPI.
 * Cada método traduce un step de Gherkin a acciones del actor Juan.
 */
public class TransaccionesACHSteps {

    // Estado compartido entre steps del mismo escenario
    private MakeHttpRequests abilityDeJuan;
    private Response ultimaRespuesta;
    private TransaccionACH.TransaccionACHBuilder transaccionBuilder;
    private String transaccionIdCreada;

    private static final String BASE_URL = System.getProperty(
            "wompi.baseUrl", "https://sandbox.wompi.co/v1");
    private static final String MERCHANT_KEY = System.getProperty(
            "wompi.merchantKey", "prv_test_changeme");

    // =========================================================
    // GIVEN — Abilities del actor Juan
    // =========================================================

    @Given("que Juan puede hacer peticiones HTTP a WOMPI")
    public void juanPuedeHacerPeticionesHttpAWompi() {
        abilityDeJuan = MakeHttpRequests.aWompiCon(BASE_URL, MERCHANT_KEY);
        transaccionBuilder = TransaccionACH.builder()
                .moneda("COP")
                .descripcion("Pago de prueba ACH");
    }

    @Given("que Juan ya creó una transacción ACH con id {string}")
    public void juanYaCreoUnaTransaccionACHConId(String transaccionId) {
        // Crea una transacción real para tener un ID válido que consultar
        TransaccionACH transaccion = TransaccionACH.builder()
                .monto(150000)
                .moneda("COP")
                .descripcion("Pago de prueba ACH")
                .emailCliente("cliente@correo.com")
                .tipoCuenta("CHECKING_ACCOUNT")
                .numeroCuenta("1234567890")
                .banco("BANCOLOMBIA")
                .titularCuenta("Juan Pérez")
                .tipoDocTitular("CC")
                .numDocTitular("1000123456")
                .build();
        Response creacion = CrearTransaccionACH.con(transaccion).ejecutarCon(abilityDeJuan);
        transaccionIdCreada = creacion.jsonPath().getString("data.id");
    }

    @Given("que Juan usa la merchant key {string}")
    public void juanUsaLaMerchantKey(String llaveInvalida) {
        abilityDeJuan = MakeHttpRequests.aWompiCon(BASE_URL, llaveInvalida);
        transaccionBuilder = TransaccionACH.builder()
                .moneda("COP")
                .descripcion("Pago de prueba ACH");
    }

    // =========================================================
    // WHEN — Tasks del actor Juan
    // =========================================================

    @When("Juan intenta crear una transacción ACH con los siguientes datos")
    public void juanIntentaCrearUnaTransaccionACHConLosSiguientesDatos(DataTable dataTable) {
        Map<String, String> datos = dataTable.asMap(String.class, String.class);

        TransaccionACH transaccion = TransaccionACH.builder()
                .monto(Integer.parseInt(datos.getOrDefault("monto", "0")))
                .moneda(datos.getOrDefault("moneda", "COP"))
                .descripcion(datos.getOrDefault("descripcion", "Pago de prueba ACH"))
                .emailCliente(datos.getOrDefault("email_cliente", ""))
                .tipoCuenta(datos.getOrDefault("tipo_cuenta", "CHECKING_ACCOUNT"))
                .numeroCuenta(datos.getOrDefault("numero_cuenta", ""))
                .banco(datos.getOrDefault("banco", ""))
                .titularCuenta(datos.getOrDefault("titular_cuenta", ""))
                .tipoDocTitular(datos.getOrDefault("tipo_doc_titular", "CC"))
                .numDocTitular(datos.getOrDefault("num_doc_titular", ""))
                .build();

        ultimaRespuesta = CrearTransaccionACH.con(transaccion).ejecutarCon(abilityDeJuan);
    }

    @When("Juan intenta crear una transacción ACH con monto {int} y email {string}")
    public void juanIntentaCrearUnaTransaccionACHConMontoYEmail(int monto, String email) {
        TransaccionACH transaccion = TransaccionACH.builder()
                .monto(monto)
                .moneda("COP")
                .descripcion("Pago de prueba ACH")
                .emailCliente(email)
                .tipoCuenta("CHECKING_ACCOUNT")
                .numeroCuenta("1234567890")
                .banco("BANCOLOMBIA")
                .titularCuenta("Juan Pérez")
                .tipoDocTitular("CC")
                .numDocTitular("1000123456")
                .build();

        ultimaRespuesta = CrearTransaccionACH.con(transaccion).ejecutarCon(abilityDeJuan);
    }

    @When("Juan consulta el estado de la transacción {string}")
    public void juanConsultaElEstadoDeLaTransaccion(String transaccionId) {
        String idAConsultar = (transaccionIdCreada != null) ? transaccionIdCreada : transaccionId;
        ultimaRespuesta = ConsultarEstadoTransaccion
                .conId(idAConsultar)
                .ejecutarCon(abilityDeJuan);
    }

    // =========================================================
    // THEN — Questions del actor Juan
    // =========================================================

    @Then("Juan debe ver que la transacción fue creada con código HTTP {int}")
    public void juanDebeVerQuelaTransaccionFueCreadaConCodigoHTTP(int codigoEsperado) {
        assertEquals(codigoEsperado, ultimaRespuesta.getStatusCode(),
                "Código HTTP inesperado. Body: " + ultimaRespuesta.getBody().asString());
    }

    @Then("Juan debe ver el status {string} en la respuesta")
    public void juanDebeVerElStatusEnLaRespuesta(String statusEsperado) {
        String statusActual = ObtenerStatusTransaccion.desde(ultimaRespuesta);
        assertEquals(statusEsperado, statusActual,
                "Status de transacción inesperado");
    }

    @Then("Juan debe ver un {string} de transacción en la respuesta")
    public void juanDebeVerUnIdDeTransaccionEnLaRespuesta(String campo) {
        String valor = ObtenerStatusTransaccion.idDesde(ultimaRespuesta);
        assertNotNull(valor, "El campo '" + campo + "' no debe ser nulo en la respuesta");
        assertFalse(valor.isBlank(), "El campo '" + campo + "' no debe estar vacío");
    }

    @Then("Juan debe ver la referencia de pago generada por WOMPI")
    public void juanDebeVerLaReferenciaDePagoGeneradaPorWompi() {
        String referencia = ObtenerStatusTransaccion.campoDesde(ultimaRespuesta, "reference");
        assertNotNull(referencia, "La referencia de pago no debe ser nula");
    }

    @Then("Juan debe ver el código HTTP {int} en la respuesta")
    public void juanDebeVerElCodigoHTTPEnLaRespuesta(int codigoEsperado) {
        assertEquals(codigoEsperado, ultimaRespuesta.getStatusCode(),
                "Código HTTP inesperado. Body: " + ultimaRespuesta.getBody().asString());
    }

    @Then("Juan debe ver el campo {string} con el valor {string}")
    public void juanDebeVerElCampoConElValor(String campo, String valorEsperado) {
        String valorActual = ObtenerStatusTransaccion.campoDesde(ultimaRespuesta, campo);
        assertEquals(valorEsperado, valorActual,
                "El campo '" + campo + "' tiene un valor inesperado");
    }

    @Then("Juan debe ver que el monto de la transacción es mayor a {int}")
    public void juanDebeVerQueElMontoDelaTransaccionEsMayorA(int minimo) {
        assertTrue(VerificarMonto.desde(ultimaRespuesta) > minimo,
                "El monto debe ser mayor a " + minimo);
    }

    @Then("Juan debe recibir un error con código HTTP {int}")
    public void juanDebeRecibirUnErrorConCodigoHTTP(int codigoEsperado) {
        assertEquals(codigoEsperado, ultimaRespuesta.getStatusCode(),
                "Código de error HTTP inesperado. Body: " + ultimaRespuesta.getBody().asString());
    }

    @Then("Juan debe ver el mensaje de error {string}")
    public void juanDebeVerElMensajeDeError(String mensajeEsperado) {
        String mensajeActual = ObtenerStatusTransaccion.mensajeErrorDesde(ultimaRespuesta);
        assertNotNull(mensajeActual, "La respuesta de error no contiene un mensaje");
        assertTrue(mensajeActual.toLowerCase().contains(mensajeEsperado.toLowerCase()),
                "Mensaje de error inesperado. Recibido: " + mensajeActual);
    }

    @Then("Juan debe ver un mensaje de error relacionado con el formato del email")
    public void juanDebeVerUnMensajeDeErrorRelacionadoConElFormatoDelEmail() {
        String mensaje = ObtenerStatusTransaccion.mensajeErrorDesde(ultimaRespuesta);
        assertNotNull(mensaje, "La respuesta de error no contiene un mensaje");
        assertTrue(
                mensaje.toLowerCase().contains("email") ||
                mensaje.toLowerCase().contains("correo") ||
                mensaje.toLowerCase().contains("customer_email"),
                "El mensaje de error no hace referencia al email. Recibido: " + mensaje
        );
    }

    @Then("Juan no debe ver ningún {string} de transacción en la respuesta")
    public void juanNoDebeVerNingunIdDeTransaccionEnLaRespuesta(String campo) {
        String valor = ObtenerStatusTransaccion.idDesde(ultimaRespuesta);
        assertNull(valor, "El campo '" + campo + "' no debería estar presente en una respuesta de error");
    }
}
