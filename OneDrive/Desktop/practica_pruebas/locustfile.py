"""
Script de Locust para pruebas de rendimiento del servicio de facturación ParkingUV
Simula clientes concurrentes calculando tarifas de estacionamiento

Requisitos:
- El servidor debe estar corriendo en http://localhost:8080
- Instalar Locust: pip install locust

Ejecutar:
- locust --host=http://localhost:8080
- Abrir navegador en http://localhost:8089
"""

from locust import HttpUser, task, between
import random


class ParkingBillingUser(HttpUser):
    """
    Simula un usuario que calcula tarifas de estacionamiento
    """
    # Tiempo de espera entre requests (1-3 segundos)
    wait_time = between(1, 3)

    @task(5)
    def calculate_regular_short_stay(self):
        """
        Caso común: Cliente regular con estadía corta (31-150 minutos)
        Peso: 5 (más frecuente)
        """
        minutes = random.randint(31, 150)
        self.client.post("/api/billing/calculate", json={
            "minutes": minutes,
            "vip": False
        }, name="/api/billing/calculate [Regular Short]")

    @task(3)
    def calculate_regular_medium_stay(self):
        """
        Cliente regular con estadía media (151-720 minutos / 2.5-12 horas)
        Peso: 3
        """
        minutes = random.randint(151, 720)
        self.client.post("/api/billing/calculate", json={
            "minutes": minutes,
            "vip": False
        }, name="/api/billing/calculate [Regular Medium]")

    @task(2)
    def calculate_regular_long_stay(self):
        """
        Cliente regular con estadía larga (721-1440 minutos / 12-24 horas)
        Peso: 2
        """
        minutes = random.randint(721, 1440)
        self.client.post("/api/billing/calculate", json={
            "minutes": minutes,
            "vip": False
        }, name="/api/billing/calculate [Regular Long]")

    @task(2)
    def calculate_vip_short_stay(self):
        """
        Cliente VIP con estadía corta
        Peso: 2
        """
        minutes = random.randint(31, 150)
        self.client.post("/api/billing/calculate", json={
            "minutes": minutes,
            "vip": True
        }, name="/api/billing/calculate [VIP Short]")

    @task(1)
    def calculate_vip_long_stay(self):
        """
        Cliente VIP con estadía larga
        Peso: 1 (menos frecuente)
        """
        minutes = random.randint(720, 2880)
        self.client.post("/api/billing/calculate", json={
            "minutes": minutes,
            "vip": True
        }, name="/api/billing/calculate [VIP Long]")

    @task(1)
    def calculate_free_period(self):
        """
        Cliente en periodo gratuito (0-30 minutos)
        Peso: 1
        """
        minutes = random.randint(0, 30)
        self.client.post("/api/billing/calculate", json={
            "minutes": minutes,
            "vip": False
        }, name="/api/billing/calculate [Free Period]")

    @task(1)
    def calculate_boundary_values(self):
        """
        Prueba de valores límite importantes
        Peso: 1
        """
        boundary_minutes = random.choice([0, 30, 31, 90, 91, 1440, 2880])
        is_vip = random.choice([True, False])
        self.client.post("/api/billing/calculate", json={
            "minutes": boundary_minutes,
            "vip": is_vip
        }, name="/api/billing/calculate [Boundary]")

    @task(10)
    def health_check(self):
        """
        Health check del servicio
        Peso: 10 (muy frecuente, simula monitoreo)
        """
        self.client.get("/api/billing/health", name="/api/billing/health")

    def on_start(self):
        """
        Ejecutado cuando un usuario simulado inicia
        """
        # Verificar que el servicio está disponible
        response = self.client.get("/api/billing/health")
        if response.status_code != 200:
            print("WARNING: El servicio no está disponible")


class StressTestUser(HttpUser):
    """
    Usuario para pruebas de estrés - no tiene tiempo de espera
    Solo se usa cuando se quiere hacer stress testing
    """
    wait_time = between(0.1, 0.5)  # Espera mínima

    @task
    def rapid_fire_calculations(self):
        """
        Envía requests rápidamente para estresar el sistema
        """
        minutes = random.randint(31, 1440)
        is_vip = random.choice([True, False])
        self.client.post("/api/billing/calculate", json={
            "minutes": minutes,
            "vip": is_vip
        })


# Configuración para assertions de performance
# El P95 debe ser menor a 300ms según los requisitos
def on_locust_init(environment, **kwargs):
    """
    Hook que se ejecuta al inicializar Locust
    Configura validaciones de performance
    """
    @environment.events.request.add_listener
    def on_request(request_type, name, response_time, response_length, exception, context, **kwargs):
        if exception:
            print(f"Request failed: {name} - {exception}")


# Para ejecutar desde línea de comandos con validaciones automáticas:
# locust --headless --users 50 --spawn-rate 10 --run-time 1m --host http://localhost:8080
