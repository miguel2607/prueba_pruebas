package com.parkinguv.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación Spring Boot para el servicio de facturación
 * Esta aplicación expone un API REST para pruebas de rendimiento
 */
@SpringBootApplication
public class ParkingBillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingBillingApplication.class, args);
    }
}
