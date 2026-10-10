# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Expendable Backpacks is a Paper plugin for Minecraft 26.2 featuring eight backpack tiers with progressive storage capacity (9-54 slots). The plugin includes a unique Enderpack system for shared storage across multiple backpack instances.

**Core Technologies:**
- Java 25
- Paper API 26.2
- Gradle build system with Shadow plugin
- Google Java Style (enforced via Checkstyle)
- SpotBugs with FindSecBugs for static analysis

## Build Commands

```bash
# Build the plugin (runs tests, checkstyle, spotbugs)
./gradlew build

# Build without running tests
./gradlew assemble

# Run only tests
./gradlew test

# Run only checkstyle
./gradlew checkstyleMain checkstyleTest

# Run only spotbugs
./gradlew spotbugsMain spotbugsTest

# Create release JAR (removes version suffix)
./gradlew release

# Clean build directory
./gradlew clean
```

The final plugin JAR is located at `build/libs/ExpendableBackpacks-<version>.jar` after building.

## Testing

Set up a local test server:
1. Place the built JAR in `test-server/plugins/`
2. Start the Paper server from the `test-server/` directory
3. The plugin data will be stored in `test-server/plugins/ExpendableBackpacks/`

**Note:** There are currently no unit tests in this project. All testing is manual via the test server.

## Code Architecture

### Storage System (UUID-Based)

The plugin uses a **UUID-based storage model** where each backpack instance has a unique identifier stored in its PersistentDataContainer. This is critical for understanding how the system works:

- **BackpackManager** (`storage/BackpackManager.java`) - Central storage controller
  - Maintains in-memory cache of loaded inventories (`Map<UUID, Inventory>`)
  - Handles YAML serialization/deserialization (`backpacks.yml`)
  - Auto-saves backpack contents when modified
  - Handles inventory resizing during upgrades

- **BackpackItem** (`items/BackpackItem.java`) - Item factory and validator
  - Creates backpack items with custom player head textures
  - Stores UUID and tier level in NBT data (PersistentDataContainer)
  - Provides utility methods: `isBackpack()`, `getBackpackUUID()`, `getBackpackTier()`
  - Supports cloning (same UUID) and upgrading (preserves UUID, changes tier)

### Enderpack System

Enderpacks are special backpacks that enable **shared storage** across multiple physical items:

- Multiple Enderpack items can share the same UUID
- When opened, all Enderpacks with the same UUID access the same inventory
- Cloning recipe: 1 Enderpack + 1 Ender Pearl → 2 Enderpacks (same UUID)
- Implementation: `BackpackCraftingListener` detects cloning recipes and preserves UUID

### Tier System

Eight tiers defined in `BackpackTier` enum with progression:
1. Dirt (9 slots) - downgrade tier
2. Leather (9 slots) - starting tier
3. Copper (18 slots)
4. Iron (27 slots)
5. Gold (36 slots)
6. Diamond (45 slots)
7. Netherite (54 slots) - uses smithing table
8. Enderpack (27 slots) - special shared storage

**Upgrade Mechanism:**
- Crafting table upgrades: Surround backpack with 8x upgrade material
- Smithing table: Diamond → Netherite (uses upgrade template)
- UUID is preserved during upgrades
- BackpackManager automatically resizes inventory when tier changes

### Event Listeners

The plugin uses multiple specialized listeners:

- **BackpackInteractionListener** - Handles right-click to open backpacks, auto-saves on close
- **BackpackCraftingListener** - Processes upgrade recipes, manages UUID preservation, handles Enderpack cloning
- **BackpackSmithingListener** - Handles Netherite upgrade via smithing table
- **BackpackProtectionListener** - Prevents backpacks from being placed inside other backpacks (inception protection)
- **BackpackGuideGUI** - Interactive GUI showing all recipes and tier information

### Plugin Lifecycle

**Initialization (`onEnable`):**
1. Initialize BackpackItem factory (static initialization with plugin instance)
2. Create BackpackManager and load all backpacks from YAML
3. Register all crafting recipes via BackpackRecipes
4. Register event listeners
5. Register commands and tab completers

**Shutdown (`onDisable`):**
1. BackpackManager saves all loaded inventories to YAML
2. All backpack data persists across server restarts

## Code Quality Standards

This project enforces **Google Java Style** with strict quality checks:

- **Checkstyle**: Google Java Style with 100-character line limit, no warnings allowed (`maxWarnings = 0`)
- **SpotBugs**: Static analysis with FindSecBugs security plugin
- **Javadoc**: Required for all protected/public classes and methods
- **Naming conventions**:
  - Variables containing "UUID" require `@SuppressWarnings("checkstyle:AbbreviationAsWordInName")`
  - Exposed fields/methods require `@SuppressWarnings("EI_EXPOSE_REP")` or `"EI_EXPOSE_REP2"`

When adding new backpack features:
- Always preserve UUIDs when modifying existing backpacks
- Immediately save to YAML when creating new backpack instances
- Use BackpackItem factory methods (never construct ItemStacks directly)
- Add proper checks in BackpackProtectionListener if introducing new backpack types

## Plugin Configuration

The plugin uses `plugin.yml` for metadata and commands. Version is auto-injected during build via Gradle's `processResources` task:

```yaml
expand( NAME: rootProject.name, VERSION: version, PACKAGE: rootProject.group )
```

Override version at build time: `./gradlew build -Pver=x.y.z`
