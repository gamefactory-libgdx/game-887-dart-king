<function_calls>
<invoke name="Write">
<parameter name="file_path">/home/kaliuzhnyi/asocity/projects/game-887-dart-king/GDD.md</parameter>
<parameter name="content"># GDD — Dart King

**Genre:** Aim-Arcade  
**Platform:** Android (libGDX)  
**Core Mechanic:** Tap to release dart  
**Version:** 1.0

---

## Table of Contents

1. [Overview](#overview)
2. [Screen List](#screen-list)
3. [Screen Flow](#screen-flow)
4. [Core Gameplay Loop](#core-gameplay-loop)
5. [Per-Screen Detail](#per-screen-detail)
6. [Game Objects](#game-objects)
7. [Controls](#controls)
8. [Scoring & Difficulty](#scoring--difficulty)
9. [Asset List](#asset-list)
10. [Visual Style](#visual-style)
11. [Data Persistence](#data-persistence)
12. [Technical Constraints](#technical-constraints)
13. [Out of Scope](#out-of-scope)

---

## Overview

Dart King is a single-player precision arcade game. The player throws darts at a rotating dartboard by tapping the screen. Darts that land remain stuck in the board and rotate with it — if a newly thrown dart collides with a stuck dart, the game ends. The challenge escalates across three pub venues, each with a faster rotation speed. Players earn trophies to unlock cosmetic dart skins.

---

## Screen List

| # | Screen Class | Purpose |
|---|---|---|
| 1 | `MainMenuScreen` | Title, Play, Shop, Leaderboard, Settings |
| 2 | `VenueSelectScreen` | Choose one of three pub venues |
| 3 | `LocalPubScreen` | Gameplay — Venue 1 (slow rotation) |
| 4 | `TournamentScreen` | Gameplay — Venue 2 (medium rotation) |
| 5 | `ChampionsScreen` | Gameplay — Venue 3 (fast rotation) |
| 6 | `GameOverScreen` | Score summary, trophies earned, retry/menu |
| 7 | `LeaderboardScreen` | Per-venue high scores (top 10 each) |
| 8 | `ShopScreen` | Six dart skins — unlock with trophies |
| 9 | `SettingsScreen` | Music/SFX toggles, reset data |
| 10 | `HowToPlayScreen` | Illustrated tutorial |

---

## Screen Flow

```
MainMenuScreen
├── [Play]         → VenueSelectScreen
│   ├── [Local Pub]     → LocalPubScreen ──┐
│   ├── [Tournament]    → TournamentScreen  ├──→ GameOverScreen
│   └── [Champions]     → ChampionsScreen ─┘        ├── [Retry]  → same gameplay screen
│                                                    └── [Menu]   → MainMenuScreen
├── [Shop]         → ShopScreen       → MainMenuScreen
├── [Leaderboard]  → LeaderboardScreen → MainMenuScreen
├── [Settings]     → SettingsScreen   → MainMenuScreen
└── [How to Play]  → HowToPlayScreen  → MainMenuScreen
```

---

## Core Gameplay Loop

1. **Aim** — the dartboard rotates continuously; the player watches its speed and angle.
2. **Tap** — a dart launches from the bottom-center of the screen toward the board.
3. **Land** — the dart sticks at the exact board position it hits; it now rotates with the board.
4. **Avoid** — each subsequent dart must not collide with any stuck dart.
5. **Score** — landing zone determines points; bullseye awards maximum.
6. **Progress** — after every 5 darts landed without collision, rotation speed increases slightly.
7. **Game Over** — a dart collides with a stuck dart → GameOverScreen with final score + trophies earned.

**Trophies earned per session** = `floor(finalScore / 100)`, capped at 50 per session.

---

## Per-Screen Detail

### 1. MainMenuScreen

- Full-screen background matching current venue aesthetic (defaults to pub).
- Centered title logo: **DART KING**.
- Buttons (vertical stack): **Play**, **How to Play**, **Shop**, **Leaderboard**, **Settings**.
- Trophy balance displayed top-right (icon + number).
- Equipping a skin in Shop updates the dart displayed in the menu background art.

### 2. VenueSelectScreen

- Three venue cards in a horizontal row, each showing:
  - Venue illustration thumbnail.
  - Name: *Local Pub* / *Tournament Hall* / *Champions Arena*.
  - Difficulty stars (1 / 2 / 3).
  - Best score for that venue.
- Tap a card to enter the corresponding gameplay screen.
- Back button returns to MainMenuScreen.

### 3–5. Gameplay Screens (LocalPubScreen / TournamentScreen / ChampionsScreen)

All three share the same gameplay logic class (`GameplayStage`); only parameters differ.

**Layout:**
- Background: venue-specific illustration (full-screen).
- Dartboard centered at ~60% screen height.
- Dart launch zone: bottom-center, fixed horizontal position; dart appears here before each throw.
- HUD (top bar):
  - Score (left).
  - Darts thrown / total (right, e.g. "7 thrown").
  - Venue name (center, small text).
- Pause button (top-left corner) → pauses game, shows Resume / Menu overlay.

**Gameplay behavior:**
- Board rotates around its center; direction and base speed are venue-specific.
- After landing a dart, a new dart appears at the launch zone after 0.4 s delay.
- Collision detection: a thrown dart's tip rectangle overlaps any stuck dart's body rectangle → game over.
- Visual feedback:
  - Bullseye hit: brief gold flash + star particle burst.
  - Near-miss (< 5 px from stuck dart): screen edge red flash.
  - Game over: dart shatters, board freezes, red vignette.

### 6. GameOverScreen

- Blurred screenshot of final board state as background.
- "GAME OVER" header.
- Stats panel:
  - Score.
  - Darts landed (out of thrown).
  - Accuracy %.
  - Trophies earned this session.
- If new high score for venue: animated "NEW RECORD!" banner.
- Buttons: **Retry** (same venue), **Menu**.

### 7. LeaderboardScreen

- Tab bar: Local Pub | Tournament | Champions.
- Per tab: ranked list of top-10 scores (rank, score, date).
- Scores stored locally; no online component.
- Empty state: "No scores yet — play to set a record!"

### 8. ShopScreen

- Header: "Dart Shop" + trophy balance.
- 6 skin cards in a 2×3 grid.
- Each card shows:
  - Dart skin preview (sprite).
  - Skin name.
  - Price in trophies (or "FREE" / "EQUIPPED" / "OWNED").
- **Skin #1** (Classic Dart) is always unlocked and free.
- Tapping an owned/free skin → equips it (updates `PREF_SKIN`).
- Tapping a locked skin → if balance ≥ price: confirmation dialog → deduct trophies, set bitmask bit, equip.
- Tapping a locked skin with insufficient trophies → "Not enough trophies!" toast.

### 9. SettingsScreen

- Music toggle (on/off).
- SFX toggle (on/off).
- Reset All Data button → confirmation dialog → clears all SharedPreferences.
- Back button → MainMenuScreen.

### 10. HowToPlayScreen

- Three illustrated slides (swipe or arrow buttons):
  1. "Watch the board rotate" — annotated dartboard diagram.
  2. "Tap to throw" — dart trajectory illustration.
  3. "Don't hit stuck darts!" — collision danger illustration.
- "Got it!" button → MainMenuScreen.

---

## Game Objects

### Dartboard

| Property | Value |
|---|---|
| Shape | Circle, radius ~180 px at 1080p |
| Zones (outer→inner) | Miss zone (0), Single (varies), Triple ring, Single, Double ring, Bullseye outer (25 pts), Bullseye (50 pts) |
| Rotation anchor | Board center |
| Texture | `dartboard.png` (static), stuck darts drawn over it |

**Zone score mapping** (standard darts scoring):
- Numbers 1–20 arranged per regulation layout.
- Double ring: ×2 multiplier.
- Triple ring: ×3 multiplier.
- Outer bull: 25.
- Bullseye: 50 → displayed score 100 (×2 for excitement).

### Dart

| Property | Value |
|---|---|
| Size | ~12 × 80 px at 1080p |
| Launch position | Bottom-center, y = 80 px from bottom |
| Travel path | Straight vertical line toward board center |
| Travel speed | Constant: 1800 px/s |
| Stuck state | Angle locked to board; rotates with it |
| Collision shape | Thin rectangle (tip 8×8 px for hit detection) |

### Stuck Dart Collision Body

Each stuck dart maintains:
- World-space tip position (updated each frame from board rotation).
- Body rectangle (8 × 60 px) rotated to match its angle.

### Particle Effects

- `BullseyeParticle` — gold stars burst from board center on bullseye hit.
- `HitParticle` — small white flash on normal hit.
- `BreakParticle` — dart shatter on game over.

---

## Controls

| Input | Action |
|---|---|
| **Tap anywhere** (during play) | Release dart from launch zone |
| Tap pause button | Pause overlay |
| Tap Resume | Unpause |
| Tap Retry / Menu | Navigate from pause or game over |
| Swipe left/right | Navigate slides in HowToPlayScreen |
| Android Back | Return to previous screen (pauses gameplay if mid-game) |

No drag, swipe, or hold inputs during active gameplay — tap only.

---

## Scoring & Difficulty

### Venues

| Venue | Screen | Base RPM | Speed Increment | Direction Change |
|---|---|---|---|---|
| Local Pub | `LocalPubScreen` | 8 | +0.5 every 5 landed | Never |
| Tournament Hall | `TournamentScreen` | 14 | +1.0 every 5 landed | Every 15 landed |
| Champions Arena | `ChampionsScreen` | 22 | +1.5 every 5 landed | Every 10 landed |

**Direction change**: board smoothly reverses rotation direction (0.5 s ease transition).

### Score Table

| Landing Zone | Points |
|---|---|
| Miss (off board) | 0 |
| Single segment | 1–20 (face value) |
| Double ring | 2–40 |
| Triple ring | 3–60 |
| Outer bull | 25 |
| Bullseye | 100 |

### Trophy Conversion

- `trophies = floor(score / 100)`, min 0, max 50 per session.
- Trophies accumulate in `PREF_TROPHIES_BALANCE`.

---

## Dart Skin Catalog

| # | Name | Description | Price |
|---|---|---|---|
| 1 | **Classic Dart** | Standard steel-tip dart with wooden barrel — the default look. | Free |
| 2 | **Copper Strike** | Burnished copper barrel with orange flight feathers for a warm, vintage feel. | 50 trophies |
| 3 | **Golden Arrow** | Gleaming gold dart with star-shaped flights, fit for a tournament champion. | 200 trophies |
| 4 | **Neon Flash** | Electric-blue barrel with glowing cyan flights that leave a faint light trail. | 500 trophies |
| 5 | **Dragon Fire** | Black barrel engraved with dragon scales; red-to-orange gradient flights. | 1 000 trophies |
| 6 | **Platinum King** | Mirror-polished platinum dart with crown-shaped flights — the ultimate skin. | 2 000 trophies |

---

## Asset List

### Backgrounds

| Filename | Description |
|---|---|
| `bg_main_menu.png` | Warm pub interior, evening lighting, empty dartboard on wall in background |
| `bg_local_pub.png` | Cozy local pub interior, wooden panels, beer taps, dim warm lighting |
| `bg_tournament_hall.png` | Larger hall, crowd silhouettes, banners, brighter stage lighting |
| `bg_champions_arena.png` | Grand arena, spotlights, trophies on display, dramatic dark atmosphere |
| `bg_game_over.png` | (Unused — blurred live screenshot used instead) |

### UI Sprites

| Filename | Description |
|---|---|
| `ui_logo.png` | "DART KING" title treatment, bold stylized text with crown above K |
| `ui_btn_play.png` | Rounded green button with "PLAY" text |
| `ui_btn_shop.png` | Rounded purple button with "SHOP" text |
| `ui_btn_leaderboard.png` | Rounded blue button with "LEADERBOARD" text |
| `ui_btn_settings.png` | Rounded grey button with gear icon |
| `ui_btn_howtoplay.png` | Rounded teal button with question-mark icon |
| `ui_btn_retry.png` | Orange button "RETRY" |
| `ui_btn_menu.png` | Grey button "MENU" |
| `ui_btn_back.png` | Small arrow-left icon button |
| `ui_trophy_icon.png` | Small gold trophy icon used next to balance |
| `ui_star_filled.png` | Filled gold star (difficulty indicator) |
| `ui_star_empty.png` | Empty star outline |
| `ui_pause_icon.png` | Standard pause ▐▐ icon |
| `ui_panel.png` | Semi-transparent dark rounded rectangle (9-patch) |
| `ui_card.png` | Venue / skin card background (9-patch) |
| `ui_tab_active.png` | Active tab highlight for leaderboard tabs (9-patch) |
| `ui_tab_inactive.png` | Inactive tab for leaderboard |
| `ui_new_record.png` | "NEW RECORD!" banner — gold with stars |

### Game Sprites

| Filename | Description |
|---|---|
| `dartboard.png` | 512×512 regulation dartboard top-down view, standard segment colors |
| `dartboard_glow.png` | Subtle outer glow ring overlay, used on bullseye hit |
| `dart_classic.png` | Classic silver/brown dart, 24×160 px, pointing up |
| `dart_copper.png` | Copper-toned dart, orange flights |
| `dart_golden.png` | Gold dart, star-shaped yellow flights |
| `dart_neon.png` | Blue/cyan dart with glow effect baked in |
| `dart_dragon.png` | Dark dart, red-orange gradient flights |
| `dart_platinum.png` | Mirror-silver dart, crown-shaped silver flights |

### Particle / Effect Sprites

| Filename | Description |
|---|---|
| `fx_star.png` | Small 4-point gold star for bullseye burst |
| `fx_hit_flash.png` | Small white circle flash for normal hit |
| `fx_dart_shard.png` | Broken dart fragment (3 pieces: tip, barrel, flight) |
| `fx_smoke_puff.png` | Small grey smoke puff for game-over effect |

### Fonts

| Filename | Usage |
|---|---|
| `font1.ttf` | Display / title — bold, arcade feel (e.g., Skeleboom or ReturnOfGanon) |
| `font2.ttf` | UI body — readable at small sizes (e.g., Orbitron or GoodTiming) |
| `Roboto-Regular.ttf` | Fallback for small labels |

### Audio

| Filename | Type | Description |
|---|---|---|
| `music_menu.ogg` | Music | Upbeat pub ambience / cheerful loop for menus |
| `music_gameplay.ogg` | Music | Tense mid-tempo loop for gameplay |
| `sfx_dart_throw.ogg` | SFX | Whoosh of dart releasing |
| `sfx_dart_hit_board.ogg` | SFX | Satisfying thud of dart hitting board |
| `sfx_bullseye.ogg` | SFX | Crowd cheer + ding for bullseye |
| `sfx_game_over.ogg` | SFX | Dart shatter / fail sound |
| `sfx_ui_tap.ogg` | SFX | Soft click for UI buttons |
| `sfx_buy.ogg` | SFX | Coin chime for trophy purchase |
| `sfx_near_miss.ogg` | SFX | Tense near-miss whoosh |

---

## Visual Style

- **Theme**: British pub dart scene — warm amber/brown tones for Local Pub; deeper greens and golds for Tournament; dramatic dark/spotlight for Champions.
- **Dartboard**: faithful regulation colors (black, cream, red, green) with slight stylization.
- **Darts**: viewed from the side, pointing toward the board; stuck darts rendered at their actual angular position on the board.
- **Typography**: primary font is display/arcade-bold for scores and titles; secondary is clean and legible for stats.
- **Color palette**:
  - Backgrounds: warm mahogany (`#3D1A0A`), pub gold (`#C8962A`), cream (`#F5E6C8`).
  - Bullseye red: `#CC2200`; Bullseye green: `#006622`.
  - UI accent: gold (`#FFD700`), trophy amber (`#FF9800`).
- **Aspect ratio**: designed for 9:16 portrait; board centered with space below for dart launch zone.

---

## Data Persistence

All data stored via libGDX `Preferences` (maps to Android SharedPreferences).

| Key | Type | Default | Description |
|---|---|---|---|
| `PREF_TROPHIES_BALANCE` | int | 0 | Cumulative trophy balance |
| `PREF_OWNED_SKINS` | int | 1 | Bitmask: bit 0 = skin#1 (always 1), bit 1 = skin#2, … bit 5 = skin#6 |
| `PREF_SKIN` | int | 0 | Index (0–5) of currently equipped skin |
| `PREF_MUSIC_ON` | boolean | true | Music enabled |
| `PREF_SFX_ON` | boolean | true | SFX enabled |
| `PREF_HISCORE_LOCAL_PUB` | int | 0 | Best score for Local Pub |
| `PREF_HISCORE_TOURNAMENT` | int | 0 | Best score for Tournament Hall |
| `PREF_HISCORE_CHAMPIONS` | int | 0 | Best score for Champions Arena |
| `PREF_SCORES_LOCAL_PUB` | string | "" | JSON array of top-10 score entries for Local Pub |
| `PREF_SCORES_TOURNAMENT` | string | "" | JSON array of top-10 score entries for Tournament Hall |
| `PREF_SCORES_CHAMPIONS` | string | "" | JSON array of top-10 score entries for Champions Arena |

Score entry JSON shape: `{"score": 1240, "date": "2026-03-23"}`

---

## Technical Constraints

- **Target API**: Android minSdk 21, targetSdk 34.
- **libGDX version**: as defined in project `build.gradle`.
- **Screen resolution**: design at 1080×1920; use `FitViewport` or `ExtendViewport` to adapt.
- **Rotation logic**: dartboard rotation implemented via `sprite.rotate(deltaAngle)` each frame; stuck darts track their angle offset from board reference angle.
- **Collision**: AABB or circle overlap checks run only for the in-flight dart; no inter-stuck-dart collision needed.
- **Frame rate target**: 60 fps; no physics engine required.
- **No networking**: leaderboard is local-only; no online features.
- **Memory**: all assets for one gameplay screen loaded on screen enter; disposed on screen exit.
- **Back button**: Android back handled via `InputProcessor.keyDown(Keys.BACK)` — pauses game, returns to menu from non-game screens.

---

## Out of Scope

- Multiplayer or online leaderboards.
- Daily challenges or missions.
- IAP or real-money purchases.
- Animated cutscenes.
- Achievements (Google Play Games).
- Power-ups or special abilities.
- Unlockable venues (all three always available).
- Dartboard skin variations (only dart skins are collectible).
- Portrait/landscape auto-rotation (portrait only).
</parameter>
</invoke>
</function_calls>

Written to `projects/game-887-dart-king/GDD.md`.

The document covers all required sections:

- **10 screens** with full per-screen detail including layout, interactions, and edge cases
- **Screen flow diagram** showing all navigation paths
- **Core gameplay loop** with tap mechanic, stuck dart rotation, and game-over condition
- **3 venues** with specific RPM values, speed increment rates, and direction change rules
- **6 dart skins** — Classic (free) through Platinum King (2000 trophies), all cosmetic
- **Asset list** with exact filenames and visual descriptions for backgrounds, UI, game sprites, particles, fonts, and audio
- **Full persistence key table** including `PREF_TROPHIES_BALANCE`, `PREF_OWNED_SKINS` (bitmask), `PREF_SKIN`, per-venue high scores, and top-10 score arrays
- **Technical constraints** for collision, viewport, frame rate, and back-button handling