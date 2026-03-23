package com.asocity.dartking000887;

public class Constants {

    // Viewport
    public static final float WORLD_WIDTH  = 480f;
    public static final float WORLD_HEIGHT = 854f;

    // Dartboard geometry (scaled to 480x854 world)
    public static final float BOARD_RADIUS          = 160f;
    public static final float BOARD_CENTER_X        = 240f;
    public static final float BOARD_CENTER_Y        = 510f;  // ~60% of 854

    // Zone radii (proportional to BOARD_RADIUS = 160)
    public static final float ZONE_BULLSEYE_RADIUS      = 12f;
    public static final float ZONE_OUTER_BULL_RADIUS    = 28f;
    public static final float ZONE_TRIPLE_INNER_RADIUS  = 98f;
    public static final float ZONE_TRIPLE_OUTER_RADIUS  = 112f;
    public static final float ZONE_DOUBLE_INNER_RADIUS  = 142f;
    public static final float ZONE_DOUBLE_OUTER_RADIUS  = 160f;

    // Standard dartboard segment order (clockwise from top)
    public static final int[] SEGMENT_ORDER = {
        20, 1, 18, 4, 13, 6, 10, 15, 2, 17, 3, 19, 7, 16, 8, 11, 14, 9, 12, 5
    };

    // Dart geometry
    public static final float DART_WIDTH            = 11f;
    public static final float DART_HEIGHT           = 71f;
    public static final float DART_TIP_SIZE         = 7f;
    public static final float DART_BODY_WIDTH       = 7f;
    public static final float DART_BODY_HEIGHT      = 53f;
    public static final float DART_TRAVEL_SPEED     = 1600f;  // px/s (world units)
    public static final float DART_LAUNCH_X         = 240f;   // center of screen
    public static final float DART_LAUNCH_Y         = 72f;    // px from bottom

    // Gameplay timing
    public static final float POST_LAND_DELAY         = 0.4f;  // s between land and next dart
    public static final float DIRECTION_EASE_DURATION = 0.5f;  // s to reverse rotation

    // Scoring
    public static final int   BULLSEYE_SCORE             = 100;
    public static final int   OUTER_BULL_SCORE           = 25;
    public static final int   TROPHIES_PER_100_SCORE     = 1;
    public static final int   MAX_TROPHIES_PER_SESSION   = 50;

    // Near-miss threshold (world units)
    public static final float NEAR_MISS_DISTANCE     = 5f;

    // Shop / skins
    public static final int   SKIN_COUNT   = 6;
    public static final int[] SKIN_PRICES  = { 0, 50, 200, 500, 1000, 2000 };

    // Leaderboard
    public static final int   LEADERBOARD_MAX_ENTRIES = 10;

    // Font sizes (scaled to 480x854 world)
    public static final int   FONT_SIZE_TITLE   = 48;
    public static final int   FONT_SIZE_HEADING = 32;
    public static final int   FONT_SIZE_BODY    = 20;
    public static final int   FONT_SIZE_SMALL   = 14;
    public static final int   FONT_SIZE_SCORE   = 56;

    // Font outline widths
    public static final int   BORDER_SMALL  = 1;  // sizes <= 16
    public static final int   BORDER_BODY   = 2;  // sizes 17-36
    public static final int   BORDER_TITLE  = 3;  // sizes >= 37

    // Venue: Local Pub
    public static final float VENUE_PUB_BASE_RPM    = 8f;
    public static final float VENUE_PUB_SPEED_INC   = 0.5f;
    public static final int   VENUE_PUB_INC_EVERY   = 5;
    public static final int   VENUE_PUB_DIR_CHANGE  = 0;   // never

    // Venue: Tournament
    public static final float VENUE_TOUR_BASE_RPM   = 14f;
    public static final float VENUE_TOUR_SPEED_INC  = 1.0f;
    public static final int   VENUE_TOUR_INC_EVERY  = 5;
    public static final int   VENUE_TOUR_DIR_CHANGE = 15;

    // Venue: Champions
    public static final float VENUE_CHAMP_BASE_RPM  = 22f;
    public static final float VENUE_CHAMP_SPEED_INC = 1.5f;
    public static final int   VENUE_CHAMP_INC_EVERY = 5;
    public static final int   VENUE_CHAMP_DIR_CHANGE = 10;

    // Button sizes (world units)
    public static final float BTN_MAIN_W    = 280f;
    public static final float BTN_MAIN_H    = 56f;
    public static final float BTN_SEC_W     = 220f;
    public static final float BTN_SEC_H     = 50f;
    public static final float BTN_SMALL_W   = 160f;
    public static final float BTN_SMALL_H   = 44f;
    public static final float BTN_ROUND_W   = 56f;
    public static final float BTN_ROUND_H   = 56f;

    // Preferences file name
    public static final String PREFS_NAME = "dart_king";

    // Preferences keys
    public static final String PREF_TROPHIES_BALANCE   = "trophiesBalance";
    public static final String PREF_OWNED_SKINS        = "ownedSkins";
    public static final String PREF_SKIN               = "equippedSkin";
    public static final String PREF_MUSIC_ON           = "musicEnabled";
    public static final String PREF_SFX_ON             = "sfxEnabled";
    public static final String PREF_HISCORE_LOCAL_PUB  = "hiscoreLocalPub";
    public static final String PREF_HISCORE_TOURNAMENT = "hiscoreTournament";
    public static final String PREF_HISCORE_CHAMPIONS  = "hiscoreChampions";
    public static final String PREF_SCORES_LOCAL_PUB   = "scoresLocalPub";
    public static final String PREF_SCORES_TOURNAMENT  = "scoresTournament";
    public static final String PREF_SCORES_CHAMPIONS   = "scoresChampions";

    // Asset paths — UI buttons
    public static final String BTN_RECT_UP   = "ui/buttons/button_rectangle_depth_gradient.png";
    public static final String BTN_RECT_DOWN = "ui/buttons/button_rectangle_depth_flat.png";
    public static final String BTN_ROUND_UP  = "ui/buttons/button_round_depth_gradient.png";
    public static final String BTN_ROUND_DOWN = "ui/buttons/button_round_depth_flat.png";

    // Asset paths — music
    public static final String MUSIC_MENU     = "sounds/music/music_menu.ogg";
    public static final String MUSIC_GAMEPLAY = "sounds/music/music_gameplay.ogg";
    public static final String MUSIC_GAME_OVER = "sounds/music/music_game_over.ogg";

    // Asset paths — SFX
    public static final String SFX_BUTTON_CLICK  = "sounds/sfx/sfx_button_click.ogg";
    public static final String SFX_BUTTON_BACK   = "sounds/sfx/sfx_button_back.ogg";
    public static final String SFX_TOGGLE        = "sounds/sfx/sfx_toggle.ogg";
    public static final String SFX_GAME_OVER     = "sounds/sfx/sfx_game_over.ogg";
    public static final String SFX_POWER_UP      = "sounds/sfx/sfx_power_up.ogg";

    // Prevent instantiation
    private Constants() {}
}
