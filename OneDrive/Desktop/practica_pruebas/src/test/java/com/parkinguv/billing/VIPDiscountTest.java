package com.parkinguv.billing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests para la regla de negocio: Descuento VIP
 * Los clientes VIP tienen 20% de descuento sobre el total antes de aplicar el tope diario
 */
public class VIPDiscountTest {

    @Test
    public void testCalculateCost_ThirtyOneMinutesVIP_ReturnFourHundred() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 31;
        boolean isVIP = true;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        assertEquals(400, cost, "El costo para 31 minutos VIP debe ser 400 (500 - 20%)");
    }

    @Test
    public void testCalculateCost_1440MinutesVIP_ReturnNineThousandSixHundred() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 1440; // 24 horas
        boolean isVIP = true;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        // Sin descuento seria 12000, con descuento 20%: 12000 * 0.8 = 9600
        // El descuento se aplica antes del tope diario
        assertEquals(9600, cost, "El costo para 1440 minutos VIP debe ser 9600 (descuento antes del tope)");
    }

    @Test
    public void testCalculateCost_NinetyOneMinutesVIP_ReturnEightHundred() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 91;
        boolean isVIP = true;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        // Sin descuento: 1000, con descuento 20%: 1000 * 0.8 = 800
        assertEquals(800, cost, "El costo para 91 minutos VIP debe ser 800 (1000 - 20%)");
    }

    @Test
    public void testCalculateCost_ThirtyMinutesVIP_ReturnZero() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 30;
        boolean isVIP = true;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        assertEquals(0, cost, "Los primeros 30 minutos son gratuitos incluso para VIP");
    }

    @Test
    public void testCalculateCost_2880MinutesVIP_ReturnNineThousandSixHundred() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 2880; // 48 horas
        boolean isVIP = true;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        // Sin descuento seria mas de 12000, pero con tope: 12000
        // Con descuento VIP primero: 24000 * 0.8 = 19200
        // Luego tope: min(19200, 12000) = 12000
        assertEquals(12000, cost, "El tope diario de 12000 se aplica despues del descuento VIP");
    }
}
