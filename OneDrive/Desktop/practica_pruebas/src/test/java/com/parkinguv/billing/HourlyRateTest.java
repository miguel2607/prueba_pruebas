package com.parkinguv.billing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests para la regla de negocio: Cobro por Hora
 * A partir del minuto 31 se cobra $500 por cada hora o fraccion
 */
public class HourlyRateTest {

    @Test
    public void testCalculateCost_ThirtyOneMinutes_ReturnFiveHundred() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 31;
        boolean isVIP = false;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        assertEquals(500, cost, "El costo para 31 minutos debe ser 500 (1 hora o fraccion)");
    }
}
