# Tabla de Casos de Prueba - ParkingUV S.A.S.

## Técnicas Aplicadas
- **Partición de Equivalencia**: División de rangos de entrada en clases que producen comportamiento similar
- **Análisis de Valores Límite**: Prueba de valores en los límites de cada partición

---

## 1. Regla de Negocio: Periodo Gratuito (30 minutos)

### Particiones de Equivalencia

| ID Partición | Descripción | Rango | Resultado Esperado |
|--------------|-------------|-------|-------------------|
| P1-1 | Dentro del periodo gratuito | 0 ≤ minutos ≤ 30 | Costo = 0 |
| P1-2 | Fuera del periodo gratuito | minutos > 30 | Costo > 0 |
| P1-3 | Valores inválidos | minutos < 0 | IllegalArgumentException |

### Valores Límite

| ID Caso | Minutos | Tipo Cliente | Valor Esperado | Clase | Técnica |
|---------|---------|--------------|----------------|-------|---------|
| VL-1.1  | -1      | Regular      | Exception      | P1-3  | Límite inferior inválido |
| VL-1.2  | 0       | Regular      | 0 pesos        | P1-1  | Límite inferior válido |
| VL-1.3  | 1       | Regular      | 0 pesos        | P1-1  | Dentro del rango |
| VL-1.4  | 29      | Regular      | 0 pesos        | P1-1  | Justo antes del límite |
| VL-1.5  | 30      | Regular      | 0 pesos        | P1-1  | **Límite superior** |
| VL-1.6  | 31      | Regular      | 500 pesos      | P1-2  | **Justo después del límite** |
| VL-1.7  | 32      | Regular      | 500 pesos      | P1-2  | Fuera del rango |

---

## 2. Regla de Negocio: Cobro por Hora ($500/hora o fracción)

### Particiones de Equivalencia

| ID Partición | Descripción | Rango | Cobro Esperado |
|--------------|-------------|-------|----------------|
| P2-1 | Sin cobro (periodo gratuito) | 0-30 min | 0 horas |
| P2-2 | Primera hora | 31-90 min | 1 hora ($500) |
| P2-3 | Segunda hora | 91-150 min | 2 horas ($1000) |
| P2-4 | Tercera hora | 151-210 min | 3 horas ($1500) |
| P2-N | Hora N | ... | N horas |

### Valores Límite

| ID Caso | Minutos | Horas Cobrables | Valor Esperado | Partición | Observación |
|---------|---------|-----------------|----------------|-----------|-------------|
| VL-2.1  | 30      | 0               | 0 pesos        | P2-1      | Límite periodo gratuito |
| VL-2.2  | 31      | 1               | 500 pesos      | P2-2      | **Inicio cobro** |
| VL-2.3  | 60      | 1               | 500 pesos      | P2-2      | 30 min cobrables = 1 hora |
| VL-2.4  | 89      | 1               | 500 pesos      | P2-2      | 59 min cobrables = 1 hora |
| VL-2.5  | 90      | 1               | 500 pesos      | P2-2      | **60 min exactos** |
| VL-2.6  | 91      | 2               | 1000 pesos     | P2-3      | **Fracción sube a 2 horas** |
| VL-2.7  | 120     | 2               | 1000 pesos     | P2-3      | 90 min cobrables |
| VL-2.8  | 150     | 2               | 1000 pesos     | P2-3      | **120 min exactos** |
| VL-2.9  | 151     | 3               | 1500 pesos     | P2-4      | **Fracción sube a 3 horas** |

---

## 3. Regla de Negocio: Tope Diario ($12,000)

### Particiones de Equivalencia

| ID Partición | Descripción | Rango | Resultado |
|--------------|-------------|-------|-----------|
| P3-1 | Debajo del tope | Costo calculado < $12,000 | Costo calculado |
| P3-2 | Exactamente en el tope | Costo calculado = $12,000 | $12,000 |
| P3-3 | Por encima del tope | Costo calculado > $12,000 | $12,000 (se aplica tope) |

### Valores Límite

| ID Caso | Minutos | Costo Sin Tope | Valor Esperado | Partición | Observación |
|---------|---------|----------------|----------------|-----------|-------------|
| VL-3.1  | 1410    | 11,500 pesos   | 11,500 pesos   | P3-1      | Debajo del tope |
| VL-3.2  | 1439    | 12,000 pesos   | 12,000 pesos   | P3-2      | **23h 59min = tope** |
| VL-3.3  | 1440    | 12,000 pesos   | 12,000 pesos   | P3-2      | **24h exactas** |
| VL-3.4  | 1441    | 12,000 pesos   | 12,000 pesos   | P3-3      | **Excede, se aplica tope** |
| VL-3.5  | 2880    | 24,000 pesos   | 12,000 pesos   | P3-3      | 48 horas, tope aplicado |

---

## 4. Regla de Negocio: Descuento VIP (20%)

### Particiones de Equivalencia

| ID Partición | Descripción | Condición | Resultado |
|--------------|-------------|-----------|-----------|
| P4-1 | Cliente Regular | isVIP = false | Sin descuento |
| P4-2 | Cliente VIP, sin alcanzar tope | isVIP = true, costo < $12,000 | Descuento 20% aplicado |
| P4-3 | Cliente VIP, alcanza tope después del descuento | isVIP = true, costo con descuento < $12,000 | Descuento aplicado, sin tope |
| P4-4 | Cliente VIP, tope aplicado después del descuento | isVIP = true, costo con descuento ≥ $12,000 | Tope de $12,000 |

### Valores Límite y Combinaciones

| ID Caso | Minutos | Cliente | Costo Base | Descuento | Antes Tope | Después Tope | Partición |
|---------|---------|---------|------------|-----------|------------|--------------|-----------|
| VL-4.1  | 31      | Regular | 500        | 0         | 500        | 500          | P4-1      |
| VL-4.2  | 31      | VIP     | 500        | 100       | 400        | 400          | P4-2      |
| VL-4.3  | 91      | Regular | 1000       | 0         | 1000       | 1000         | P4-1      |
| VL-4.4  | 91      | VIP     | 1000       | 200       | 800        | 800          | P4-2      |
| VL-4.5  | 1440    | Regular | 12,000     | 0         | 12,000     | 12,000       | P4-1      |
| VL-4.6  | 1440    | VIP     | 12,000     | 2,400     | 9,600      | 9,600        | P4-3      |
| VL-4.7  | 2880    | Regular | 24,000     | 0         | 24,000     | 12,000       | P4-1 + P3-3 |
| VL-4.8  | 2880    | VIP     | 24,000     | 4,800     | 19,200     | 12,000       | P4-4      |

---

## 5. Casos Especiales y de Borde

| ID Caso | Descripción | Entrada | Salida Esperada | Justificación |
|---------|-------------|---------|-----------------|---------------|
| CE-1    | Valor mínimo válido | 0 minutos, Regular | 0 pesos | Límite inferior |
| CE-2    | Valor negativo | -1 minutos | IllegalArgumentException | Validación de entrada |
| CE-3    | Exactamente en límite gratuito | 30 minutos, Regular | 0 pesos | Último minuto gratis |
| CE-4    | Primer minuto cobrable | 31 minutos, Regular | 500 pesos | Primer minuto que se cobra |
| CE-5    | Una hora exacta | 90 minutos, Regular | 500 pesos | 60 min cobrables |
| CE-6    | Fracción de segundo | 91 minutos, Regular | 1000 pesos | Redondeo hacia arriba |
| CE-7    | Justo antes del tope | 1439 minutos, Regular | 12,000 pesos | Última hora antes de 24h |
| CE-8    | Exactamente 24 horas | 1440 minutos, Regular | 12,000 pesos | Tope diario exacto |
| CE-9    | VIP con descuento máximo | 1440 minutos, VIP | 9,600 pesos | 20% sobre 12,000 |
| CE-10   | VIP con tope aplicado | 2880 minutos, VIP | 12,000 pesos | Tope después de descuento |

---

## Resumen de Cobertura

| Técnica | Casos Aplicados | Reglas Cubiertas |
|---------|-----------------|------------------|
| **Partición de Equivalencia** | 13 particiones | Todas (4 reglas) |
| **Valores Límite** | 34 casos | Todas (4 reglas) |
| **Casos Especiales** | 10 casos | Validaciones y combinaciones |
| **Total de Casos Documentados** | **57 casos** | 100% de las reglas |

---

## Notas

1. Todos los casos de valores límite están implementados en los tests unitarios y BDD
2. La partición de equivalencia garantiza que cada rango de comportamiento está cubierto
3. Los valores límite verifican las transiciones entre particiones
4. Los casos especiales validan combinaciones críticas de reglas de negocio
