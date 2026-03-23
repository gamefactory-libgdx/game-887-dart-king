package com.asocity.dartking000887.gameplay;

import com.badlogic.gdx.math.MathUtils;
import com.asocity.dartking000887.Constants;
import com.asocity.dartking000887.MainGame;
import com.asocity.dartking000887.model.GameResult;

/**
 * Central controller for all gameplay logic.
 * GameplayScreen owns this and calls update() / draw() each frame.
 */
public class GameplayStage {

    public VenueConfig venue;
    public Dartboard dartboard;
    public FlyingDart flyingDart;
    public ParticleSystem particles;

    public int score        = 0;
    public int dartsThrown  = 0;
    public int dartsLanded  = 0;
    public boolean gameOver = false;
    public boolean paused   = false;

    /** Countdown after landing before the next dart appears. */
    private float postLandDelay = 0f;

    /** True if we're waiting for the next dart to spawn. */
    private boolean waitingForNextDart = false;

    /** Equipped dart skin. */
    public int equippedSkin = 0;

    /** Listener so GameplayScreen can react to events. */
    public interface Listener {
        void onDartLanded(float worldX, float worldY, int pointsScored);
        void onNearMiss();
        void onGameOver();
    }

    public Listener listener;

    private final MainGame game;

    public GameplayStage(MainGame game, VenueConfig venue) {
        this.game  = game;
        this.venue = venue;
    }

    // -----------------------------------------------------------------------
    // Init
    // -----------------------------------------------------------------------

    public void init() {
        equippedSkin = com.asocity.dartking000887.SaveData.getEquippedSkin();

        dartboard = new Dartboard(
            Constants.BOARD_CENTER_X,
            Constants.BOARD_CENTER_Y,
            venue.baseRpm
        );

        flyingDart        = new FlyingDart(equippedSkin);
        particles         = new ParticleSystem();
        score             = 0;
        dartsThrown       = 0;
        dartsLanded       = 0;
        gameOver          = false;
        paused            = false;
        postLandDelay     = 0f;
        waitingForNextDart = false;
    }

    // -----------------------------------------------------------------------
    // Update
    // -----------------------------------------------------------------------

    public void update(float delta) {
        if (paused || gameOver) return;

        dartboard.update(delta);
        particles.update(delta);

        if (flyingDart.active) {
            flyingDart.update(delta);

            // Check collision with stuck darts first (game over condition)
            if (flyingDart.checkHitStuck(dartboard.stuckDarts)) {
                // Near-miss sfx check: if tipRect nearly overlaps (already overlapping = hit)
                triggerGameOver();
                return;
            }

            // Check hit on dartboard
            if (flyingDart.checkHitBoard(dartboard.centerX, dartboard.centerY)) {
                landDart(flyingDart.x, flyingDart.y);
                return;
            }

            // Off screen — dart missed (no penalty, just schedule next dart)
            if (flyingDart.isOffScreen()) {
                flyingDart.active   = false;
                waitingForNextDart  = true;
                postLandDelay       = Constants.POST_LAND_DELAY;
            }
        } else if (waitingForNextDart) {
            postLandDelay -= delta;
            if (postLandDelay <= 0f) {
                spawnNewDart();
            }
        } else {
            // No dart yet — spawn first one immediately
            spawnNewDart();
        }
    }

    // -----------------------------------------------------------------------
    // Actions
    // -----------------------------------------------------------------------

    /** Called when the player taps the screen. */
    public void throwDart() {
        if (gameOver || paused || flyingDart.active || waitingForNextDart) return;
        flyingDart.launch(Constants.DART_LAUNCH_X, Constants.DART_LAUNCH_Y);
        dartsThrown++;
        game.playSound(Constants.SFX_POWER_UP);
    }

    private void landDart(float worldX, float worldY) {
        flyingDart.active = false;

        int points = dartboard.scoreForImpact(worldX, worldY);
        score += points;
        dartsLanded++;

        // Compute board-local angle offset from world hit point
        float dx  = worldX - dartboard.centerX;
        float dy  = worldY - dartboard.centerY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        float worldAngleDeg = (float) Math.toDegrees(Math.atan2(dy, dx));
        float offset = worldAngleDeg - dartboard.currentAngle;

        StuckDart stuck = new StuckDart(offset, dist, equippedSkin);
        dartboard.addStuckDart(stuck);

        // Particle effects
        if (points >= Constants.BULLSEYE_SCORE) {
            particles.spawnBullseye(worldX, worldY);
            dartboard.triggerGlow(0.6f);
            game.playSound(Constants.SFX_POWER_UP);
        } else {
            particles.spawnHit(worldX, worldY);
            game.playSound(Constants.SFX_BUTTON_CLICK);
        }

        // Speed up every N darts landed
        if (dartsLanded % venue.incrementEvery == 0) {
            dartboard.incrementSpeed(venue.speedIncrement);
        }

        // Direction change
        if (venue.directionChangeEvery > 0 &&
            dartsLanded % venue.directionChangeEvery == 0) {
            dartboard.reverseDirection();
        }

        if (listener != null) listener.onDartLanded(worldX, worldY, points);

        waitingForNextDart = true;
        postLandDelay      = Constants.POST_LAND_DELAY;
    }

    private void triggerGameOver() {
        gameOver = true;
        flyingDart.active = false;

        // Particles at board center
        particles.spawnBreak(dartboard.centerX, dartboard.centerY);
        dartboard.freeze();

        game.playMusicOnce(Constants.MUSIC_GAME_OVER);
        game.playSound(Constants.SFX_GAME_OVER);

        if (listener != null) listener.onGameOver();
    }

    private void spawnNewDart() {
        waitingForNextDart = false;
        flyingDart.skinIndex = equippedSkin;
        // dart starts at launch position but is NOT active yet — waits for tap
        flyingDart.active = false;
        flyingDart.x = Constants.DART_LAUNCH_X;
        flyingDart.y = Constants.DART_LAUNCH_Y;
    }

    // -----------------------------------------------------------------------
    // Pause / Resume
    // -----------------------------------------------------------------------

    public void pause()  { paused = true;  }
    public void resume() { paused = false; }

    // -----------------------------------------------------------------------
    // Result
    // -----------------------------------------------------------------------

    public GameResult buildResult() {
        GameResult r   = new GameResult();
        r.venue        = venue;
        r.score        = score;
        r.dartsThrown  = dartsThrown;
        r.dartsLanded  = dartsLanded;

        int prevBest   = com.asocity.dartking000887.SaveData.getHiScore(venue.prefHiScore);
        r.isNewHiScore = score > prevBest;
        r.trophiesEarned = Math.min(score / 100, Constants.MAX_TROPHIES_PER_SESSION);
        return r;
    }

    // -----------------------------------------------------------------------
    // Query
    // -----------------------------------------------------------------------

    /** True if a dart is ready to throw (not in flight, not on delay). */
    public boolean canThrow() {
        return !gameOver && !paused && !flyingDart.active && !waitingForNextDart;
    }
}
