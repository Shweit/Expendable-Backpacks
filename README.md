# Expendable Backpacks

A comprehensive backpack plugin for Paper-compatible servers featuring eight unique tiers, progressive upgrades, and shared storage capabilities through the Enderpack system.

[![Minecraft](https://img.shields.io/badge/minecraft-26.2-green.svg)](https://www.minecraft.net/)
[![License](https://img.shields.io/badge/license-%20%20GNU%20GPLv3%20-yellow?style=plastic)](LICENSE)

---

## Features

- **Eight Backpack Tiers** - Progressive storage from 9 to 54 slots (Dirt, Leather, Copper, Iron, Gold, Diamond, Netherite, Enderpack)
- **Placeable Backpacks** - Place backpacks as blocks in the world using Shift + Right-click
- **Automatic Inventory Persistence** - All backpack contents saved automatically with YAML-based storage
- **Stackable Upgrades** - Upgrade backpacks by surrounding them with materials while preserving contents
- **Enderpack System** - Shared storage across multiple Enderpacks using UUID-based identification
- **Enderpack Cloning** - Create multiple access points to the same inventory
- **Starter Backpack Configuration** - Give new players a Leather Backpack on first join
- **Inception Protection** - Prevents nesting backpacks within other backpacks
- **Full Protection** - Placed backpacks protected from explosions, pistons, fire, and lava
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

1. Download the latest JAR file from the [Releases](https://github.com/shweit/expendable-backpacks/releases) page
2. Place the JAR file in your server's `plugins/` directory
3. Restart or reload your server
4. Use `/backpack` to access the interactive guide

### Requirements
- Minecraft Version: 26.2
- Server Software: Paper, Purpur, or Folia
- Java Version: 25 or higher

---

## Usage

### Opening Backpacks
- **In Hand**: Right-click any backpack item in your inventory to access its contents
- **Placed Block**: Right-click a placed backpack block to open its inventory

### Placing Backpacks
- **Place**: Hold a backpack and **Shift + Right-click** on any surface to place it as a block
- **Break**: Mine the placed backpack to retrieve it with all contents intact
- **Protection**: Placed backpacks are immune to explosions, pistons, fire, and lava

### Viewing the Guide
Execute `/backpack` to open an interactive GUI displaying:
- All available backpack tiers and their specifications
- Crafting recipes with visual pattern representations
- Upgrade progression paths
- Enderpack functionality details

### Creating Your First Backpack
1. Craft a Leather Backpack using Leather, String, and a Chest (or receive one on first join if configured)
2. Right-click the backpack item to open its inventory
3. Store items as needed
4. Optionally place it in your base using Shift + Right-click
5. Upgrade to higher tiers by surrounding with appropriate materials

---

## Enderpack System

The Enderpack provides unique shared storage functionality:

- **Shared Storage**: All Enderpacks with identical UUIDs access the same inventory (items and placed blocks)
- **Cloneable**: Combine one Enderpack with one Ender Pearl to create two Enderpacks sharing the same UUID
- **Multiple Access Points**: Distribute cloned Enderpacks across different locations or players
- **Works When Placed**: Placed Enderpack blocks share the same inventory as their item counterparts
- **UUID-Based Identification**: Each Enderpack group identified by unique identifier

### Implementation Example
1. Craft an initial Enderpack
2. Clone the Enderpack by crafting it with an Ender Pearl (yields 2 Enderpacks with matching UUID)
3. Keep one in your inventory for on-the-go access
4. Place one as a block at your base using Shift + Right-click
5. Give one to a teammate or store in a chest
6. All instances with the same UUID access shared storage - even the placed block!

---

## Data Storage

Backpack data is persisted in `plugins/ExpendableBackpacks/backpacks.yml`:
- UUID-based identification for each backpack instance
- Automatic inventory serialization and saving
- Data persistence across server restarts and reloads
- Tab completion support for all registered backpack UUIDs

---

## Configuration

The plugin's configuration file is located at `plugins/ExpendableBackpacks/config.yml`.

### Available Options

```yaml
# Give a Leather Backpack to players when they first join the server
# Default: false
give-backpack-on-first-join: false

# Message sent to players when they receive their starter backpack
# Use & for color codes (e.g., &a for green, &6 for gold)
# Set to empty string ("") to disable the message
starter-backpack-message: "&7Welcome! You've been given a &7Leather Backpack &7to get started. Right-click to open!"
```

### Starter Backpack Feature
Enable `give-backpack-on-first-join: true` to automatically give new players a Leather Backpack when they join for the first time. This feature:
- Only triggers for players who have never joined before
- Adds the backpack to the player's inventory
- Drops the backpack at the player's location if inventory is full
- Sends a customizable welcome message with color code support

---

## Issue Reporting

Report bugs or submit feature requests through the [GitHub Issues](https://github.com/shweit/expendable-backpacks/issues) page.

---

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for detailed version history.

### Latest Features
- Placeable backpacks as blocks (Shift + Right-click)
- Full protection for placed backpacks (explosions, pistons, fire, lava)
- Configurable starter backpack for new players
- Eight backpack tiers with progressive storage (9-54 slots)
- Enderpack shared storage system
- Enderpack cloning mechanism
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
