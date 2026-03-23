```markdown
# Figma AI Design Brief — Dart King

**Genre:** Aim-Arcade | **Platform:** Android (480×854 portrait)
**Mood:** British pub atmosphere — warm, dim, slightly gritty. Feels like a real pub at night.

---

## 1. Art Style & Color Palette

Realistic-painterly pub aesthetic with warm tungsten lighting and deep shadow contrast. The style sits between a stylised illustration and a moody photo-reference painting — think hand-painted tavern backdrops. Worn wood, brass fixtures, neon beer signs, and chalk scoreboards are the visual vocabulary. There is no cartoon flatness and no sci-fi chrome — every surface should feel slightly aged and tactile.

**Primary palette**

| Role | Hex | Usage |
|---|---|---|
| Deep Pub Shadow | `#1A0F07` | Base darks, vignettes |
| Warm Mahogany | `#4B1E0D` | Wood panels, frames |
| Amber Tungsten | `#E8840A` | Key light, glows |
| Brass Gold | `#C9963A` | Trophy accents, trim |
| Cream Chalk | `#F2EDD7` | Text surfaces, chalk lines |

**Accent palette**

| Role | Hex | Usage |
|---|---|---|
| Dartboard Red | `#C0242B` | Bulls-eye ring highlights |
| Neon Green | `#39D353` | Score pop, trophy glow |

**Font mood:** Bold slab-serif or worn western display for titles (e.g. MotionControlBold). Clean condensed sans for scores (e.g. Orbitron or GoodTiming). No script or pixel fonts — this is a serious pub, not a carnival.

---

## 2. App Icon — icon_512.png (512×512px)

Square canvas, full bleed. No text, no letterboxing.

- **Background:** Radial gradient from `#4B1E0D` at center to near-black `#0E0704` at corners, with a subtle worn-leather texture overlay.
- **Central symbol:** A single dart viewed head-on at a 20° angle — tip pointing down-right — impaled precisely in a glowing bulls-eye. The dartboard occupies roughly 60% of the canvas diameter, showing only the centre three rings (bull, bull-25, treble-bull band) so the image reads clearly at small sizes.
- **Glow/shadow:** Amber-gold rim light wraps the dart barrel. The bulls-eye emits a soft red-orange radial glow (`#C0242B` → transparent). A thin brass ring frames the outer board edge.
- **Mood:** Confident, dramatic, slightly dangerous — the feel of a perfect throw.

---

## 3. UI Screens (480×854 portrait)

> **RULE:** All images are BACKGROUNDS ONLY. No rendered text, no button graphics, no icons.
> The game engine overlays all interactive elements in code.
> DO include empty decorative panel outlines, banner shapes, and card frames — styled but blank inside.

---

### MainMenuScreen

**A) Background Image**

Full-height photograph-painted interior of a British pub at night. A dartboard hangs centre-back on a dark wood-panelled wall, lit by a single warm pendant bulb casting a cone of amber light. Beer taps and shelves of bottles are blurred in the left background. The floor is dark flagstone. A horizontal banner-shaped wooden sign plank spans the top third — deeply carved but no text — with brass nail heads at each corner. Two vertical pillar shapes frame the left and right edges, acting as natural UI rails. Heavy vignette darkens all four corners to `#0E0704`.

**B) Button Layout (code-drawn)**

```
DART KING [title label]  | top-Y=80px   | x=centered          | size=340x70
PLAY                     | top-Y=320px  | x=centered          | size=260x60
VENUE SELECT             | top-Y=400px  | x=centered          | size=260x60
SHOP                     | top-Y=480px  | x=centered          | size=260x60
LEADERBOARD              | top-Y=560px  | x=centered          | size=260x60
HOW TO PLAY              | top-Y=640px  | x=centered          | size=260x60
SETTINGS ⚙               | top-Y=790px  | x=right@20px        | size=100x44
```

---

### VenueSelectScreen

**A) Background Image**

Three distinct pub alcoves arranged side-by-side across a single painted panorama: left alcove is dimly lit with sticky carpet and a pool table glimpsed behind — Local Pub; centre alcove is brighter, with trophy shelf and a proper oche line painted on the floor — Tournament Hall; right alcove is dramatically lit with stage spotlights and a crowd silhouette — Champions Arena. Each alcove is separated by a heavy dark timber post. Three empty rectangular card-frame shapes (no fill, just a worn-wood carved border) are centred in each alcove to receive venue names. A chalk scoreboard shape at the top, blank.

**B) Button Layout**

```
CHOOSE VENUE [label]   | top-Y=60px   | x=centered          | size=300x50
LOCAL PUB              | top-Y=250px  | x=left@40px         | size=120x160
TOURNAMENT HALL        | top-Y=250px  | x=centered          | size=120x160
CHAMPIONS ARENA        | top-Y=250px  | x=right@40px        | size=120x160
BACK                   | top-Y=790px  | x=left@20px         | size=120x44
```

---

### LocalPubScreen (Gameplay — Venue 1)

**A) Background Image**

Intimate pub corner viewed straight-on. Dark wood-panelled wall fills the frame. A worn circular dartboard mount ring is centred at approximately 40% down the canvas — the ring is an empty decorative frame only, no actual board graphic (engine renders the board). Sticky red-patterned carpet at the bottom quarter. A neon "OPEN" sign glows amber-orange in the upper-left corner, slightly out-of-focus. A short chalk scoreboard rectangle sits in the upper-right, blank. Heavy vignette. Warm ambient light only from a single unseen bulb above.

**B) Button Layout**

```
SCORE [label]          | top-Y=30px   | x=right@20px        | size=140x44
DARTS LEFT [label]     | top-Y=30px   | x=left@20px         | size=140x44
PAUSE ⏸                | top-Y=20px   | x=centered          | size=60x44
[dartboard — engine]   | top-Y=180px  | x=centered          | size=300x300
[dart launch zone]     | top-Y=720px  | x=centered          | size=full-width×80
```

---

### TournamentScreen (Gameplay — Venue 2)

**A) Background Image**

Wider tournament hall. Brick wall with painted wainscoting. A proper oche throw line is chalked on a wooden floor visible at screen bottom. Trophy cabinet with empty shelves on the left wall edge. Overhead fluorescent strip lights cast a cooler, harder light compared to Local Pub — still warm overall but slightly bluer highlights (`#D4A84B`). The dartboard mount ring is centred at 38% height, same decorative-frame treatment. A large blank rectangular scoreboard banner shape spans the upper portion.

**B) Button Layout**

```
SCORE [label]          | top-Y=30px   | x=right@20px        | size=140x44
DARTS LEFT [label]     | top-Y=30px   | x=left@20px         | size=140x44
PAUSE ⏸                | top-Y=20px   | x=centered          | size=60x44
[dartboard — engine]   | top-Y=175px  | x=centered          | size=320x320
[dart launch zone]     | top-Y=720px  | x=centered          | size=full-width×80
```

---

### ChampionsScreen (Gameplay — Venue 3)

**A) Background Image**

Dramatic arena stage. Deep charcoal background with theatrical spotlights beaming down from upper corners, creating hard-edged light cones in `#E8840A` and `#C9963A`. A crowd silhouette (heads and shoulders only, very dark) lines the bottom 15% of the canvas. Red velvet rope barriers frame the lower stage. The dartboard mount ring is centred at 35% height — same decorative-ring treatment — but with a stronger glow halo suggesting prestige. Two championship banner shapes hang on either side wall, blank. Heavy atmospheric haze/smoke effect at ceiling.

**B) Button Layout**

```
SCORE [label]          | top-Y=30px   | x=right@20px        | size=140x44
DARTS LEFT [label]     | top-Y=30px   | x=left@20px         | size=140x44
PAUSE ⏸                | top-Y=20px   | x=centered          | size=60x44
[dartboard — engine]   | top-Y=165px  | x=centered          | size=340x340
[dart launch zone]     | top-Y=720px  | x=centered          | size=full-width×80
```

---

### GameOverScreen

**A) Background Image**

Close-up of the dartboard face after a fatal collision — implied through dramatic lighting, not explicit gore. The scene is the same venue wall as wherever the player was, but now in a "slow-motion freeze" atmosphere: everything except the centre of the image is heavily blurred and darkened. An empty hexagonal trophy-case frame sits in the upper-centre area (for trophies earned). A horizontal blank ribbon/banner shape below it holds score readout. A worn wooden signboard shape in the lower-centre area, blank, for retry/menu options. Amber light with a slight red tint shift compared to gameplay.

**B) Button Layout**

```
GAME OVER [label]      | top-Y=80px   | x=centered          | size=300x60
SCORE [label]          | top-Y=200px  | x=centered          | size=240x50
BEST [label]           | top-Y=265px  | x=centered          | size=200x40
TROPHIES [label]       | top-Y=340px  | x=centered          | size=200x40
RETRY                  | top-Y=560px  | x=centered          | size=240x56
MENU                   | top-Y=636px  | x=centered          | size=240x56
```

---

### LeaderboardScreen

**A) Background Image**

Pub wall of fame: rich dark mahogany wood panelling fills the frame. Nine empty rectangular plaque frames are evenly stacked in the centre — slightly worn brass-border plaques, no text, leaving room for the top-10 score list. A "CHAMPIONS BOARD" carved wooden header sign at top — carved frame only, no lettering. A gold star rosette decorates each upper corner. Warm directional lighting from upper-left casts soft shadows off each plaque. Subtle green felt stripe border at left and right edges.

**B) Button Layout**

```
HALL OF FAME [label]   | top-Y=50px   | x=centered          | size=300x50
VENUE TABS [row]       | top-Y=110px  | x=centered          | size=440x44
[score rows 1-10]      | top-Y=175px  | x=centered          | size=440x54 each
BACK                   | top-Y=800px  | x=left@20px         | size=120x44
```

---

### ShopScreen

**A) Background Image**

Pub display cabinet interior: glass-fronted dark wood cabinet with six empty circular mount pedestals arranged in a 2×3 grid, each on a velvet cushion (empty — engine renders dart skin on top). Spotlights illuminate each pedestal from above. Cabinet has brass hinges and a keyhole. Price tags (blank white card shapes) hang from each pedestal. A trophy-coin counter banner shape sits in the upper-right corner, blank. Shop name sign at top — carved frame, no text. Dark wood background with ambient warm light.

**B) Button Layout**

```
DART SHOP [label]      | top-Y=50px   | x=centered          | size=260x50
TROPHIES [label]       | top-Y=30px   | x=right@20px        | size=160x40
SKIN 1 [card]          | top-Y=160px  | x=left@40px         | size=160x180
SKIN 2 [card]          | top-Y=160px  | x=right@40px        | size=160x180
SKIN 3 [card]          | top-Y=360px  | x=left@40px         | size=160x180
SKIN 4 [card]          | top-Y=360px  | x=right@40px        | size=160x180
SKIN 5 [card]          | top-Y=560px  | x=left@40px         | size=160x180
SKIN 6 [card]          | top-Y=560px  | x=right@40px        | size=160x180
BACK                   | top-Y=800px  | x=left@20px         | size=120x44
```

---

### SettingsScreen

**A) Background Image**

Cosy pub back-office corner. A corkboard pinned with empty notices (blank rectangles with push-pin shapes) fills the centre. A small wooden desk in the lower third with nothing on it. Old stereo/jukebox shape visible on the left wall — warm amber glow from its dial but no text. A hanging bare bulb provides overhead light. Two decorative horizontal divider bar shapes (carved wood) divide the canvas into thirds, creating visual zones for settings groups. Heavy vignette at edges.

**B) Button Layout**

```
SETTINGS [label]       | top-Y=60px   | x=centered          | size=260x50
MUSIC [toggle label]   | top-Y=200px  | x=left@60px         | size=200x44
MUSIC TOGGLE           | top-Y=200px  | x=right@60px        | size=80x44
SFX [toggle label]     | top-Y=270px  | x=left@60px         | size=200x44
SFX TOGGLE             | top-Y=270px  | x=right@60px        | size=80x44
RESET DATA             | top-Y=580px  | x=centered          | size=240x50
BACK                   | top-Y=800px  | x=left@20px         | size=120x44
```

---

### HowToPlayScreen

**A) Background Image**

Illustrated pub tutorial wall. Imagine a large blank chalkboard mounted on the mahogany wall — the board is empty (engine renders tutorial text and arrows on top). Chalk-dust hazing around the board's edges for texture. A small wooden ledge at the bottom of the chalkboard holds chalk and an eraser (decorative only). Four empty rounded-rectangle panel shapes are arranged in a 2×2 grid within the chalkboard area, suggesting step panels — blank interiors. A "HOW TO PLAY" carved wooden sign frame sits above the board, no text. Warm lamp glow from upper-left.

**B) Button Layout**

```
HOW TO PLAY [label]    | top-Y=50px   | x=centered          | size=300x50
STEP 1 panel           | top-Y=155px  | x=left@30px         | size=195x170
STEP 2 panel           | top-Y=155px  | x=right@30px        | size=195x170
STEP 3 panel           | top-Y=345px  | x=left@30px         | size=195x170
STEP 4 panel           | top-Y=345px  | x=right@30px        | size=195x170
[tip text area]        | top-Y=550px  | x=centered          | size=420x120
BACK                   | top-Y=790px  | x=left@20px         | size=120x44
```

---

## 4. Export Checklist

```
- icon_512.png (512x512)
- ui/main_menu_screen.png (480x854)
- ui/venue_select_screen.png (480x854)
- ui/local_pub_screen.png (480x854)
- ui/tournament_screen.png (480x854)
- ui/champions_screen.png (480x854)
- ui/game_over_screen.png (480x854)
- ui/leaderboard_screen.png (480x854)
- ui/shop_screen.png (480x854)
- ui/settings_screen.png (480x854)
- ui/how_to_play_screen.png (480x854)
```

**Total:** 1 icon + 10 UI backgrounds = **11 files**

---

## Design Constraints Reminder

- All UI images are **background art only** — no text, no buttons, no icons rendered in image
- Empty decorative frames/panels are encouraged — engine fills them
- Consistent warm tungsten lighting across all screens for visual cohesion
- Dark overall: average luminance should stay below 40% to ensure white/cream text is readable over every background
- No transparency / alpha — all PNGs are fully opaque
```