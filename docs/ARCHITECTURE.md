# Arquitectura: Screenplay Pattern en wompi-automation-test

## 1. Introducción

El **Screenplay Pattern** es un patrón de diseño para pruebas de aceptación centrado en el **actor** que interactúa con el sistema. A diferencia del Page Object Model, modela *quién* hace *qué* y *por qué*, resultando en pruebas más legibles y mantenibles.

---

## 2. Componentes

| Componente | Descripción | Ejemplo en este proyecto |
|---|---|---|
| **Actor** | El sujeto que ejecuta las acciones | `Juan` (el comercio) |
| **Ability** | Capacidad técnica que el actor posee | `MakeHttpRequests` — conectarse a WOMPI |
| **Task** | Acción de negocio que el actor realiza | `CrearTransaccionACH`, `ConsultarEstadoTransaccion` |
| **Question** | Pregunta que el actor hace al sistema para verificar un resultado | `ObtenerStatusTransaccion`, `VerificarMonto` |

---

## 3. Flujo de Ejecución

```
Feature (Gherkin)
    └── StepDefinitions       ← traduce lenguaje natural a código
            └── Task          ← ejecuta la acción de negocio
                    └── Ability (MakeHttpRequests)
                            └── API de WOMPI
                                    └── Response
                                            └── Question  ← verifica el resultado
```

---

## 4. Clases y Responsabilidades

```
abilities/
  MakeHttpRequests        Configura RestAssured con baseUrl y merchant key

tasks/
  CrearTransaccionACH     POST /transactions con payload ACH
  ConsultarEstadoTransaccion  GET /transactions/{id}

questions/
  ObtenerStatusTransaccion  Lee status, id y campos de la respuesta
  VerificarMonto            Valida que el monto sea mayor a cero

models/
  TransactionRequest      Payload de creación de transacción
  TransactionResponse     Respuesta exitosa de WOMPI
  ErrorResponse           Respuesta de error de WOMPI
  PaymentMethod           Datos del método de pago ACH
  TransaccionACH          Modelo interno del builder de pruebas

stepdefinitions/
  TransaccionesACHSteps   Conecta cada step Gherkin con Tasks y Questions

runner/
  TestRunner              Punto de entrada para mvn test
```

---

## 5. Flujo End-to-End

```gherkin
Given que Juan puede hacer peticiones HTTP a WOMPI
```
→ Se instancia `MakeHttpRequests` con la merchant key del ambiente.

```gherkin
When Juan intenta crear una transacción ACH con los siguientes datos
```
→ `TransaccionesACHSteps` construye un `TransactionRequest` y lo pasa a `CrearTransaccionACH`, que ejecuta `POST /transactions` usando la `Ability`.

```gherkin
Then Juan debe ver el status "PENDING_PAYMENT" en la respuesta
```
→ `ObtenerStatusTransaccion.desde(response)` extrae el campo `data.status` y JUnit lo valida.

---

## 6. Cómo Agregar Nuevos Escenarios

1. **Escribir el escenario** en `wompi_transactions.feature` usando el actor (`Juan`).
2. **Crear una Task** en `tasks/` si la acción de negocio no existe aún.
3. **Crear una Question** en `questions/` si se necesita una nueva verificación.
4. **Agregar los steps** en `TransaccionesACHSteps.java` anotados con `@Given`, `@When` o `@Then`.
5. **Ejecutar** con `mvn test -Dcucumber.filter.tags="@tu-tag"`.
