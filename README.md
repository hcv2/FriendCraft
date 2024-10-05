# FriendCraft

A Minecraft NeoForge mod that enables summoning AI player entities through chat commands. These entities can serve as teammates in combat, assistants for automatic resource gathering, explorers to guide players to new areas, and possess learnable behaviors.

## Features

- **AI Player Entities** - Summon AI companions that look and behave like real players
- **Command System** - Full control via `/aiplayer` commands (spawn, despawn, follow, stop, mine)
- **Behavior State Machine** - AI supports IDLE, FOLLOWING, EXPLORING, FIGHTING, and MINING states
- **Multi-AI Configuration** - Configure multiple AI roles with different names, personalities, and memory paths
- **Learning System** - AI adjusts behavior weights based on player feedback over time
- **Dual Memory Storage** - Memory can be stored globally (shared across saves) or bound to individual saves

## Requirements

- **Minecraft**: 1.20.4
- **NeoForge**: 20.4.233 or later
- **Java**: 17+

## Installation

1. Install [NeoForge 1.20.4](https://neoforged.net/) for your Minecraft client/server
2. Download the latest `FriendCraft` release JAR
3. Place the JAR file in your `mods/` directory
4. Launch Minecraft with the NeoForge profile

## Configuration

### Configuration File

The mod generates a configuration file at `config/friendcraft.json` on first launch. This file defines all available AI roles and can be manually edited at any time (changes take effect on next server start).

**Default configuration format:**

```json
{
  "bot1": {
    "name": "Assistant",
    "memory_path": "./memory/bot1/",
    "personality": "assistant"
  },
  "bot2": {
    "name": "Friend",
    "memory_path": "./memory/bot2/",
    "personality": "friend"
  }
}
```

### Configuration Fields

| Field | Type | Description |
|-------|------|-------------|
| `bot_id` | string | Unique identifier for the AI role (used as the key in the JSON object) |
| `name` | string | Display name shown in-game for this AI |
| `memory_path` | string | Storage path for AI memory data. See [Memory Storage](#memory-storage) below |
| `personality` | string | Personality type (e.g., `assistant`, `friend`, `guard`, etc.) |

### Memory Storage

The `memory_path` field determines where AI memory data is stored:

- **Global storage** (path starts with `./memory/`): Memory is stored in `config/FriendCraft/memory/<bot_id>/` and persists across all saves
- **Save-bound storage**: Memory is stored in `<save_folder>/FriendCraft/memory/<bot_id>/` and is tied to the specific save

Each AI role gets its own independent memory directory containing `memory.json` (behavior weights, experiences, and knowledge base).

## Commands

All commands require operator permission level 2 or higher.

| Command | Description |
|---------|-------------|
| `/aiplayer spawn <bot_id>` | Spawn an AI player using the specified bot configuration |
| `/aiplayer despawn <name>` | Remove an AI player by its display name |
| `/aiplayer follow <name>` | Make the AI player follow you |
| `/aiplayer stop <name>` | Make the AI player stop its current behavior |
| `/aiplayer mine <name> <block>` | Make the AI player mine the specified block type |

### Examples

```
/aiplayer spawn bot1          # Spawns "Assistant" (from bot1 config)
/aiplayer follow Assistant    # Assistant starts following you
/aiplayer stop Assistant      # Assistant stops and returns to idle
/aiplayer despawn Assistant   # Removes Assistant from the world
/aiplayer mine Assistant coal # Assistant starts mining coal
```

## Building from Source

```bash
# Clone the repository
git clone https://github.com/hcv2/FriendCraft.git
cd FriendCraft

# Build the mod
./gradlew build

# The built JAR will be in build/libs/
```

## Precautions

- **Backup your worlds** before installing or updating the mod
- **Do not edit `memory.json` files** while the server is running — data may be overwritten on save
- **Configuration changes** to `friendcraft.json` require a server restart to take effect
- **AI entities are server-side** — they will not appear in single-player LAN worlds without a server running
- **Memory path changes** in configuration will create new memory directories; existing memory data will not be automatically migrated
- **Command permissions** — only players with permission level 2+ (operators) can use `/aiplayer` commands
- **Performance** — each AI player entity consumes server resources similar to a real player; avoid spawning too many simultaneously

## License

- **Code**: LGPL-3.0
- **Assets**: All Rights Reserved
