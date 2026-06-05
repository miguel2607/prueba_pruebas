package com.parkinguv.billing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests para la regla de negocio: Tope Diario
 * El cobro maximo por dia (24 horas = 1440 minutos) es de $12,000
 */
public class DailyCapTest {

    @Test
    public void testCalculateCost_1440Minutes_ReturnTwelveThousand() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 1440; // 24 horas
        boolean isVIP = false;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        assertEquals(12000, cost, "El costo para 1440 minutos (24h) debe ser 12000 (tope diario)");
    }

    @Test
    public void testCalculateCost_2880Minutes_ReturnTwelveThousand() {
        // Arrange
        ParkingBillingService service = new ParkingBillingService();
        int minutes = 2880; // 48 horas
        boolean isVIP = false;

        // Act
        int cost = service.calculateCost(minutes, isVIP);

        // Assert
        assertEquals(12000, cost, "El costo para 2880 minutos (48h) no debe exceder el tope de 12000");
    }
}
