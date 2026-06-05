package com.parkinguv.billing.bdd;

import com.parkinguv.billing.ParkingBillingService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParkingBillingSteps {

    private ParkingBillingService billingService;
    private int actualCost;
    private int minutes;
    private boolean isVIP;

    @Given("el sistema de facturación está disponible")
    public void elSistemaDeFacturacionEstaDisponible() {
        billingService = new ParkingBillingService();
    }

    @When("un cliente estaciona por {int} minutos")
    public void unClienteEstacionaPorMinutos(int minutos) {
        this.minutes = minutos;
        this.isVIP = false;
        this.actualCost = billingService.calculateCost(minutos, false);
    }

    @When("un cliente {word} estaciona por {int} minutos")
    public void unClienteTipoEstacionaPorMinutos(String tipoCliente, int minutos) {
        this.minutes = minutos;
        this.isVIP = tipoCliente.equalsIgnoreCase("VIP");
        this.actualCost = billingService.calculateCost(minutos, this.isVIP);
    }

    @Then("el costo debe ser {int} pesos")
    public void elCostoDebeSerPesos(int costoEsperado) {
        assertEquals(costoEsperado, actualCost,
            String.format("El costo para %d minutos (VIP=%s) debe ser %d pero fue %d",
                minutes, isVIP, costoEsperado, actualCost));
    }

    @And("el cliente no debe pagar nada")
    public void elClienteNoDebePagarNada() {
        assertEquals(0, actualCost, "El cliente no debe pagar nada en el periodo gratuito");
    }

    @And("se debe cobrar {int} hora completa")
    public void seDebeCobraHoraCompleta(int horas) {
        assertTrue(actualCost > 0, "Se debe cobrar al menos una hora");
    }

    @And("se deben cobrar {int} horas por la fracción adicional")
    public void seDebenCobrarHorasPorLaFraccionAdicional(int horas) {
        int expectedCost = horas * 500;
        assertEquals(expectedCost, actualCost,
            String.format("Se deben cobrar %d horas (%d pesos)", horas, expectedCost));
    }

    @And("se deben cobrar {int} horas completas")
    public void seDebenCobrarHorasCompletas(int horas) {
        int expectedCost = horas * 500;
        assertEquals(expectedCost, actualCost,
            String.format("Se deben cobrar %d horas completas (%d pesos)", horas, expectedCost));
    }

    @And("se debe aplicar el tope máximo diario")
    public void seDebeAplicarElTopeMaximoDiario() {
        assertEquals(12000, actualCost, "Se debe aplicar el tope diario de 12000 pesos");
    }

    @And("no se debe exceder el tope diario")
    public void noSeDebeExcederElTopeDiario() {
        assertTrue(actualCost <= 12000,
            String.format("El costo no debe exceder 12000 pesos, pero fue %d", actualCost));
    }

    @And("se debe aplicar {int}% de descuento sobre {int} pesos")
    public void seDebeAplicarDescuentoSobrePesos(int porcentajeDescuento, int costoBase) {
        int expectedCost = (int) (costoBase * (1 - porcentajeDescuento / 100.0));
        assertEquals(expectedCost, actualCost,
            String.format("Se debe aplicar %d%% de descuento sobre %d pesos = %d",
                porcentajeDescuento, costoBase, expectedCost));
    }

    @And("el descuento se aplica antes del tope diario")
    public void elDescuentoSeAplicaAntesDelTopeDiario() {
        assertTrue(actualCost < 12000,
            "El descuento VIP debe resultar en un costo menor al tope diario");
    }

    @And("el tope diario se aplica después del descuento VIP")
    public void elTopeDiarioSeAplicaDespuesDelDescuentoVIP() {
        assertEquals(12000, actualCost,
            "El tope diario de 12000 se aplica incluso después del descuento VIP");
    }
}
