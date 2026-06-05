Feature: Sistema de Facturación ParkingUV
  Como gerente de ParkingUV S.A.S.
  Quiero calcular automáticamente el costo de estacionamiento
  Para cobrar correctamente a mis clientes según las reglas de negocio

  Background:
    Given el sistema de facturación está disponible

  Scenario: Periodo gratuito de 30 minutos
    When un cliente estaciona por 30 minutos
    Then el costo debe ser 0 pesos
    And el cliente no debe pagar nada

  Scenario: Cobro mínimo después del periodo gratuito
    When un cliente estaciona por 31 minutos
    Then el costo debe ser 500 pesos
    And se debe cobrar 1 hora completa

  Scenario: Cobro por fracción de hora
    When un cliente estaciona por 91 minutos
    Then el costo debe ser 1000 pesos
    And se deben cobrar 2 horas por la fracción adicional

  Scenario: Cobro por múltiples horas exactas
    When un cliente estaciona por 150 minutos
    Then el costo debe ser 1000 pesos
    And se deben cobrar 2 horas completas

  Scenario: Tope diario de facturación
    When un cliente estaciona por 1440 minutos
    Then el costo debe ser 12000 pesos
    And se debe aplicar el tope máximo diario

  Scenario: Más de 24 horas aplica el mismo tope
    When un cliente estaciona por 2880 minutos
    Then el costo debe ser 12000 pesos
    And no se debe exceder el tope diario

  Scenario: Cliente VIP obtiene descuento en periodo corto
    When un cliente VIP estaciona por 31 minutos
    Then el costo debe ser 400 pesos
    And se debe aplicar 20% de descuento sobre 500 pesos

  Scenario: Cliente VIP obtiene descuento antes del tope
    When un cliente VIP estaciona por 1440 minutos
    Then el costo debe ser 9600 pesos
    And el descuento se aplica antes del tope diario

  Scenario: Cliente VIP con estancia prolongada
    When un cliente VIP estaciona por 2880 minutos
    Then el costo debe ser 12000 pesos
    And el tope diario se aplica después del descuento VIP

  Scenario Outline: Validación de diferentes tiempos de estacionamiento
    When un cliente <tipo_cliente> estaciona por <minutos> minutos
    Then el costo debe ser <costo_esperado> pesos

    Examples:
      | tipo_cliente | minutos | costo_esperado |
      | regular      | 0       | 0              |
      | regular      | 15      | 0              |
      | regular      | 30      | 0              |
      | regular      | 31      | 500            |
      | regular      | 60      | 500            |
      | regular      | 90      | 500            |
      | regular      | 91      | 1000           |
      | regular      | 1440    | 12000          |
      | VIP          | 31      | 400            |
      | VIP          | 91      | 800            |
      | VIP          | 1440    | 9600           |
