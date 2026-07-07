# ZonePracOldCombat

1.8 PvP mode enforcement for ZonePractice Pro via OldCombatMechanics

## » About

This addon bridges ZonePractice Pro and OldCombatMechanics. Maps arena ladders to OCM combat modes (`old` / `new`) and applies module overrides per-player at match start. No world restriction headaches, no manual `/ocm mode` commands — players get the right combat mode automatically.

## » Dependencies

- **ZonePractice Pro** — required
- **OldCombatMechanics** — required

## » Installation

1. Install ZonePractice Pro and OldCombatMechanics
2. Drop `ZonePracOldCombat.jar` into your `plugins/` folder
3. Restart the server
4. Edit `plugins/ZonePracOldCombat/config.yml` to map ladders to modes

## » Configuration

**config.yml**

```yaml
# Which ladders use which mode
ladder-modes:
  Nodebuff: old
  BuildUHC: old
  # ...

# Modules to enable for each mode (must match OCM module names)
modules:
  old:
    - "disable-attack-cooldown"
    - "disable-sword-sweep"
    - "old-tool-damage"
    - "sword-blocking"
    - "shield-damage-reduction"
    - "old-golden-apples"
    - "old-player-knockback"
    - "old-player-regen"
    - "old-armour-strength"
    - "old-potion-effects"
    - "old-critical-hits"
    - "disable-attack-sounds"
  new: []
```

## » How it works

- Listens to `MatchStartEvent` and `MatchRoundStartEvent`
- Resolves the ladder name from the match at runtime
- Looks up the configured mode (`old` / `new`) for that ladder
- Applies `setModuleOverridesForPlayer()` with `FORCE_ENABLED` on every module listed under that mode — bypasses OCM world-based modeset restrictions
- Clears all overrides on `MatchEndEvent`

## » Compatibility

- Spigot / Paper 1.20.6 — 1.21
- Java 9+

Enjoy ZonePracOldCombat!
