# ParkingUV S.A.S. - Módulo de Facturación

[![CI/CD Pipeline](https://github.com/miguel2607/prueba_pruebas/workflows/CI/CD%20Pipeline%20-%20ParkingUV/badge.svg)](https://github.com/miguel2607/prueba_pruebas/actions)

Sistema de facturación para parqueaderos desarrollado con **TDD (Test-Driven Development)**, incluyendo pruebas BDD, análisis de seguridad y pruebas de rendimiento.

## Tabla de Contenidos

- [Descripción del Proyecto](#descripción-del-proyecto)
- [Reglas de Negocio](#reglas-de-negocio)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Instalación](#instalación)
- [Ejecución de Pruebas](#ejecución-de-pruebas)
  - [Tests Unitarios (TDD)](#tests-unitarios-tdd)
  - [Tests BDD (Cucumber)](#tests-bdd-cucumber)
  - [Pruebas de Seguridad](#pruebas-de-seguridad)
  - [Pruebas de Rendimiento](#pruebas-de-rendimiento)
- [Ejecución de la Aplicación](#ejecución-de-la-aplicación)
- [Casos de Prueba](#casos-de-prueba)
- [Pipeline CI/CD](#pipeline-cicd)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Commits y Proceso TDD](#commits-y-proceso-tdd)

---

## Descripción del Proyecto

ParkingUV S.A.S. requiere un módulo de facturación automatizado para calcular el cobro a sus clientes según reglas de negocio específicas. Este proyecto fue construido desde cero usando **TDD (Test-Driven Development)** con ciclos Red-Green-Refactor evidenciados en los commits.

## Reglas de Negocio

1. **Periodo Gratuito**: Los primeros 30 minutos son gratuitos
2. **Cobro por Hora**: A partir del minuto 31 se cobra $500 por cada hora o fracción
3. **Tope Diario**: El cobro máximo por día (24 horas) es de $12,000
4. **Descuento VIP**: Los clientes con membresía VIP tienen un 20% de descuento sobre el total **antes** de aplicar el tope diario

## Tecnologías Utilizadas

- **Java 11**
- **Maven** - Gestión de dependencias
- **JUnit 5** - Tests unitarios
- **Cucumber** - Tests BDD con Gherkin
- **Spring Boot 2.7** - API REST
- **Locust** - Pruebas de rendimiento
- **OWASP Dependency Check** - Análisis de seguridad
- **GitHub Actions** - CI/CD Pipeline

---

## Instalación

### Requisitos Previos

- Java JDK 11 o superior
- Maven 3.6+
- Python 3.7+ (solo para pruebas de rendimiento)
- Git

### Clonar el Repositorio

```bash
git clone https://github.com/miguel2607/prueba_pruebas.git
cd prueba_pruebas
```

### Instalar Dependencias

```bash
mvn clean install
```

---

## Ejecución de Pruebas

### Tests Unitarios (TDD)

Ejecutar todos los tests unitarios:

```bash
mvn test
```

Ejecutar tests excluyendo BDD:

```bash
mvn test -Dtest=!CucumberBDDTest
```

Ejecutar un test específico:

```bash
mvn test -Dtest=VIPDiscountTest
mvn test -Dtest=DailyCapTest
mvn test -Dtest=HourlyRateTest
```

Ver reporte de cobertura:

```bash
mvn verify
# El reporte se genera en: target/site/jacoco/index.html
```

**Resultado Esperado**: 16 tests unitarios pasando ✅

---

### Tests BDD (Cucumber)

Ejecutar tests BDD escritos en Gherkin:

```bash
mvn test -Dtest=CucumberBDDTest
```

Los escenarios están definidos en: `src/test/resources/features/parking_billing.feature`

**Resultado Esperado**: 20 escenarios BDD pasando ✅

**Reporte BDD**:
- HTML: `target/cucumber-reports/cucumber.html`
- JSON: `target/cucumber-reports/cucumber.json`

Los escenarios incluyen:
- ✅ Periodo gratuito de 30 minutos
- ✅ Cobro mínimo después del periodo gratuito
- ✅ Cobro por fracción de hora
- ✅ Tope diario de facturación
- ✅ Descuento VIP
- ✅ Validación de valores límite

---

### Pruebas de Seguridad

Ejecutar análisis de vulnerabilidades OWASP:

```bash
mvn org.owasp:dependency-check-maven:check
```

**Reporte de Seguridad**: `target/dependency-check-report.html`

---

### Pruebas de Rendimiento

Las pruebas de rendimiento validan que el **P95 de respuesta sea menor a 300ms** con 50 usuarios concurrentes.

#### Paso 1: Iniciar el Servidor

```bash
# Compilar la aplicación
mvn clean package -DskipTests

# Iniciar el servidor Spring Boot
java -jar target/parking-billing-1.0-SNAPSHOT.jar
```

El servidor se ejecutará en `http://localhost:8080`

#### Paso 2: Instalar Locust (Python)

```bash
pip install locust
```

#### Paso 3: Ejecutar Pruebas con Locust

**Opción A - Interfaz Web** (Recomendado):

```bash
locust --host=http://localhost:8080
```

Abrir navegador en `http://localhost:8089` y configurar:
- Number of users: 50
- Spawn rate: 10
- Host: http://localhost:8080

**Opción B - Modo Headless**:

```bash
locust --headless \
       --users 50 \
       --spawn-rate 10 \
       --run-time 60s \
       --host http://localhost:8080 \
       --html target/locust-report.html
```

**Endpoints Probados**:
- `POST /api/billing/calculate` - Cálculo de tarifas
- `GET /api/billing/health` - Health check

**Escenarios de Carga**:
- Cliente regular - estadía corta (31-150 min)
- Cliente regular - estadía media (151-720 min)
- Cliente VIP - estadía corta
- Periodo gratuito (0-30 min)
- Valores límite (30, 31, 90, 91, 1440, 2880)

**Resultado Esperado**:
- ✅ 0% de fallos
- ✅ P95 < 300ms
- ✅ Throughput alto

---

## Ejecución de la Aplicación

### Modo Desarrollo

```bash
mvn spring-boot:run
```

### Producción

```bash
# Compilar
mvn clean package

# Ejecutar JAR
java -jar target/parking-billing-1.0-SNAPSHOT.jar
```

### Probar el API

```bash
# Health check
curl http://localhost:8080/api/billing/health

# Calcular costo para cliente regular
curl -X POST http://localhost:8080/api/billing/calculate \
  -H "Content-Type: application/json" \
  -d '{"minutes": 91, "vip": false}'

# Calcular costo para cliente VIP
curl -X POST http://localhost:8080/api/billing/calculate \
  -H "Content-Type: application/json" \
  -d '{"minutes": 1440, "vip": true}'
```

---

## Casos de Prueba

La tabla completa de casos de prueba con **Partición de Equivalencia** y **Análisis de Valores Límite** está en:

📄 **[CASOS_DE_PRUEBA.md](CASOS_DE_PRUEBA.md)**

### Resumen de Cobertura

| Técnica | Casos | Reglas Cubiertas |
|---------|-------|------------------|
| Partición de Equivalencia | 13 particiones | 4 reglas |
| Valores Límite | 34 casos | 4 reglas |
| Casos Especiales | 10 casos | Validaciones |
| **TOTAL** | **57 casos** | **100%** |

---

## Pipeline CI/CD

El proyecto utiliza **GitHub Actions** para automatizar todas las pruebas en cada push.

### Jobs del Pipeline

1. **Tests Unitarios** ✅
   - Ejecuta todos los tests TDD
   - Genera reporte de cobertura

2. **Tests BDD** ✅
   - Ejecuta escenarios Cucumber
   - Genera reporte HTML

3. **Análisis de Seguridad** 🔒
   - OWASP Dependency Check
   - Detecta vulnerabilidades

4. **Build** 📦
   - Compila la aplicación
   - Genera artefacto JAR

5. **Pruebas de Rendimiento** ⚡ (solo en `main`/`master`)
   - Ejecuta Locust con 50 usuarios
   - Valida P95 < 300ms

### Ver el Pipeline

👉 [GitHub Actions](https://github.com/miguel2607/prueba_pruebas/actions)

---

## Estructura del Proyecto

```
parking-billing/
├── .github/
│   └── workflows/
│       └── ci.yml                      # Pipeline GitHub Actions
├── src/
│   ├── main/
│   │   ├── java/com/parkinguv/billing/
│   │   │   ├── ParkingBillingService.java   # Lógica de negocio
│   │   │   ├── ParkingBillingApplication.java  # Spring Boot App
│   │   │   └── api/
│   │   │       └── BillingController.java    # REST Controller
│   │   └── resources/
│   │       └── application.properties        # Configuración
│   └── test/
│       ├── java/com/parkinguv/billing/
│       │   ├── ParkingBillingServiceTest.java  # Tests básicos
│       │   ├── HourlyRateTest.java             # Tests cobro/hora
│       │   ├── DailyCapTest.java               # Tests tope diario
│       │   ├── VIPDiscountTest.java            # Tests descuento VIP
│       │   └── bdd/
│       │       ├── CucumberBDDTest.java        # Runner Cucumber
│       │       └── ParkingBillingSteps.java    # Step Definitions
│       └── resources/
│           └── features/
│               └── parking_billing.feature     # Escenarios Gherkin
├── locustfile.py                       # Script pruebas rendimiento
├── CASOS_DE_PRUEBA.md                  # Tabla de casos de prueba
├── pom.xml                             # Dependencias Maven
└── README.md                           # Este archivo
```

---

## Commits y Proceso TDD

Este proyecto fue desarrollado usando **TDD estricto** con ciclos Red-Green-Refactor:

### Commits del Proceso (19+ commits)

```
10a9a83 - First commit (inicial)
71c240b - test: add failing test for zero minutes parking (RED)
7cf899a - implementacion: agregar servicio de facturacion con calculo de costo cero (GREEN)
d1a3a46 - refactor: agregar constantes, validaciones y documentacion (REFACTOR)

09b79f8 - test: agregar test fallido para cobro por hora (RED)
b638d17 - implementacion: agregar logica de cobro por hora (GREEN)
c5762db - refactor: extraer metodo calculateHourlyCost (REFACTOR)

664ad8d - test: agregar test fallido para tope diario (RED)
f3bd1e3 - implementacion: aplicar tope diario usando Math.min (GREEN)
0841a9e - refactor: extraer metodo applyDailyCap (REFACTOR)

2f1311d - test: agregar test fallido para descuento VIP (RED)
9e86167 - implementacion: corregir valores esperados en test VIP (GREEN)
a76d6f0 - refactor: agregar tests adicionales para cobertura VIP (REFACTOR)

c9e1f46 - feat: agregar pruebas BDD con Cucumber y Gherkin
6bda2dd - docs: agregar tabla de casos de prueba
1f6f15f - feat: agregar API REST con Spring Boot y script de Locust
b08d362 - ci: agregar pipeline de GitHub Actions
```

**Total**: Más de 19 commits evidenciando el proceso completo ✅

---

## Autor

**Miguel Rodriguez**
- GitHub: [@miguel2607](https://github.com/miguel2607)
- Repositorio: [prueba_pruebas](https://github.com/miguel2607/prueba_pruebas)

---

## Licencia

Este proyecto es de uso académico para demostración de TDD, BDD y pruebas de calidad de software.

---

## Comandos Rápidos

```bash
# Instalar todo
mvn clean install

# Solo tests unitarios
mvn test -Dtest=!CucumberBDDTest

# Solo tests BDD
mvn test -Dtest=CucumberBDDTest

# Seguridad
mvn org.owasp:dependency-check-maven:check

# Iniciar servidor
mvn spring-boot:run

# Pruebas de rendimiento (en otra terminal)
locust --host=http://localhost:8080

# Build completo
mvn clean package

# Ejecutar JAR
java -jar target/parking-billing-1.0-SNAPSHOT.jar
```

---

**¡Pipeline en Verde! ✅**
