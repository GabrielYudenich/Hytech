# CONTEXT

## Project Intent
Hytech is a technology-themed content mod for Hytale. This pass expands the content set with a cohesive material progression, components, and machine blocks while keeping everything data-driven.

## Design Decisions
- Keep most content data-driven in JSON, while enabling optional plugin behavior for interactive blocks.
- Keep new assets in `Common/Resources`, `Common/Icons/ItemsGenerated`, and `Common/BlockTextures` so the mod loads cleanly with asset packs enabled.
- Add a dedicated Hytech creative category with separate tabs for items, components, and blocks.
- Use known base-game models (`Resources/Materials/Ingot.blockymodel` and `Resources/Ores/Ore_Large.blockymodel`) to avoid missing model references.

## Conventions
- Item IDs follow `Ore_*`, `Ingredient_Bar_*`, `Component_*`, and `Block_*` patterns.
- Translation keys are `server.items.<Id>.name` (and mirrored in `items.<Id>.name`) in `Server/Languages/en-US/server.lang`.
- Recipes use `Furnace` for smelting and `Workbench` for crafting.

## Assets
- All PNGs are generated placeholders. You can swap them with custom art at any time.
- Tech block textures live in `Common/BlockTextures` and are referenced by block JSON.

## Next Steps (Optional)
- Expand the power system (multiple sources, chunk updates on placement/break).
- Improve piston movement (multi-block push, unmovable rules).
- Add world generation or loot tables for new ores.
- Replace generated icons/textures with custom art and models.

## Plugin Project
There is a `HytechPlugin` Gradle project that can build a single JAR containing both code and assets.
The assets are copied into `HytechPlugin/src/main/resources` to keep the plugin self-contained.
The plugin currently registers custom interactions for Hytech switch, piston, and battery blocks and includes a basic power network.
