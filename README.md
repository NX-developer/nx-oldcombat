# NX Old Combat

A Fabric mod for Minecraft 1.21 that restores the pre-1.9 ("1.8-style") PvP combat system: no attack cooldown, no sweep attack, right-click sword blocking instead of shields, and rebalanced sword/axe damage.

This mod is server-side authoritative. If you install it on a server, vanilla clients will already feel the old combat behavior without installing anything themselves, because the changes affect damage calculation and item use logic that the server controls.

## Features

- **No attack cooldown** — every hit deals full damage regardless of how fast you click, just like in 1.8.
- **No sweep attack** — the AoE "sweeping edge" hit introduced in 1.9 never triggers.
- **Sword blocking** — right-click and hold a sword to block, exactly like the original pre-1.9 mechanic. Vanilla's generic blocking logic (damage reduction, axe-disables-blocking, block sound/particles) applies automatically since it is not hardcoded to shields.
- **Shields disabled** — right-clicking a shield no longer does anything. The item still exists (so banners, loot tables, and other mods referencing it don't break), but it can no longer block.
- **Rebalanced weapon damage** — swords deal more damage, axes deal less, restoring the 1.8 hierarchy where swords were the dominant PvP weapon.

## Requirements

- Minecraft 1.21
- Fabric Loader >= 0.15.0
- Fabric API
- Java 21

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 1.21 and place it in your `mods` folder.
3. Download the latest `nx-oldcombat` jar from the [Releases](../../releases) page and place it in your `mods` folder.
4. For a server-wide effect, install it only on the server — vanilla clients do not need it.

## How it works

The mod is implemented with three Mixin classes. This section is written so that anyone reading the source can understand what each hook does and why, and hopefully spot bugs or edge cases that are worth fixing.

### `PlayerAttackMixin` — cooldown and sweep removal

- Injects into `PlayerEntity#getAttackCooldownProgress` and forces the return value to `1.0F`. Vanilla uses this value to scale outgoing melee damage based on how "charged" the attack cooldown is; forcing it to `1.0F` means every hit is always treated as fully charged, which removes the cooldown penalty entirely.
- Redirects the call to `World#getOtherEntities(...)` inside `PlayerEntity#attack` (the call vanilla uses to find nearby entities to apply sweep damage to) so that it always returns an empty list. This disables sweep attack without touching anything else in the attack method.

**Known limitation:** this does not disable the Sweeping Edge enchantment's ability to be applied via commands/NBT, it only prevents the sweep hit itself from ever triggering.

### `WeaponDamageMixin` — sword/axe damage rebalance

- Injects at the `RETURN` of `LivingEntity#modifyAppliedDamage`, a vanilla hook designed for exactly this purpose (adjusting the final damage value of an incoming hit).
- Reads the weapon used via `DamageSource#getWeaponStack()`. If it's a `SwordItem`, the damage is multiplied by `NX_SWORD_MULTIPLIER` (default `1.3`). If it's an `AxeItem`, it's multiplied by `NX_AXE_MULTIPLIER` (default `0.6`).
- These constants are simple multipliers rather than hardcoded per-material values, so they scale uniformly across wood/stone/iron/gold/diamond/netherite tiers without needing to know each material's exact vanilla damage number.

**Worth exploring:** the multipliers are flat constants and not configurable at runtime yet. A config file (e.g. via Fabric's `ModMenu`/`Cloth Config`, or a simple JSON in the config folder) would be a good contribution if you want server owners to tune the values without recompiling.

### `ItemUseMixin` — sword blocking, shield disabling

- Injects into `Item#getUseAction`, `Item#getMaxUseTime`, and `Item#use`, all declared on the base `Item` class (not on `SwordItem`/`ShieldItem` directly, since those classes don't override these methods themselves — the mixin has to target the class that actually defines the method body).
- For `SwordItem`: returns `UseAction.BLOCK`, a max use time of `72000` ticks, and starts the "using item" state on right-click (`user.setCurrentHand(hand)`), mirroring exactly what `ShieldItem` does natively.
- For `ShieldItem`: forces `UseAction.NONE`, a max use time of `0`, and makes `use()` return `pass` so right-clicking does nothing.
- Because vanilla's `LivingEntity#isBlocking()` simply checks whether the entity is using an item whose `getUseAction()` is `BLOCK`, no extra mixin was needed to make swords actually reduce incoming damage while blocking — that logic already exists in vanilla and now applies to swords automatically.

**Worth exploring:** blocking currently doesn't have its own model/animation distinct from the shield's raised-arm pose (Minecraft doesn't have a "blocking with sword" animation asset built in). Adding a resource pack or a client-side rendering tweak for a more authentic look would be a nice visual contribution.

## Building from source

```bash
git clone https://github.com/NX-developer/nx-oldcombat.git
cd nx-oldcombat
./gradlew build
```

The output jar will be in `build/libs/`.

## Contributing

Issues and pull requests are welcome. If you find a case where the combat behavior doesn't match 1.8 accurately, or you want to make the damage multipliers configurable, feel free to open a PR.

## License

MIT
