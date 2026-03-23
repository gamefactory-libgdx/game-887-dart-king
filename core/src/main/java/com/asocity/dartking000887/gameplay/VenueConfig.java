package com.asocity.dartking000887.gameplay;

import com.asocity.dartking000887.Constants;

/** Each constant represents one playable venue with its own speed parameters. */
public enum VenueConfig {

    VENUE_LOCAL_PUB(
        Constants.VENUE_PUB_BASE_RPM,
        Constants.VENUE_PUB_SPEED_INC,
        Constants.VENUE_PUB_INC_EVERY,
        Constants.VENUE_PUB_DIR_CHANGE,
        "ui/local_pub_screen.png",
        Constants.PREF_HISCORE_LOCAL_PUB,
        Constants.PREF_SCORES_LOCAL_PUB,
        "Local Pub"
    ),

    VENUE_TOURNAMENT(
        Constants.VENUE_TOUR_BASE_RPM,
        Constants.VENUE_TOUR_SPEED_INC,
        Constants.VENUE_TOUR_INC_EVERY,
        Constants.VENUE_TOUR_DIR_CHANGE,
        "ui/tournament_screen.png",
        Constants.PREF_HISCORE_TOURNAMENT,
        Constants.PREF_SCORES_TOURNAMENT,
        "Tournament Hall"
    ),

    VENUE_CHAMPIONS(
        Constants.VENUE_CHAMP_BASE_RPM,
        Constants.VENUE_CHAMP_SPEED_INC,
        Constants.VENUE_CHAMP_INC_EVERY,
        Constants.VENUE_CHAMP_DIR_CHANGE,
        "ui/champions_screen.png",
        Constants.PREF_HISCORE_CHAMPIONS,
        Constants.PREF_SCORES_CHAMPIONS,
        "Champions Arena"
    );

    public final float baseRpm;
    public final float speedIncrement;
    public final int   incrementEvery;
    public final int   directionChangeEvery;  // 0 = never
    public final String bgTexture;
    public final String prefHiScore;
    public final String prefScores;
    public final String displayName;

    VenueConfig(float baseRpm, float speedIncrement, int incrementEvery,
                int directionChangeEvery, String bgTexture,
                String prefHiScore, String prefScores, String displayName) {
        this.baseRpm              = baseRpm;
        this.speedIncrement       = speedIncrement;
        this.incrementEvery       = incrementEvery;
        this.directionChangeEvery = directionChangeEvery;
        this.bgTexture            = bgTexture;
        this.prefHiScore          = prefHiScore;
        this.prefScores           = prefScores;
        this.displayName          = displayName;
    }
}
