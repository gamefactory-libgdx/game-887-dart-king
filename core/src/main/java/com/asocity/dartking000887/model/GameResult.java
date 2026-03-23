package com.asocity.dartking000887.model;

import com.asocity.dartking000887.gameplay.VenueConfig;

/** Plain data class carrying the result of one gameplay session. */
public class GameResult {
    public VenueConfig venue;
    public int score;
    public int dartsThrown;
    public int dartsLanded;
    public boolean isNewHiScore;
    public int trophiesEarned;  // floor(score / 100), capped at MAX_TROPHIES_PER_SESSION
}
