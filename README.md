# Tres Vidas — mod para Minecraft Java 26.2 (Forge)

Solo tienes **3 vidas**. Tu nombre cambia de color según las que te quedan:
🟢 3 → 🟡 2 → 🔴 1. Al perder la última quedas **eliminado** (modo espectador).

## Descargar
Ve a la pestaña **Releases** de este repositorio y descarga el archivo `.jar`.
Ponlo en la carpeta `mods` de tu Minecraft con **Forge 26.2**.

> Juega en un mundo de **Supervivencia** normal (no Hardcore) y **sin trucos**,
> para que no puedas salir del modo espectador con `/gamemode`.

## Objetos
| Objeto | Cómo se consigue | Qué hace (clic derecho) |
|---|---|---|
| **Vida** ❤ | Con la Daga Dentada | +1 vida (máximo 3) |
| **Vida Dorada** ★ | 4 lingotes de oro, 4 bloques de oro, 1 diamante | Vida extra que se pierde antes que las normales (máx. 3) |
| **Vida Temporal** ⌛ | 8 cueros alrededor de 1 diamante | Si mueres en los siguientes 5 minutos, no pierdes vida |
| **Daga Dentada** 🗡 | 2 lingotes de hierro en diagonal + 1 palo | Te arrancas una vida y la obtienes como objeto para regalarla. Si matas a alguien con ella, suelta una Vida |
| **Revividor** | Daga Dentada + Ancla de reaparición | Cárgalo con Vidas Doradas (hasta 3). Un jugador eliminado vuela dentro y revive con tantas vidas como cargas tenga |

## Comandos
- `/vidas` — ver tus vidas
- `/vidas ver <jugador>`
- Solo operadores: `/vidas poner|dar|quitar|doradas <jugadores> <n>` y `/vidas reiniciar <jugadores>`

## Cómo se compila
GitHub lo compila solo con cada cambio (pestaña **Actions**): descarga el MDK de Forge 26.2,
mete el código de `src/` y genera el `.jar`.

Inspirado en la serie "3rd Life". Código y texturas originales.
