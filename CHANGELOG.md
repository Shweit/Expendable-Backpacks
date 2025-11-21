# Changelog

All notable changes to Expendable Backpacks will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2025-11-14

### 🎉 Initial Release

#### ✨ Added
- **8 Backpack Tiers**: Dirt, Leather, Copper, Iron, Gold, Diamond, Netherite, Enderpack
- **Upgrade System**: Surround backpacks with materials to upgrade
  - Leather → Copper → Iron → Gold → Diamond → Netherite progression
  - Items and UUID preserved during upgrades
- **Enderpack System**: Special tier with shared storage functionality
  - All Enderpacks with same ID share the same inventory
  - Cloning recipe: 1 Enderpack + 1 Ender Pearl = 2 Enderpacks (same ID)
  - Stackable clones for easy management
- **Interactive GUI Guide**:
  - Main menu showing all 8 tiers
  - Detail views with visual crafting recipes
  - Click-based navigation with back buttons
  - Sound effects for better UX
- **Crafting Recipes**:
  - Leather Backpack base recipe
  - Dirt Backpack downgrade
  - Enderpack special crafting
  - All upgrade patterns
  - Smithing Table for Netherite upgrade
- **Admin Commands**:
  - `/backpack give <player> <tier>` - Give backpacks
  - `/backpack open <uuid>` - Open any backpack
  - `/backpack clear <uuid>` - Clear backpack contents
  - `/backpack clone <uuid>` - Clone an Enderpack
- **Storage System**:
  - YAML-based persistent storage
  - Unique UUID for each backpack
  - Automatic saving on inventory changes
  - In-memory caching for performance
  - Tab completion for backpack UUIDs
- **Protection Features**:
  - Inception protection (can't put backpacks inside backpacks)
  - Title-based inventory detection
  - Prevents shift-clicking backpacks into backpacks
  - Prevents dragging backpacks into backpacks
- **Custom Textures**: Beautiful player head textures for all 8 tiers
- **Permission System**: 7 permissions (`backpack.use`, `backpack.give`, `backpack.openOthers`, `backpack.clear`, `backpack.clone`, `backpack.admin`)

#### 🔧 Technical Details
- Built for Paper/Spigot 1.21.1+
- Java 21+ required
- No external dependencies
- Event-driven architecture
- PersistentDataContainer for NBT data
- Optimized inventory resizing on upgrades

#### 📚 Documentation
- Comprehensive README.md
- Modrinth description
- In-game interactive guide
- Code comments and JavaDocs

---

## [1.0.1] - 2025-11-15

### 🐛 Fixed
- **Critical**: Fixed duplicate UUID bug where all crafted leather backpacks shared the same inventory (#9)
  - All players' leather backpacks were accessing the same storage
  - Now generates unique UUID for each crafted leather backpack
  - Added pattern detection in PrepareItemCraftEvent handler
- **Auto-save**: Implemented automatic inventory saving to prevent data loss (#8)
  - Backpack inventories now save immediately when closed
  - Prevents item loss during server crashes or unexpected shutdowns

### ✨ Added
- GitHub Actions workflow for automated publishing to Modrinth
- CLAUDE.md documentation for development guidance

### 🔧 Dependencies
- Bumped `com.github.spotbugs:spotbugs-annotations` from 4.9.6 to 4.9.8
- Bumped `com.github.spotbugs` from 6.4.2 to 6.4.5
- Bumped `org.junit.jupiter:junit-jupiter` from 6.0.0 to 6.0.1
- Bumped `org.junit.platform:junit-platform-launcher` from 6.0.0 to 6.0.1

### 🏗️ Build
- Updated `actions/upload-artifact` from v4 to v5 in CI workflows

---

## [1.1.0] - 2025-11-21

### ✨ Added
- **Placeable Backpacks**: Major new feature allowing backpacks to be placed as blocks
  - Place backpacks using Shift + Right-click on any surface
  - Right-click placed backpacks to open their inventory
  - Breaking placed backpacks returns the item with all contents intact
  - UUID and tier data preserved through item → block → item lifecycle
  - Placed backpacks use Player Head blocks with custom textures
  - Works seamlessly with all backpack tiers including Enderpacks
- **Placeable Backpack Protection**: Comprehensive protection system for placed backpacks
  - Explosion protection (Creeper, TNT, etc.)
  - Block explosion protection (Beds, Respawn Anchors)
  - Piston protection (push/pull immunity)
  - Fire and lava immunity
  - All protection events properly handled with high priority
- **Enderpack Shared Storage for Placed Blocks**: Placed Enderpacks share inventory with item counterparts
  - Multiple placed Enderpack blocks with same UUID access shared storage
  - Perfect for creating multiple access points across bases
  - Team storage functionality with placed blocks
- **Starter Backpack Configuration**: Configurable first-join backpack system
  - New `config.yml` with `give-backpack-on-first-join` option (default: false)
  - Gives new players a Leather Backpack on their first server join
  - Customizable welcome message with color code support
  - Drops backpack at player location if inventory is full
  - Only triggers for players who have never joined before
- **New Utility Class**: BackpackBlockUtil for block operations
  - `isBackpackBlock()` - Check if a block is a placed backpack
  - `getBackpackUUIDFromBlock()` - Retrieve UUID from placed blocks
  - `getBackpackTierFromBlock()` - Retrieve tier from placed blocks
  - `setBlockData()` - Store UUID and tier in block PersistentDataContainer
  - Uses same NamespacedKeys as items for consistency

### 🎨 Changed
- **Simplified Log Messages**: Cleaner server console output
  - Removed ASCII box formatting from enable/disable messages
  - More concise and professional log output
  - "Expendable Backpacks has been enabled!" with feature list
  - Single-line disable message
- **Enhanced BackpackInteractionListener**: Updated interaction system
  - Added sneak detection for placement vs opening
  - Normal right-click opens backpack inventory
  - Shift + right-click allows block placement
  - Right-click on placed backpack blocks opens their inventory
  - New `openBackpackBlock()` method for block interaction

### 🔧 Technical Details
- New `PlayerJoinListener` for first-join detection and starter backpack distribution
- New `BackpackBlockListener` with 7 event handlers for placement and protection
- Config system initialized with `saveDefaultConfig()` in main class
- Block data stored in Skull PersistentDataContainer
- All quality checks passing (Checkstyle, SpotBugs)
- Maintains backward compatibility with existing backpacks

### 📚 Documentation
- Updated README.md with placeable backpacks section
- Added configuration documentation
- Updated Enderpack section to mention placed blocks
- Created MOD_DESCRIPTION.md for mod platforms
- Removed version numbers from documentation per project guidelines

---

## [Unreleased]

### 🐛 Known Issues
None currently reported

---

## Version History

- **1.1.0** - Placeable backpacks and starter config (2025-11-21)
- **1.0.1** - Bug fixes and auto-save (2025-11-15)
- **1.0.0** - Initial Release (2025-11-14)

---

**Note**: Please report any bugs or suggestions on our [GitHub Issues](https://github.com/shweit/expendable-backpacks/issues) page!
