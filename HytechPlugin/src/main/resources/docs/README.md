# Hytech Mod Tech Pack

## Overview
This mod adds a first wave of technology-themed content inspired by Minecraft-style tech mods. It focuses on a data-driven progression (ores → bars → components → machines) and keeps all functionality in JSON so it is playable in creative or as a crafting progression.

## Content Added

### Ores and Bars
| Type | Item IDs | Notes |
| --- | --- | --- |
| Ores (items) | Ore_Sulfur, Ore_Copper, Ore_Tin, Ore_Zinc | Dropped by ore blocks |
| Ore blocks | Ore_Sulfur_Stone, Ore_Copper_Stone, Ore_Tin_Stone, Ore_Zinc_Stone | Mining drops ore + stone |
| Bars | Ingredient_Bar_Sulfur, Ingredient_Bar_Copper, Ingredient_Bar_Tin, Ingredient_Bar_Zinc | Smelt ore in Furnace |
| Alloys | Ingredient_Bar_Bronze, Ingredient_Bar_Brass | Workbench recipes |

### Components
| Item ID | Purpose |
| --- | --- |
| Component_Plate_Copper | Base plate for circuits and batteries |
| Component_Plate_Bronze | Mechanical plate |
| Component_Wire_Copper | Conductive wire |
| Component_Gear_Bronze | Mechanical gearing |
| Component_Circuit_Basic | Basic electronics |
| Component_Battery_Sulfur | Simple chemical battery |
| Component_Motor_Basic | Power component |
| Component_Rod_Bronze | Mechanical rod |
| Component_Screw_Bronze | Fasteners |
| Component_Coil_Copper | Coil for motors/energy |
| Component_Capacitor_Basic | Basic energy storage |
| Component_Magnet_Basic | Magnetic component |
| Component_Frame_Machine | Machine frame |

### Blocks
| Block ID | Purpose |
| --- | --- |
| Block_Machine_Casing | Base machine block |
| Block_Cable_Copper | Power cable (propagates signal) |
| Block_Battery_Sulfur | Simple power source (discharges over time) |
| Block_Lamp_Basic | Lamp with On/Off state (lights up when On) |
| Block_Switch_Basic | Switch that toggles nearby lamps |
| Block_Piston_Basic | Simple piston (extends then retracts) |
| Block_Generator_Handcrank | Placeholder generator |
| Block_Crusher_Basic | Placeholder crusher |
| Block_Assembler_Basic | Placeholder assembler |

## Crafting Chain (Summary)
1. Smelt ores into bars in the Furnace.
2. Craft alloys in the Workbench.
3. Craft components in the Workbench.
4. Craft tech blocks in the Workbench.

## Categories
- Items: `Hytech.Items`
- Components: `Hytech.Components`
- Blocks: `Hytech.Blocks`

## Assets
All icons and textures are generated placeholders to keep the pack complete. Replace them with your custom models and textures when ready.

## Behavior Notes
With the plugin enabled, these behaviors are available:
- Switch toggles a local power network.
- Cable propagates power with a small delay per segment.
- Battery provides temporary power and discharges over time.
- Lamp lights up when powered.
- Piston extends when powered, pushes one block, and pulls one block on retract.

Defaults: propagation step delay is 0.08s and battery discharge is 15s.

These are intentionally simple first-pass behaviors and can be upgraded later with true power networks, block movement, or entity-state logic.

## Plugin Build (Optional, For Functional Blocks)
If you want functional behavior, use the `HytechPlugin` project and build a single JAR that includes both code and assets.

1. Install Java 25 (JetBrains Runtime recommended).
2. From `HytechPlugin`, run `.\gradlew.bat build` (Windows).
3. The JAR will be in `HytechPlugin/build/libs`. Copy that JAR into the Hytale `Mods` folder to test.

If Gradle complains about `JAVA_HOME`, point it to a JDK install (not the Hytale JRE).
The bundled Hytale JRE does not include `javac`, so it cannot compile the plugin.

The `HytechPlugin/src/main/resources` folder already contains `Common/`, `Server/`, and `docs/` so the JAR is fully self-contained.
