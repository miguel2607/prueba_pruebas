# Tabla de Casos de Prueba - ParkingUV Billing Module

## Analisis de Particion de Equivalencia y Valores Limite

### Regla 1: Periodo Gratuito y Cobro por Hora

**Descripcion:** Los primeros 30 minutos son gratuitos. A partir del minuto 31 se cobra $500 por cada hora o fraccion.

#### Particiones de Equivalencia

| ID | Particion | Rango | Resultado Esperado |
|----|-----------|-------|-------------------|
| PE1 | Periodo gratuito | 0 ≤ minutos ≤ 30 | $0 |
| PE2 | Cobro normal | 31 ≤ minutos < 1440 | $500 * ceil((minutos - 30) / 60) |
| PE3 | Cobro con tope diario | minutos ≥ 1440 | Aplicar tope de $12,000 |

#### Valores Limite

| Caso | Minutos | Tipo | Costo Esperado (sin VIP) | Justificacion |
|------|---------|------|-------------------------|---------------|
| VL1 | 0 | Limite inferior PE1 | $0 | Primer valor valido del periodo gratuito |
| VL2 | 30 | Limite superior PE1 | $0 | Ultimo minuto gratuito |
| VL3 | 31 | Limite inferior PE2 | $500 | Primer minuto con cobro (1 hora o fraccion) |
| VL4 | 60 | Limite dentro PE2 | $500 | Exactamente 30 min de cobro = 1 hora |
| VL5 | 90 | Limite dentro PE2 | $500 | 1 hora completa de cobro |
| VL6 | 91 | Limite dentro PE2 | $1,000 | Inicia segunda hora (fraccion) |
| VL7 | 1439 | Limite superior PE2 | $12,000 | 23h 59min, debe cobrar pero aplicar tope |
| VL8 | 1440 | Limite inferior PE3 | $12,000 | Exactamente 24 horas, tope diario |
| VL9 | 1441 | Dentro PE3 | $12,000 | Mas de 24 horas, aplica tope |

### Regla 2: Descuento por Membresia VIP

**Descripcion:** Los clientes con membresia VIP tienen un 20% de descuento sobre el total antes de aplicar el tope diario.

#### Particiones de Equivalencia

| ID | Particion | Condicion | Resultado Esperado |
|----|-----------|-----------|-------------------|
| PE1 | Cliente regular | isVIP = false | Sin descuento |
| PE2 | Cliente VIP | isVIP = true | 20% descuento antes del tope |

#### Valores Limite

| Caso | Minutos | VIP | Costo Sin Desc. | Desc. 20% | Costo Final | Justificacion |
|------|---------|-----|----------------|-----------|-------------|---------------|
| VL10 | 31 | false | $500 | $0 | $500 | Cliente regular, minimo cobro |
| VL11 | 31 | true | $500 | $100 | $400 | Cliente VIP, minimo cobro |
| VL12 | 90 | false | $500 | $0 | $500 | Cliente regular, 1 hora |
| VL13 | 90 | true | $500 | $100 | $400 | Cliente VIP, 1 hora |
| VL14 | 1440 | false | $14,150 | $0 | $12,000 | Regular, tope aplicado |
| VL15 | 1440 | true | $14,150 | $2,830 | $11,320 | VIP, descuento antes del tope |
| VL16 | 1920 | true | $18,850 | $3,770 | $12,000 | VIP, descuento insuficiente para evitar tope |

### Casos de Prueba Combinados

| ID | Minutos | VIP | Clase Equiv. | Costo Esperado | Descripcion |
|----|---------|-----|--------------|----------------|-------------|
| TC1 | 0 | false | PE1 | $0 | Estacionamiento instantaneo |
| TC2 | 15 | false | PE1 | $0 | Dentro del periodo gratuito |
| TC3 | 30 | false | PE1 | $0 | Limite superior periodo gratuito |
| TC4 | 31 | false | PE2 | $500 | Primer minuto con cobro |
| TC5 | 60 | false | PE2 | $500 | 30 minutos de cobro = 1 hora |
| TC6 | 61 | false | PE2 | $1,000 | Fraccion de segunda hora |
| TC7 | 90 | false | PE2 | $500 | 1 hora exacta de cobro |
| TC8 | 120 | false | PE2 | $1,000 | 1.5 horas de cobro |
| TC9 | 150 | false | PE2 | $1,000 | 2 horas de cobro |
| TC10 | 151 | false | PE2 | $1,500 | Fraccion de tercera hora |
| TC11 | 1440 | false | PE3 | $12,000 | 24 horas, tope diario |
| TC12 | 2880 | false | PE3 | $12,000 | 48 horas, tope diario |
| TC13 | 30 | true | PE1 | $0 | VIP en periodo gratuito |
| TC14 | 31 | true | PE2 | $400 | VIP, primer cobro con descuento |
| TC15 | 90 | true | PE2 | $400 | VIP, 1 hora con descuento |
| TC16 | 1440 | true | PE3 | $11,320 | VIP, 24h con descuento antes de tope |
| TC17 | 1920 | true | PE3 | $12,000 | VIP, descuento pero alcanza tope |

## Notas

- **ceil((minutos - 30) / 60)**: Se redondea hacia arriba para cobrar cualquier fraccion de hora.
- **Tope diario**: Se aplica despues del descuento VIP.
- **24 horas = 1440 minutos**
- **Descuento VIP**: Se calcula sobre el monto antes del tope: `montoFinal = min(monto * 0.8, 12000)`
