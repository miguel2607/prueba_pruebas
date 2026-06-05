package com.parkinguv.billing.api;

import com.parkinguv.billing.ParkingBillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el servicio de facturación
 * Este endpoint es utilizado para pruebas de rendimiento con Locust
 */
@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final ParkingBillingService billingService;

    public BillingController() {
        this.billingService = new ParkingBillingService();
    }

    /**
     * Calcula el costo de estacionamiento
     *
     * @param request Objeto con minutos y estado VIP
     * @return Respuesta con el costo calculado
     */
    @PostMapping("/calculate")
    public ResponseEntity<BillingResponse> calculateCost(@RequestBody BillingRequest request) {
        try {
            int cost = billingService.calculateCost(request.getMinutes(), request.isVip());
            return ResponseEntity.ok(new BillingResponse(cost, request.getMinutes(), request.isVip()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint de health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    // Clases internas para request y response

    public static class BillingRequest {
        private int minutes;
        private boolean vip;

        public BillingRequest() {}

        public BillingRequest(int minutes, boolean vip) {
            this.minutes = minutes;
            this.vip = vip;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public boolean isVip() {
            return vip;
        }

        public void setVip(boolean vip) {
            this.vip = vip;
        }
    }

    public static class BillingResponse {
        private int cost;
        private int minutes;
        private boolean vip;

        public BillingResponse() {}

        public BillingResponse(int cost, int minutes, boolean vip) {
            this.cost = cost;
            this.minutes = minutes;
            this.vip = vip;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public boolean isVip() {
            return vip;
        }

        public void setVip(boolean vip) {
            this.vip = vip;
        }
    }
}
