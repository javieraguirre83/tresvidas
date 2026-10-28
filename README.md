# 3rd Life + para Minecraft 26.2 (Forge)

Port del mod **3rd Life +** (de supermj767) a **Minecraft Java 26.2 con Forge**.

Solo tienes **3 vidas**. Tu nombre cambia de color según las que te quedan, y al perder la última quedas **eliminado** (modo espectador).

## ⬇️ Descargar

**[Descargar el mod (.jar)](https://github.com/javieraguirre83/tresvidas/releases/download/ultima/3rdLifePlus-1.1.3-forge-26.2.jar)**

1. Descarga el archivo `.jar`.
2. Presiona **Windows + R**, escribe `%appdata%\.minecraft\mods` y dale Enter.
3. Pega ahí el `.jar` y abre Minecraft con el perfil de **Forge 26.2**.

---

## ❤️ Colores de tu nombre

| Color | Vidas |
|---|---|
| 🟢 Verde | 3 vidas |
| 🟡 Amarillo | 2 vidas |
| 🔴 Rojo | 1 vida (¡la última!) |
| ⚫ Gris oscuro | Eliminado (0 vidas) |
| 🟢 Verde oscuro | 3 vidas + 1 dorada |
| 🔵 Azul | 3 vidas + 2 doradas |
| 🟣 Morado | 3 vidas + 3 doradas |

Al morir se gasta primero la **Vida Temporal** (si la tienes activa), luego las **Vidas Doradas** y al final las normales.

---

## ⌨️ Teclas

| Tecla | Qué hace |
|---|---|
| **I** | Mostrar u ocultar los corazones en pantalla |
| **O** | Pasar los corazones a la izquierda o a la derecha |

Puedes cambiarlas en **Opciones → Controles → Asignación de teclas → Varios**.

---

## 🧰 Objetos

| Objeto | Receta | Cómo se usa |
|---|---|---|
| **Life** (Vida) | No se craftea: se obtiene con la Daga Dentada | Clic derecho **sobre un bloque**: +1 vida (máximo 3) |
| **Golden Life** (Vida Dorada) | 4 lingotes de oro en las esquinas, 4 bloques de oro en los lados y 1 diamante al centro | Clic derecho **sobre un bloque**: si tienes menos de 3 vidas, te da 1; si ya tienes 3, te da 1 Vida Dorada (máximo 3) |
| **Temporary Life** (Vida Temporal) | 8 cueros alrededor de 1 diamante | Clic derecho: durante **5 minutos** morir no te quita vida. No se acumula |
| **Jagged Dagger** (Daga Dentada) | 2 lingotes de hierro arriba a la derecha, 1 lingote al centro y 1 palo abajo a la izquierda | Clic derecho: te arrancas una vida y la obtienes como objeto **"Tu nombre's Life"** para regalarla. No funciona con tu última vida ni si tienes Vidas Doradas. Si matas a alguien con ella, suelta su Life |
| **Reviver** (Revividor) | Daga Dentada + Ancla de reaparición (sin forma) | Ver abajo |

### Cómo usar el Revividor

1. Colócalo en el suelo.
2. Dale clic derecho con una **Vida Dorada** para cargarlo (hasta 3 cargas).
3. Un jugador **eliminado** (en espectador) vuela **dentro del bloque** y revive con tantas vidas como cargas tenía.
4. **Agachado + clic derecho**: le quitas una carga y te devuelve la Vida Dorada.

> **Si juegas solo:** deja un Revividor cargado **antes** de que te eliminen, o usa `/resetlives @p` (necesita trucos).

---

## 💻 Comandos

Necesitan **trucos activados**. Si tu mundo no los tiene: **Esc → Abrir en LAN → Permitir trucos: Sí → Iniciar mundo LAN** (hay que repetirlo cada vez que entras al mundo).

`@p` eres tú; también puedes poner el nombre del jugador.

| Comando | Qué hace |
|---|---|
| `/setlives @p 3` | Pone tus vidas en 3 (puedes poner de 0 a 6; de 4 a 6, lo que pasa de 3 son vidas doradas) |
| `/setlives @p 6` | 3 vidas + 3 vidas doradas (el máximo) |
| `/addlives @p 1` | Suma 1 o 2 vidas |
| `/takelives @p 1` | Quita 1 o 2 vidas (no funciona si tienes vidas doradas; usa `/setlives`) |
| `/resetlives @p` | Todo como al inicio: 3 vidas, sin doradas ni temporal. Si estabas eliminado, te regresa al juego |

---

## 🛠️ Cómo se compila

GitHub lo compila automáticamente (pestaña **Actions**): descarga Forge 26.2, mete el código de `src/` y publica el `.jar` en **Releases**.
