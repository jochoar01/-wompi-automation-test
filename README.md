# WOMPI Automation Test

Automatización de pruebas de integración API para transacciones ACH en la plataforma WOMPI.
Implementado con **Screenplay Pattern** + **BDD Cucumber** + **REST Assured**.

---

## Requisitos

| Herramienta | Versión mínima |
|-------------|----------------|
| Java        | 17 LTS         |
| Maven       | 3.8+           |
| Git         | 2.x            |

---

## Ejecución

```bash
# Todas las pruebas
mvn clean test -Dwompi.merchantKey=prv_test_TU_LLAVE

# Solo happy path
mvn clean test -Dwompi.merchantKey=prv_test_TU_LLAVE -Dcucumber.filter.tags="@exitoso"

# Solo escenarios de error
mvn clean test -Dwompi.merchantKey=prv_test_TU_LLAVE -Dcucumber.filter.tags="@error"
```

---

## Estructura del proyecto

```
-wompi-automation-test/
├── docs/
│   └── ARCHITECTURE.md                    # Documentación del Screenplay Pattern
├── src/
│   └── test/
│       ├── java/
│       │   └── com/wompi/automation/
│       │       ├── abilities/
│       │       │   └── MakeHttpRequests.java          # Ability: conectarse a WOMPI
│       │       ├── models/
│       │       │   ├── TransaccionACH.java             # Builder interno de pruebas
│       │       │   ├── TransactionRequest.java         # Payload de creación (POST)
│       │       │   ├── TransactionResponse.java        # Respuesta exitosa de WOMPI
│       │       │   ├── ErrorResponse.java              # Respuesta de error de WOMPI
│       │       │   └── PaymentMethod.java              # Método de pago ACH
│       │       ├── questions/
│       │       │   ├── ObtenerStatusTransaccion.java   # Question: leer status/id/campos
│       │       │   └── VerificarMonto.java             # Question: validar monto
│       │       ├── runner/
│       │       │   └── TestRunner.java                 # Punto de entrada Maven Surefire
│       │       ├── stepdefinitions/
│       │       │   └── TransaccionesACHSteps.java      # Gherkin → Screenplay
│       │       └── tasks/
│       │           ├── CrearTransaccionACH.java        # Task: POST /transactions
│       │           └── ConsultarEstadoTransaccion.java # Task: GET /transactions/{id}
│       └── resources/
│           ├── config/
│           │   └── wompi.properties                    # URL base y merchant key
│           └── features/
│               └── wompi_transactions.feature          # Escenarios BDD en Gherkin
├── .gitignore
├── pom.xml
└── README.md
```

---

## Patrón Screenplay

| Componente | Clase | Responsabilidad |
|---|---|---|
| **Ability** | `MakeHttpRequests` | Configura la conexión HTTP con WOMPI |
| **Task** | `CrearTransaccionACH` | Ejecuta `POST /transactions` |
| **Task** | `ConsultarEstadoTransaccion` | Ejecuta `GET /transactions/{id}` |
| **Question** | `ObtenerStatusTransaccion` | Lee el resultado de la respuesta |
| **Question** | `VerificarMonto` | Valida el monto en la respuesta |

Ver [ARCHITECTURE.md](docs/ARCHITECTURE.md) para el flujo completo.

---

## Tags disponibles

| Tag | Descripción |
|---|---|
| `@exitoso` | Escenarios happy path |
| `@error` | Escenarios de error y validación |
| `@ach` | Todos los escenarios ACH |
| `@creacion` | Creación de transacciones |
| `@consulta` | Consulta de estado |
| `@monto` | Validación de monto |
| `@email` | Validación de email |
| `@merchant` | Validación de merchant key |

---

## Documentación

- [WOMPI API](https://docs.wompi.co/)
- [Cucumber](https://cucumber.io/docs/cucumber/)
- [REST Assured](https://rest-assured.io/)
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
