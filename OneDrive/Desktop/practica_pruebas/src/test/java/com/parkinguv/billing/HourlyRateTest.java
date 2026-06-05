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

    @Test
    public void testCalculateCost_SixtyMinutes_ReturnFiveHundred() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 60;
        boolean isVIP = false;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        assertEquals(500, cost, "El costo para 60 minutos debe ser 500 (30 min gratuitos + 30 min = 1 hora)");
    }

    @Test
    public void testCalculateCost_NinetyMinutes_ReturnFiveHundred() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 90;
        boolean isVIP = false;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        assertEquals(500, cost, "El costo para 90 minutos debe ser 500 (1 hora exacta de cobro)");
    }

    @Test
    public void testCalculateCost_NinetyOneMinutes_ReturnOneThousand() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 91;
        boolean isVIP = false;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        assertEquals(1000, cost, "El costo para 91 minutos debe ser 1000 (fraccion de segunda hora)");
    }

    @Test
    public void testCalculateCost_OneHundredFiftyMinutes_ReturnOneThousand() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 150;
        boolean isVIP = false;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        assertEquals(1000, cost, "El costo para 150 minutos debe ser 1000 (2 horas de cobro)");
    }
}
