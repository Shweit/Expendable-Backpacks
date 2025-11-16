# Expendable Backpacks

A comprehensive backpack plugin for Paper/Spigot servers featuring eight unique tiers, progressive upgrades, and shared storage capabilities through the Enderpack system.

[![Version](https://img.shields.io/badge/version-1.0.1-blue.svg)](https://github.com/shweit/expendable-backpacks)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21.1-green.svg)](https://www.minecraft.net/)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

---

## Features

- **Eight Backpack Tiers** - Progressive storage from 9 to 54 slots (Dirt, Leather, Copper, Iron, Gold, Diamond, Netherite, Enderpack)
- **Automatic Inventory Persistence** - All backpack contents saved automatically with YAML-based storage
- **Stackable Upgrades** - Upgrade backpacks by surrounding them with materials while preserving contents
- **Enderpack System** - Shared storage across multiple Enderpacks using UUID-based identification
- **Enderpack Cloning** - Create multiple access points to the same inventory
- **Inception Protection** - Prevents nesting backpacks within other backpacks
- **Custom Textures** - Unique player head textures for each tier
- **Interactive GUI** - In-game guide displaying all crafting recipes and upgrade paths
- **UUID-Based Storage** - Each backpack instance tracked with unique identifiers
- **Performance Optimized** - Efficient inventory caching and asynchronous saving

---

## Backpack Tiers

| Tier | Rows | Slots | Material | Color |
|------|------|-------|----------|-------|
| **Dirt Backpack** | 1 | 9 | Dirt | Gray |
| **Leather Backpack** | 1 | 9 | Leather | Gray |
| **Copper Backpack** | 2 | 18 | Copper Ingot | Orange |
| **Iron Backpack** | 3 | 27 | Iron Ingot | White |
| **Gold Backpack** | 4 | 36 | Gold Ingot | Yellow |
| **Diamond Backpack** | 5 | 45 | Diamond | Cyan |
| **Netherite Backpack** | 6 | 54 | Netherite Ingot | Dark Gray |
| **Enderpack** | 3 | 27 | Ender Pearl | Purple |

---

## Crafting Recipes

### Leather Backpack (Starting Tier)
```
L S L
L C L
L L L
```
- **L** = Leather
- **S** = String
- **C** = Chest

### Dirt Backpack (Downgrade)
```
D D D
D B D
D D D
```
- **D** = Dirt
- **B** = Leather Backpack

### Enderpack (Special Crafting)
```
E P E
P C P
E I E
```
- **E** = Ender Eye
- **P** = Ender Pearl
- **C** = Chest
- **I** = Iron Block

### Enderpack Cloning
```
1 Enderpack + 1 Ender Pearl = 2 Enderpacks (same ID!)
```
Both Enderpacks share the same inventory - perfect for multiple access points!

### Upgrades
Surround your backpack with **8x upgrade material** in a crafting table:

- **Leather → Copper**: 8x Copper Ingot
- **Copper → Iron**: 8x Iron Ingot
- **Iron → Gold**: 8x Gold Ingot
- **Gold → Diamond**: 8x Diamond
- **Diamond → Netherite**: Smithing Table (Template + Diamond Backpack + Netherite Ingot)

**Note:** Upgrades preserve backpack contents and UUID identifiers.

---

## Commands

All commands can be abbreviated using `/bp` as an alias for `/backpack`.

| Command | Description | Permission |
|---------|-------------|------------|
| `/backpack` | Open interactive guide GUI | `backpack.use` |
| `/backpack help` | Show help guide | `backpack.use` |
| `/backpack give <player> <tier>` | Give a backpack to a player | `backpack.give` |
| `/backpack open <uuid>` | Open a backpack by UUID | `backpack.openOthers` |
| `/backpack clear <uuid>` | Clear a backpack's contents | `backpack.clear` |
| `/backpack clone <uuid>` | Get a clone of an Enderpack | `backpack.clone` |

---

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `backpack.use` | Use backpack command and open own backpacks | Everyone |
| `backpack.give` | Give backpacks to players | OP |
| `backpack.openOthers` | Open backpacks by UUID | OP |
| `backpack.clear` | Clear backpack contents | OP |
| `backpack.clone` | Clone Enderpacks | OP |
| `backpack.admin` | All admin permissions (includes all above) | OP |

---

## Installation

1. Download the latest `ExpendableBackpacks-1.0.0.jar` from the [Releases](https://github.com/shweit/expendable-backpacks/releases) page
2. Place the JAR file in your server's `plugins/` directory
3. Restart or reload your server
4. Use `/backpack` to access the interactive guide

### Requirements
- Minecraft Version: 1.21.1 or higher
- Server Software: Paper or Spigot
- Java Version: 21 or higher

---

## Usage

### Opening Backpacks
Right-click any backpack item in your inventory to access its contents.

### Viewing the Guide
Execute `/backpack` to open an interactive GUI displaying:
- All available backpack tiers and their specifications
- Crafting recipes with visual pattern representations
- Upgrade progression paths
- Enderpack functionality details

### Creating Your First Backpack
1. Craft a Leather Backpack using Leather, String, and a Chest
2. Right-click the backpack item to open its inventory
3. Store items as needed
4. Upgrade to higher tiers by surrounding with appropriate materials

---

## Enderpack System

The Enderpack provides unique shared storage functionality:

- **Shared Storage**: All Enderpacks with identical UUIDs access the same inventory
- **Cloneable**: Combine one Enderpack with one Ender Pearl to create two Enderpacks sharing the same UUID
- **Multiple Access Points**: Distribute cloned Enderpacks across different locations or players
- **UUID-Based Identification**: Each Enderpack group identified by unique identifier

### Implementation Example
1. Craft an initial Enderpack
2. Clone the Enderpack by crafting it with an Ender Pearl (yields 2 Enderpacks with matching UUID)
3. Distribute copies to different locations (inventory, storage chest, other players)
4. All instances with the same UUID access shared storage

---

## Data Storage

Backpack data is persisted in `plugins/ExpendableBackpacks/backpacks.yml`:
- UUID-based identification for each backpack instance
- Automatic inventory serialization and saving
- Data persistence across server restarts and reloads
- Tab completion support for all registered backpack UUIDs

---

## Configuration

The plugin operates with default settings without additional configuration. Future releases may include:
- Configurable storage capacity per tier
- Customizable crafting recipes
- Texture customization options
- Per-tier permission requirements

---

## Issue Reporting

Report bugs or submit feature requests through the [GitHub Issues](https://github.com/shweit/expendable-backpacks/issues) page.

---

## Changelog

### Version 1.0.0 - Initial Release
- Eight backpack tiers with progressive storage (9-54 slots)
- Enderpack shared storage system
- Enderpack cloning mechanism (1:1 ratio)
- Material-based upgrade system
- Inception protection mechanism
- Interactive GUI guide with recipe visualization
- YAML-based persistent storage
- Optimized inventory caching system

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for complete details.

---

## Contributing

Contributions are welcome. Please ensure all code adheres to the project's style guidelines and passes all quality checks (Checkstyle, SpotBugs) before submitting pull requests.
