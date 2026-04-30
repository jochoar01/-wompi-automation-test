# ============================================================
# Feature: Transacciones ACH en WOMPI
# Descripción: Pruebas de integración para el método de pago
#              ACH (Automated Clearing House) / Transferencia
#              Bancaria a través de la API de WOMPI.
#
# Patrón: Screenplay Pattern
# Actor: Juan (comercio/merchant que interactúa con WOMPI)
# Ability: MakeHttpRequests — conectarse a la API de WOMPI
# Tasks: CrearTransaccionACH, ConsultarEstadoTransaccion
# Questions: ObtenerStatusTransaccion, VerificarMonto
#
# Autor: Equipo QA Automation
# Versión: 2.0.0
# ============================================================

Feature: Transacciones ACH en WOMPI
  Como Juan, un comercio integrado con WOMPI
  Quiero poder crear y consultar transacciones ACH
  Para que mis clientes puedan pagar mediante transferencia bancaria de forma segura

  Background:
    # Ability: Juan recibe la capacidad de conectarse a WOMPI antes de cada escenario
    Given que Juan puede hacer peticiones HTTP a WOMPI

  # ============================================================
  # ESCENARIOS DE ÉXITO (Happy Path)
  # ============================================================

  @exitoso @ach @creacion
  Scenario: Juan crea una transacción ACH exitosamente
    # Task: CrearTransaccionACH
    # Juan ejecuta la tarea de crear una transacción con todos los datos requeridos
    When Juan intenta crear una transacción ACH con los siguientes datos
      | campo            | valor              |
      | monto            | 150000             |
      | moneda           | COP                |
      | descripcion      | Pago de prueba ACH |
      | email_cliente    | cliente@correo.com |
      | tipo_cuenta      | CHECKING_ACCOUNT   |
      | numero_cuenta    | 1234567890         |
      | banco            | BANCOLOMBIA        |
      | titular_cuenta   | Juan Pérez         |
      | tipo_doc_titular | CC                 |
      | num_doc_titular  | 1000123456         |
    # Question: ObtenerStatusTransaccion
    # Juan pregunta al sistema cuál fue el resultado de la tarea ejecutada
    Then Juan debe ver que la transacción fue creada con código HTTP 201
    And Juan debe ver el status "PENDING_PAYMENT" en la respuesta
    And Juan debe ver un "id" de transacción en la respuesta
    And Juan debe ver la referencia de pago generada por WOMPI

  @exitoso @ach @consulta
  Scenario: Juan consulta el estado de una transacción ACH existente
    # Precondición: se crea una transacción real para obtener un ID válido
    Given que Juan ya creó una transacción ACH con id "txn_wompi_12345"
    # Task: ConsultarEstadoTransaccion
    # Juan ejecuta la tarea de consultar el estado actual de la transacción
    When Juan consulta el estado de la transacción "txn_wompi_12345"
    # Question: ObtenerStatusTransaccion + VerificarMonto
    # Juan verifica los detalles retornados por el sistema
    Then Juan debe ver el código HTTP 200 en la respuesta
    And Juan debe ver un "id" de transacción en la respuesta
    And Juan debe ver el campo "payment_method_type" con el valor "PSE"
    And Juan debe ver que el monto de la transacción es mayor a 0

  # ============================================================
  # ESCENARIOS DE ERROR (Flujos Alternos)
  # ============================================================

  @error @ach @validacion @monto
  Scenario Outline: Juan no puede crear una transacción ACH con monto inválido
    # Task: CrearTransaccionACH con datos inválidos
    # Validación de negocio: montos en cero o negativos no son permitidos por WOMPI
    When Juan intenta crear una transacción ACH con monto <monto> y email "cliente@correo.com"
    # Question: ObtenerStatusTransaccion
    # Juan verifica que el sistema rechazó la tarea con el error esperado
    Then Juan debe recibir un error con código HTTP <codigo_http>
    And Juan debe ver el mensaje de error "<mensaje_error>"
    And Juan no debe ver ningún "id" de transacción en la respuesta

    Examples:
      # Casos de borde: montos que WOMPI debe rechazar
      | monto  | codigo_http | mensaje_error       |
      | 0      | 422         | mayor o igual       |
      | -1     | 422         | mayor o igual       |
      | -50000 | 422         | mayor o igual       |

  @error @ach @validacion @email
  Scenario Outline: Juan no puede crear una transacción ACH con email inválido
    # Task: CrearTransaccionACH con email malformado
    # Validación de formato: el email del pagador debe cumplir RFC 5322
    When Juan intenta crear una transacción ACH con monto 100000 y email "<email_invalido>"
    # Question: ObtenerStatusTransaccion
    # Juan verifica que el sistema rechazó la tarea por email inválido
    Then Juan debe recibir un error con código HTTP 422
    And Juan debe ver un mensaje de error relacionado con el formato del email
    And Juan no debe ver ningún "id" de transacción en la respuesta

    Examples:
      # Formatos de email que WOMPI debe rechazar
      | email_invalido        |
      | sinArroba             |
      | @sinusuario.com       |
      | espacios en@medio.com |
      | doble@@arroba.com     |
      |                       |

  @error @ach @autenticacion @merchant
  Scenario Outline: Juan no puede crear una transacción ACH con merchant key inválida
    # Task: CrearTransaccionACH con credenciales incorrectas
    # Seguridad: WOMPI debe denegar cualquier solicitud con llave de comercio no válida
    Given que Juan usa la merchant key "<llave_invalida>"
    When Juan intenta crear una transacción ACH con monto 100000 y email "cliente@correo.com"
    # Question: ObtenerStatusTransaccion
    # Juan verifica que el sistema rechazó la autenticación
    Then Juan debe recibir un error con código HTTP <codigo_http>
    And Juan debe ver el mensaje de error "<mensaje_error>"
    And Juan no debe ver ningún "id" de transacción en la respuesta

    Examples:
      # Variantes de merchant key inválida que WOMPI debe rechazar
      | llave_invalida          | codigo_http | mensaje_error           |
      | llave_incorrecta_123    | 401         | no corresponde          |
      | prv_test_XXXINVALIDAXXX | 401         | no válida               |
      |                         | 401         | llave pública o privada |
