# ============================================================
# Feature: Transacciones ACH en WOMPI
# Descripción: Pruebas de integración para el método de pago
#              ACH (Automated Clearing House) / Transferencia
#              Bancaria a través de la API de WOMPI.
# Autor: Equipo QA Automation
# Versión: 1.0.0
# ============================================================

#language: es

Feature: Transacciones ACH en WOMPI
  Como integrador de pagos
  Quiero procesar transacciones ACH a través de la API de WOMPI
  Para que los usuarios puedan realizar transferencias bancarias de forma segura

  Background:
    # Precondición común: autenticación con credenciales válidas de WOMPI
    Given que el sistema WOMPI está disponible en el ambiente de pruebas
    And que se cuenta con una llave privada de comercio válida

  # ============================================================
  # ESCENARIOS DE ÉXITO (Happy Path)
  # ============================================================

  @exitoso @ach @creacion
  Scenario: Crear una transacción ACH válida
    # Flujo principal: creación exitosa de una transacción ACH
    Given que se tienen los datos válidos de una transacción ACH
      | campo              | valor                        |
      | monto              | 150000                       |
      | moneda             | COP                          |
      | descripcion        | Pago de prueba ACH           |
      | email_cliente      | cliente@correo.com           |
      | tipo_cuenta        | CHECKING_ACCOUNT             |
      | numero_cuenta      | 1234567890                   |
      | banco              | BANCOLOMBIA                  |
      | titular_cuenta     | Juan Pérez                   |
      | tipo_doc_titular   | CC                           |
      | num_doc_titular    | 1000123456                   |
    When el comercio envía la solicitud de creación de transacción ACH
    Then la respuesta debe tener código HTTP 201
    And el cuerpo de la respuesta debe contener el campo "id" de la transacción
    And el estado de la transacción debe ser "PENDING"
    And la respuesta debe incluir la referencia de pago generada

  @exitoso @ach @consulta
  Scenario: Consultar el estado de una transacción ACH existente
    # Flujo de consulta: se verifica que se puede obtener el estado de una transacción creada
    Given que existe una transacción ACH con id "txn_wompi_12345"
    When el comercio consulta el estado de la transacción "txn_wompi_12345"
    Then la respuesta debe tener código HTTP 200
    And el cuerpo de la respuesta debe contener el campo "id" con valor "txn_wompi_12345"
    And el cuerpo de la respuesta debe contener el campo "payment_method_type" con valor "ACH"
    And la transacción debe tener un monto mayor a 0

  @exitoso @ach @multiple
  Scenario: Crear múltiples transacciones ACH en secuencia
    # Flujo de volumen: valida que el sistema procesa varias transacciones consecutivas sin errores
    Given que se tienen múltiples solicitudes ACH válidas
      | referencia   | monto   | email                    | banco        | tipo_cuenta      |
      | REF-001      | 100000  | usuario1@correo.com      | BANCOLOMBIA  | CHECKING_ACCOUNT |
      | REF-002      | 250000  | usuario2@correo.com      | DAVIVIENDA   | SAVINGS_ACCOUNT  |
      | REF-003      | 500000  | usuario3@correo.com      | BANCO_BOGOTA | CHECKING_ACCOUNT |
    When el comercio envía cada solicitud ACH en secuencia
    Then cada transacción debe ser creada exitosamente con código HTTP 201
    And cada transacción debe tener un "id" único en la respuesta
    And cada transacción debe tener el estado "PENDING"

  # ============================================================
  # ESCENARIOS DE ERROR (Flujos Alternos)
  # ============================================================

  @error @ach @validacion @monto
  Scenario Outline: Rechazar transacción ACH por monto inválido
    # Validación de negocio: montos en cero o negativos no deben ser procesados
    Given que se prepara una solicitud ACH con monto <monto>
    And el email del cliente es "cliente.valido@correo.com"
    And la llave del comercio es válida
    When el comercio envía la solicitud de creación de transacción ACH
    Then la respuesta debe tener código HTTP <codigo_http>
    And el cuerpo de la respuesta debe contener el campo "error" con valor "<mensaje_error>"
    And la transacción no debe ser creada en el sistema

    Examples:
      # Casos de borde para montos rechazados
      | monto  | codigo_http | mensaje_error                          |
      | 0      | 422         | El monto debe ser mayor a cero         |
      | -1     | 422         | El monto debe ser mayor a cero         |
      | -50000 | 422         | El monto debe ser mayor a cero         |

  @error @ach @validacion @email
  Scenario Outline: Rechazar transacción ACH por email inválido
    # Validación de formato: el email del pagador debe cumplir con el formato RFC 5322
    Given que se prepara una solicitud ACH con monto 100000
    And el email del cliente es "<email_invalido>"
    And la llave del comercio es válida
    When el comercio envía la solicitud de creación de transacción ACH
    Then la respuesta debe tener código HTTP 422
    And el cuerpo de la respuesta debe contener un mensaje de error relacionado con el email
    And la transacción no debe ser creada en el sistema

    Examples:
      # Formatos de email que deben ser rechazados
      | email_invalido         |
      | sinArroba              |
      | @sinusuario.com        |
      | espacios en@medio.com  |
      | doble@@arroba.com      |
      |                        |

  @error @ach @autenticacion @merchant
  Scenario Outline: Rechazar transacción ACH por llave de comercio inválida
    # Seguridad: solicitudes con credenciales incorrectas deben ser denegadas
    Given que se prepara una solicitud ACH con monto 100000
    And el email del cliente es "cliente.valido@correo.com"
    And la llave del comercio es "<llave_invalida>"
    When el comercio envía la solicitud de creación de transacción ACH
    Then la respuesta debe tener código HTTP <codigo_http>
    And el cuerpo de la respuesta debe contener el campo "error" con valor "<mensaje_error>"
    And la transacción no debe ser creada en el sistema

    Examples:
      # Variantes de credenciales inválidas
      | llave_invalida              | codigo_http | mensaje_error                     |
      | llave_incorrecta_123        | 401         | Credenciales de comercio inválidas |
      | prv_test_XXXINVALIDAXXX     | 401         | Credenciales de comercio inválidas |
      |                             | 401         | Credenciales de comercio inválidas |
