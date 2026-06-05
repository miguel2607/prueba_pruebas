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
    public void testCalculateCost_1440MinutesVIP_ReturnElevenThousandThreeHundredTwenty() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 1440; // 24 horas
        boolean isVIP = true;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        // Sin descuento seria 14150, con descuento 11320
        // Como el descuento se aplica primero: 14150 * 0.8 = 11320
        assertEquals(11320, cost, "El costo para 1440 minutos VIP debe ser 11320 (descuento antes del tope)");
    }
}
