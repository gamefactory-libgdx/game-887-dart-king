<function_calls>
<invoke name="Write">
<parameter name="file_path">/home/kaliuzhnyi/asocity/projects/game-887-dart-king/GAME_SPEC.md</parameter>
<parameter name="content"># GAME_SPEC — Dart King

## 1. Game Identity

| Field | Value |
|---|---|
| Title | Dart King |
| Java package | `com.factory.template` |
| Main class | `MainGame` (extends `Game`) |
| Orientation | Portrait only (9:16) |
| Design resolution | 1080 × 1920 |
| Viewport type | `FitViewport(1080, 1920)` on all screens |
| Target FPS | 60 |
| minSdk | 21 |
| targetSdk | 34 |

---

## 2. Screen Inventory

| # | Class | extends | Purpose | Transitions |
|---|---|---|---|---|
| 1 | `MainMenuScreen` | `ScreenAdapter` | Title + nav hub | → `VenueSelectScreen`, `ShopScreen`, `LeaderboardScreen`, `SettingsScreen`, `HowToPlayScreen` |
| 2 | `VenueSelectScreen` | `ScreenAdapter` | Pick venue | → `GameplayScreen(venue)` (3 venue constants); ← `MainMenuScreen` |
| 3 | `GameplayScreen` | `ScreenAdapter` | All gameplay logic, parameterized by `VenueConfig` | → `GameOverScreen(result)` |
| 4 | `GameOverScreen` | `ScreenAdapter` | Score summary, trophy award | → `GameplayScreen(same venue)` (Retry), → `MainMenuScreen` |
| 5 | `LeaderboardScreen` | `ScreenAdapter` | Per-venue top-10 local scores | ← `MainMenuScreen` |
| 6 | `ShopScreen` | `ScreenAdapter` | 6 dart skins, trophy-gated | ← `MainMenuScreen` |
| 7 | `SettingsScreen` | `ScreenAdapter` | Music/SFX toggles, reset | ← `MainMenuScreen` |
| 8 | `HowToPlayScreen` | `ScreenAdapter` | 3 tutorial slides | ← `MainMenuScreen` |

> **Note:** `VenueSelectScreen` passes a `VenueConfig` enum value to `GameplayScreen`; there is no separate `LocalPubScreen`, `TournamentScreen`, or `ChampionsScreen` class — one `GameplayScreen` handles all three.

---

## 3. Screen Flow

```
MainMenuScreen
├── [Play]         → VenueSelectScreen
│   ├── [Local Pub]     → GameplayScreen(VENUE_LOCAL_PUB)  ─┐
│   ├── [Tournament]    → GameplayScreen(VENUE_TOURNAMENT)   ├→ GameOverScreen
│   └── [Champions]     → GameplayScreen(VENUE_CHAMPIONS)  ─┘      ├── [Retry] → GameplayScreen(same)
│                                                                    └── [Menu]  → MainMenuScreen
├── [Shop]         → ShopScreen       → MainMenuScreen
├── [Leaderboard]  → LeaderboardScreen → MainMenuScreen
├── [Settings]     → SettingsScreen   → MainMenuScreen
└── [How to Play]  → HowToPlayScreen  → MainMenuScreen
```

Android Back key behavior:
- In `GameplayScreen` (playing): pause overlay; second Back → `MainMenuScreen`.
- In all other non-menu screens: → `MainMenuScreen`.
- In `MainMenuScreen`: default Android behavior (exit app prompt or ignore).

---

## 4. Game Objects

### 4.1 `VenueConfig` (enum)

```java
enum VenueConfig {
    VENUE_LOCAL_PUB,
    VENUE_TOURNAMENT,
    VENUE_CHAMPIONS
}
```

Fields per constant (see Constants):

| Constant | baseRpm | speedIncrement | incrementEvery | directionChangeEvery | bgTexture | prefHiScore | prefScores |
|---|---|---|---|---|---|---|---|
| `VENUE_LOCAL_PUB` | 8f | 0.5f | 5 | never (0) | `bg_local_pub` | `PREF_HISCORE_LOCAL_PUB` | `PREF_SCORES_LOCAL_PUB` |
| `VENUE_TOURNAMENT` | 14f | 1.0f | 5 | 15 | `bg_tournament_hall` | `PREF_HISCORE_TOURNAMENT` | `PREF_SCORES_TOURNAMENT` |
| `VENUE_CHAMPIONS` | 22f | 1.5f | 5 | 10 | `bg_champions_arena` | `PREF_HISCORE_CHAMPIONS` | `PREF_SCORES_CHAMPIONS` |

### 4.2 `Dartboard`

Fields:
```
float centerX, centerY          // world coords of board center
float radius                    // BOARD_RADIUS (180 px)
float currentAngle              // degrees, updated each frame
float currentRpm                // current rotation speed (may be negative)
float targetRpm                 // used during direction-change easing
boolean isEasing                // true while reversing direction (0.5 s)
float easeTimer
int dartsLanded                 // count of stuck darts
Texture texture                 // dartboard.png
Texture glowTexture             // dartboard_glow.png
List<StuckDart> stuckDarts
```

Methods:
```
void update(float delta)
    // rotate currentAngle by (currentRpm / 60) * 360 * delta degrees
    // if isEasing: lerp currentRpm toward targetRpm over DIRECTION_EASE_DURATION
    // update all StuckDart world positions
    // check speedIncrement threshold
void draw(SpriteBatch batch)
    // draw texture centered on (centerX, centerY) at currentAngle
    // draw each StuckDart
int scoreForImpact(float worldX, float worldY)
    // convert world hit point to board-local polar coords
    // return points per zone table (see §6.1)
void addStuckDart(StuckDart dart)
void triggerGlow(float duration)  // plays dartboard_glow overlay briefly
```

### 4.3 `StuckDart`

Fields:
```
float boardAngleOffset     // angle offset from board reference angle at moment of impact
float boardRadiusOffset    // radial distance from board center at impact
int skinIndex              // which dart texture
Texture texture            // dart_*.png resolved from skinIndex
float worldX, worldY       // tip position in world space (updated each frame)
float worldAngle           // current world rotation angle
Rectangle bodyRect         // 8×60 px collision rect, world-aligned
```

Methods:
```
void updateWorldTransform(float boardCenterX, float boardCenterY, float boardAngle)
    // recompute worldX, worldY, worldAngle from boardAngleOffset + boardAngle
    // rebuild bodyRect
void draw(SpriteBatch batch)
```

### 4.4 `FlyingDart`

Fields:
```
float x, y                 // tip position
float speed                // DART_TRAVEL_SPEED (1800 px/s)
int skinIndex
Texture texture
boolean active
Rectangle tipRect          // 8×8 px collision rect at tip
```

Methods:
```
void launch(float startX, float startY)
    // set active = true, position to launch coords
void update(float delta)
    // move y += speed * delta
    // update tipRect
boolean checkCollisionWithBoard(Dartboard board)
    // return true if tipRect center within BOARD_RADIUS of board center
boolean checkCollisionWithStuck(List<StuckDart> stuck)
    // return true if tipRect overlaps any StuckDart.bodyRect
void draw(SpriteBatch batch)
```

### 4.5 `GameplayStage`

Central controller used by `GameplayScreen`.

Fields:
```
VenueConfig venue
Dartboard dartboard
FlyingDart flyingDart
int score
int dartsThrown
int dartsLanded
boolean gameOver
boolean paused
float postLandDelay        // countdown after landing before next dart appears
int equippedSkin           // read from PREF_SKIN at stage creation
ParticleSystem particles
```

Methods:
```
void init(VenueConfig venue)
void update(float delta)
    // if paused or gameOver: skip
    // update dartboard
    // if flyingDart.active: update dart, check collisions
    //   collision with stuck dart → triggerGameOver()
    //   hit board center area → landDart(), addScore()
    //   off screen top without hit → dart missed, schedule next dart
    // if !flyingDart.active && postLandDelay <= 0: spawn new dart
void landDart(float impactX, float impactY)
    // compute boardAngleOffset from current board angle
    // create StuckDart, add to dartboard
    // compute score for zone
    // increment dartsLanded, check speed-up threshold
    // check direction-change threshold
    // start postLandDelay timer = POST_LAND_DELAY
void triggerGameOver()
    // set gameOver = true
    // play fx_dart_shard particles
    // play sfx_game_over
    // freeze board (stop rotating)
    // red vignette overlay fade-in
GameResult buildResult()
    // returns GameResult { score, dartsThrown, dartsLanded, venue, isNewHiScore }
void pause() / resume()
```

### 4.6 `GameResult` (plain data class)

```java
class GameResult {
    VenueConfig venue;
    int score;
    int dartsThrown;
    int dartsLanded;
    boolean isNewHiScore;
    int trophiesEarned;   // floor(score / 100), capped at 50
}
```

### 4.7 `ScoreEntry` (plain data class)

```java
class ScoreEntry {
    int score;
    String date;   // "yyyy-MM-dd"
}
```

Serialized to/from JSON string via `Json` (libGDX built-in) for persistence.

### 4.8 `ParticleSystem`

Lightweight manual particle system (no libGDX `.p` files needed).

Methods:
```
void spawnBullseye(float x, float y)   // gold stars burst, fx_star.png
void spawnHit(float x, float y)        // white flash, fx_hit_flash.png
void spawnBreak(float x, float y)      // dart shards, fx_dart_shard.png + fx_smoke_puff.png
void update(float delta)
void draw(SpriteBatch batch)
```

### 4.9 `SaveData` (static utility)

Wraps all `Preferences` reads/writes. One method per key. Uses preference file name `"dart_king"`.

---

## 5. Asset Filenames

All assets live under `assets/` in the project root.

### Backgrounds (`assets/backgrounds/`)
```
bg_main_menu.png
bg_local_pub.png
bg_tournament_hall.png
bg_champions_arena.png
```

### UI Sprites (`assets/sprites/ui/`)
```
ui_logo.png
ui_btn_play.png
ui_btn_shop.png
ui_btn_leaderboard.png
ui_btn_settings.png
ui_btn_howtoplay.png
ui_btn_retry.png
ui_btn_menu.png
ui_btn_back.png
ui_trophy_icon.png
ui_star_filled.png
ui_star_empty.png
ui_pause_icon.png
ui_panel.png            (9-patch: ui_panel.9.png)
ui_card.png             (9-patch: ui_card.9.png)
ui_tab_active.png       (9-patch: ui_tab_active.9.png)
ui_tab_inactive.png     (9-patch: ui_tab_inactive.9.png)
ui_new_record.png
```

### Game Sprites (`assets/sprites/`)
```
dartboard.png           (512×512)
dartboard_glow.png      (512×512 overlay)
dart_classic.png        (24×160)
dart_copper.png
dart_golden.png
dart_neon.png
dart_dragon.png
dart_platinum.png
```

### Particle / Effect Sprites (`assets/sprites/`)
```
fx_star.png
fx_hit_flash.png
fx_dart_shard.png
fx_smoke_puff.png
```

### Fonts (`assets/fonts/`)
```
font1.ttf
font2.ttf
Roboto-Regular.ttf
```

### Audio (`assets/sounds/`)
```
music_menu.ogg
music_gameplay.ogg
sfx_dart_throw.ogg
sfx_dart_hit_board.ogg
sfx_bullseye.ogg
sfx_game_over.ogg
sfx_ui_tap.ogg
sfx_buy.ogg
sfx_near_miss.ogg
```

---

## 6. Constants (`Constants.java`)

```java
public class Constants {

    // Viewport
    public static final float WORLD_WIDTH  = 1080f;
    public static final float WORLD_HEIGHT = 1920f;

    // Dartboard geometry
    public static final float BOARD_RADIUS          = 180f;   // px at 1080p
    public static final float BOARD_CENTER_X        = 540f;
    public static final float BOARD_CENTER_Y        = 1140f;  // ~60% of 1920

    // Zone radii (inner edge of each ring, px, at 1080p, proportional to 180 px outer radius)
    // Standard darts board proportions scaled to BOARD_RADIUS = 180 px
    public static final float ZONE_BULLSEYE_RADIUS      = 14f;   // inner bull (50 pts → 100)
    public static final float ZONE_OUTER_BULL_RADIUS    = 32f;   // outer bull (25 pts)
    public static final float ZONE_TRIPLE_INNER_RADIUS  = 110f;  // inner edge of triple ring
    public static final float ZONE_TRIPLE_OUTER_RADIUS  = 126f;  // outer edge of triple ring
    public static final float ZONE_DOUBLE_INNER_RADIUS  = 160f;  // inner edge of double ring
    public static final float ZONE_DOUBLE_OUTER_RADIUS  = 180f;  // outer edge = board edge

    // Dart geometry
    public static final float DART_WIDTH            = 12f;
    public static final float DART_HEIGHT           = 80f;
    public static final float DART_TIP_SIZE         = 8f;
    public static final float DART_BODY_WIDTH       = 8f;
    public static final float DART_BODY_HEIGHT      = 60f;
    public static final float DART_TRAVEL_SPEED     = 1800f;  // px/s
    public static final float DART_LAUNCH_X         = 540f;   // center of screen
    public static final float DART_LAUNCH_Y         = 80f;    // px from bottom

    // Gameplay timing
    public static final float POST_LAND_DELAY       = 0.4f;   // seconds between land and next dart
    public static final float DIRECTION_EASE_DURATION = 0.5f; // seconds to reverse rotation

    // Scoring
    public static final int   BULLSEYE_SCORE        = 100;    // displayed (50 × 2 for excitement)
    public static final int   OUTER_BULL_SCORE      = 25;
    public static final int   TROPHIES_PER_100_SCORE = 1;     // floor(score/100)
    public static final int   MAX_TROPHIES_PER_SESSION = 50;

    // Near-miss threshold
    public static final float NEAR_MISS_DISTANCE    = 5f;     // px from stuck dart body

    // Shop
    public static final int   SKIN_COUNT            = 6;
    public static final int[] SKIN_PRICES           = { 0, 50, 200, 500, 1000, 2000 };

    // Leaderboard
    public static final int   LEADERBOARD_MAX_ENTRIES = 10;

    // Font sizes
    public static final int   FONT_SIZE_TITLE       = 96;
    public static final int   FONT_SIZE_HEADING     = 64;
    public static final int   FONT_SIZE_BODY        = 36;
    public static final int   FONT_SIZE_SMALL       = 24;

    // Venue: Local Pub
    public static final float VENUE_PUB_BASE_RPM     = 8f;
    public static final float VENUE_PUB_SPEED_INC    = 0.5f;
    public static final int   VENUE_PUB_INC_EVERY    = 5;
    public static final int   VENUE_PUB_DIR_CHANGE   = 0;    // never

    // Venue: Tournament
    public static final float VENUE_TOUR_BASE_RPM    = 14f;
    public static final float VENUE_TOUR_SPEED_INC   = 1.0f;
    public static final int   VENUE_TOUR_INC_EVERY   = 5;
    public static final int   VENUE_TOUR_DIR_CHANGE  = 15;

    // Venue: Champions
    public static final float VENUE_CHAMP_BASE_RPM   = 22f;
    public static final float VENUE_CHAMP_SPEED_INC  = 1.5f;
    public static final int   VENUE_CHAMP_INC_EVERY  = 5;
    public static final int   VENUE_CHAMP_DIR_CHANGE = 10;

    // Preferences file
    public static final String PREFS_NAME = "dart_king";
}
```

### 6.1 Zone Score Mapping

Scoring is computed by `Dartboard.scoreForImpact()` using two lookups:

1. **Radial zone** — determined by distance from board center:
   - `r <= ZONE_BULLSEYE_RADIUS` → **Bullseye** (100 pts)
   - `r <= ZONE_OUTER_BULL_RADIUS` → **Outer bull** (25 pts)
   - `r <= ZONE_TRIPLE_INNER_RADIUS` → **Single segment** (1–20, face value)
   - `r <= ZONE_TRIPLE_OUTER_RADIUS` → **Triple ring** (face value × 3)
   - `r <= ZONE_DOUBLE_INNER_RADIUS` → **Single segment** (face value)
   - `r <= ZONE_DOUBLE_OUTER_RADIUS` → **Double ring** (face value × 2)
   - `r > ZONE_DOUBLE_OUTER_RADIUS` → **Miss** (0 pts)

2. **Angular segment** — the board-local angle selects one of 20 numbered segments (1–20) in the standard regulation dartboard clockwise order starting from top:
   `[20, 1, 18, 4, 13, 6, 10, 15, 2, 17, 3, 19, 7, 16, 8, 11, 14, 9, 12, 5]`

---

## 7. Data Persistence

All keys accessed through `SaveData` utility class. Preferences file name: `"dart_king"`.

| Key | Type | Default | Description |
|---|---|---|---|
| `PREF_TROPHIES_BALANCE` | int | 0 | Cumulative trophy balance |
| `PREF_OWNED_SKINS` | int | 1 | Bitmask; bit N = skin index N owned (bit 0 always 1) |
| `PREF_SKIN` | int | 0 | Currently equipped skin index (0–5) |
| `PREF_MUSIC_ON` | boolean | true | Music enabled |
| `PREF_SFX_ON` | boolean | true | SFX enabled |
| `PREF_HISCORE_LOCAL_PUB` | int | 0 | Best score, Local Pub |
| `PREF_HISCORE_TOURNAMENT` | int | 0 | Best score, Tournament Hall |
| `PREF_HISCORE_CHAMPIONS` | int | 0 | Best score, Champions Arena |
| `PREF_SCORES_LOCAL_PUB` | String | `""` | JSON array of `ScoreEntry`, max 10 |
| `PREF_SCORES_TOURNAMENT` | String | `""` | JSON array of `ScoreEntry`, max 10 |
| `PREF_SCORES_CHAMPIONS` | String | `""` | JSON array of `ScoreEntry`, max 10 |

`ScoreEntry` JSON shape: `{"score":1240,"date":"2026-03-23"}`

**Score list update rule:** deserialize array, append new entry, sort descending by score, truncate to 10, serialize back.

**Reset All Data** in `SettingsScreen`: calls `Preferences.clear()` followed by `Preferences.flush()`.

---

## 8. Class List Summary

```
MainGame.java                  // extends Game; asset manager init; screen transitions
Constants.java                 // all magic numbers
SaveData.java                  // static Preferences helpers

screens/
  MainMenuScreen.java
  VenueSelectScreen.java
  GameplayScreen.java          // wraps GameplayStage; owns Pause overlay
  GameOverScreen.java
  LeaderboardScreen.java
  ShopScreen.java
  SettingsScreen.java
  HowToPlayScreen.java

gameplay/
  VenueConfig.java             // enum with per-venue parameters
  GameplayStage.java           // core loop controller
  Dartboard.java
  StuckDart.java
  FlyingDart.java
  ParticleSystem.java

model/
  GameResult.java
  ScoreEntry.java
```

---

## 9. Out of Scope

- Online / cloud leaderboards.
- Google Play Games achievements or sign-in.
- In-app purchases or real-money transactions.
- Animated cutscenes or intro sequences.
- Multiplayer of any kind.
- Daily challenges or missions.
- Power-ups or special abilities during gameplay.
- Landscape orientation.
- Unlockable / purchasable venues (all three always accessible).
- Dartboard skin variations (only dart skins are collectible).
- libGDX `.p` particle file format (manual particle system only).
- Any physics engine (Box2D, etc.).
- Back-button hardware key on screens other than `GameplayScreen` and `MainMenuScreen` doing anything other than navigating to `MainMenuScreen`.
</parameter>
</invoke>
</function_calls>

`projects/game-887-dart-king/GAME_SPEC.md` written.

Key decisions made explicit in the spec:

- **One `GameplayScreen` class** — not three separate screen classes; parameterized by `VenueConfig` enum. Avoids duplicate logic.
- **Zone radii as pixel constants** — all 6 ring boundaries in `Constants.java` so `scoreForImpact()` has no magic numbers.
- **Segment order array** hardcoded — the regulation 20-segment clockwise order `[20,1,18,4,13,6,10,15,2,17,3,19,7,16,8,11,14,9,12,5]` is spelled out so there's no ambiguity.
- **`GameplayStage` owns all game logic** — `GameplayScreen` is a thin shell (draws stage, owns pause overlay, handles input dispatch). This keeps the core loop testable.
- **No libGDX `.p` particle files** — manual `ParticleSystem` class only, since pipeline won't generate `.p` assets.
- **9-patch filenames** — `ui_panel.9.png`, `ui_card.9.png`, etc., explicitly called out so the agent knows to load them as `NinePatch`.