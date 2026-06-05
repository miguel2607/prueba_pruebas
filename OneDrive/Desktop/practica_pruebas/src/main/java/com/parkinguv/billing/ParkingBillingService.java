package com.parkinguv.billing;

/**
 * Servicio de facturacion para parqueaderos ParkingUV S.A.S.
 *
 * Reglas de negocio:
 * - Los primeros 30 minutos son gratuitos
 * - A partir del minuto 31 se cobra $500 por cada hora o fraccion
 * - El cobro maximo por dia (24 horas) es de $12,000
 * - Los clientes VIP tienen 20% de descuento antes de aplicar el tope diario
 */
public class ParkingBillingService {

    private static final int FREE_PERIOD_MINUTES = 30;
    private static final int HOURLY_RATE = 500;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int DAILY_CAP = 12000;

    /**
     * Calcula el costo de estacionamiento basado en el tiempo y tipo de cliente
     *
     * @param minutes Minutos de estacionamiento (debe ser >= 0)
     * @param isVIP Si el cliente tiene membresia VIP
     * @return Costo en pesos colombianos
     * @throws IllegalArgumentException si los minutos son negativos
     */
    public int calculateCost(int minutes, boolean isVIP) {
        validateMinutes(minutes);

        if (minutes <= FREE_PERIOD_MINUTES) {
            return 0;
        }

        int cost = calculateHourlyCost(minutes);
        return applyDailyCap(cost);
    }

    /**
     * Calcula el costo basado en la tarifa por hora
     *
     * @param minutes Minutos totales de estacionamiento
     * @return Costo calculado por hora o fraccion
     */
    private int calculateHourlyCost(int minutes) {
        int chargeableMinutes = minutes - FREE_PERIOD_MINUTES;
        int hours = (int) Math.ceil((double) chargeableMinutes / MINUTES_PER_HOUR);
        return hours * HOURLY_RATE;
    }

    /**
     * Aplica el tope diario al costo calculado
     *
     * @param cost Costo calculado sin tope
     * @return Costo con tope diario aplicado
     */
    private int applyDailyCap(int cost) {
        return Math.min(cost, DAILY_CAP);
    }

    /**
     * Valida que los minutos sean un valor valido
     *
     * @param minutes Minutos a validar
     * @throws IllegalArgumentException si los minutos son negativos
     */
    private void validateMinutes(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Los minutos no pueden ser negativos");
        }
    }
}

