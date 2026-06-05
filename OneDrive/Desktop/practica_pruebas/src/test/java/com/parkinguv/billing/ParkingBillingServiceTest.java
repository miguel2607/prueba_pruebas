package com.parkinguv.billing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests unitarios para ParkingBillingService usando TDD
 * Ciclo Red-Green-Refactor
 */
public class ParkingBillingServiceTest {

    @Test
    public void testCalculateCost_ZeroMinutes_ReturnZero() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 0;
        boolean isVIP = false;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        assertEquals(0, cost, "El costo para 0 minutos debe ser 0");
    }
}
