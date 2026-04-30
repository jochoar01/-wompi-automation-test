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

## Configuración rápida

1. Clona el repositorio:
```bash
git clone https://github.com/jochoar01/wompi-automation-test.git
cd wompi-automation-test
```

2. Edita `src/test/resources/config/wompi.properties`:
```properties
wompi.baseUrl=https://api.co.uat.wompi.dev/v1
wompi.merchantKey=prv_stagtest_TU_LLAVE_AQUI
```

3. Compila:
```bash
mvn clean compile
```

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
│   ├── ARCHITECTURE.md                    # Documentación del Screenplay Pattern
│   └── wompi-automation-presentation.pptx # Presentación 5 min
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

## Estado actual

### ✅ Completado
- 5 escenarios automatizados (2 happy path + 3 error handling)
- Arquitectura Screenplay completa
- Feature files con Gherkin profesional
- Integración REST Assured
- Presentación 5 minutos

### ⚠️ Limitaciones
- Algunos tests fallan por **token sandbox expirado**
- Se requiere **merchant key válida** de WOMPI
- URL y ambiente pueden variar según configuración

### 🔄 Próximos pasos
- Validar credenciales en ambiente real
- Agregar CI/CD (GitHub Actions/Jenkins)
- Generar reportes Cucumber HTML
- Data-driven testing con más casos

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

## Cómo agregar nuevos escenarios

1. Edita `src/test/resources/features/wompi_transactions.feature`
2. Agrega un nuevo `Scenario:`
3. Usa steps existentes o crea nuevos en `TransaccionesACHSteps.java`
4. Ejecuta `mvn clean test`

Ejemplo:
```gherkin
@nuevo @ach @creacion
Scenario: Juan crea múltiples transacciones en secuencia
  When Juan intenta crear 3 transacciones ACH con montos diferentes
  Then Todas deben retornar status "PENDING_PAYMENT"
  And Todas deben tener IDs únicos
```

---

## Documentación

- [WOMPI API](https://docs.wompi.co/)
- [Cucumber](https://cucumber.io/docs/cucumber/)
- [REST Assured](https://rest-assured.io/)
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
- [Screenplay Pattern](docs/ARCHITECTURE.md)