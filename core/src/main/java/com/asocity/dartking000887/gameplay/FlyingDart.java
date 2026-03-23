package com.asocity.dartking000887.gameplay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.asocity.dartking000887.Constants;

/**
 * A dart in flight — travels straight up from the launch zone toward the board.
 */
public class FlyingDart {

    /** Tip position (world units). */
    public float x;
    public float y;

    /** Whether this dart is currently in flight. */
    public boolean active = false;

    /** Skin index (0-5). */
    public int skinIndex;

    /** Small collision rectangle at the dart tip. */
    public Rectangle tipRect = new Rectangle();

    private static final Color[] BARREL_COLORS = {
        new Color(0.75f, 0.75f, 0.80f, 1f),
        new Color(0.72f, 0.40f, 0.15f, 1f),
        new Color(0.95f, 0.80f, 0.10f, 1f),
        new Color(0.10f, 0.50f, 0.95f, 1f),
        new Color(0.12f, 0.08f, 0.08f, 1f),
        new Color(0.85f, 0.85f, 0.90f, 1f),
    };

    private static final Color[] FLIGHT_COLORS = {
        new Color(0.60f, 0.60f, 0.65f, 1f),
        new Color(0.90f, 0.40f, 0.05f, 1f),
        new Color(0.95f, 0.70f, 0.05f, 1f),
        new Color(0.20f, 0.80f, 1.00f, 1f),
        new Color(0.80f, 0.10f, 0.05f, 1f),
        new Color(0.70f, 0.80f, 0.95f, 1f),
    };

    public FlyingDart(int skinIndex) {
        this.skinIndex = Math.max(0, Math.min(5, skinIndex));
    }

    /** Place dart at the launch position and set it in flight. */
    public void launch(float startX, float startY) {
        x      = startX;
        y      = startY;
        active = true;
        updateTipRect();
    }

    /** Move the dart upward and update collision rect. */
    public void update(float delta) {
        if (!active) return;
        y += Constants.DART_TRAVEL_SPEED * delta;
        updateTipRect();
    }

    private void updateTipRect() {
        float hs = Constants.DART_TIP_SIZE / 2f;
        tipRect.set(x - hs, y - hs, Constants.DART_TIP_SIZE, Constants.DART_TIP_SIZE);
    }

    /**
     * Returns true if the dart tip is within BOARD_RADIUS of the board center.
     */
    public boolean checkHitBoard(float boardCX, float boardCY) {
        if (!active) return false;
        float dx = x - boardCX;
        float dy = y - boardCY;
        return (dx * dx + dy * dy) <= (Constants.BOARD_RADIUS * Constants.BOARD_RADIUS);
    }

    /**
     * Returns true if the tip rect overlaps any stuck dart's body rect.
     */
    public boolean checkHitStuck(java.util.List<StuckDart> stuck) {
        if (!active) return false;
        for (StuckDart s : stuck) {
            if (tipRect.overlaps(s.bodyRect)) return true;
        }
        return false;
    }

    /** Returns true if the dart has flown above the top of the screen. */
    public boolean isOffScreen() {
        return y > Constants.WORLD_HEIGHT + Constants.DART_HEIGHT;
    }

    /** Draw this dart with ShapeRenderer (must be in FILLED mode). */
    public void draw(ShapeRenderer sr) {
        if (!active) return;

        Color barrel = BARREL_COLORS[skinIndex];
        Color flight = FLIGHT_COLORS[skinIndex];

        float hw     = Constants.DART_BODY_WIDTH / 2f;
        float bodyLen = Constants.DART_BODY_HEIGHT;
        float flightLen = Constants.DART_HEIGHT - bodyLen;

        // Dart points straight up (angle 90 degrees)
        // Tip at (x, y), body extends downward
        float bx1 = x;
        float by1 = y - bodyLen;

        // Barrel as rectangle
        sr.setColor(barrel);
        sr.triangle(x + hw, y, x - hw, y, x - hw, y - bodyLen);
        sr.triangle(x + hw, y, x - hw, y - bodyLen, x + hw, y - bodyLen);

        // Flight triangle
        float fhw = Constants.DART_WIDTH / 2f;
        sr.setColor(flight);
        sr.triangle(bx1, by1,
                    bx1 + fhw, by1 - flightLen,
                    bx1 - fhw, by1 - flightLen);

        // Tip dot
        sr.setColor(Color.WHITE);
        sr.circle(x, y, 2.5f, 8);
    }

    /** Draw at launch position (not active, just showing where dart will appear). */
    public void drawAtLaunch(ShapeRenderer sr) {
        Color barrel = BARREL_COLORS[skinIndex];
        Color flight = FLIGHT_COLORS[skinIndex];

        float hw     = Constants.DART_BODY_WIDTH / 2f;
        float bodyLen = Constants.DART_BODY_HEIGHT;
        float flightLen = Constants.DART_HEIGHT - bodyLen;

        float lx = Constants.DART_LAUNCH_X;
        float ly = Constants.DART_LAUNCH_Y;

        sr.setColor(barrel);
        sr.triangle(lx + hw, ly, lx - hw, ly, lx - hw, ly - bodyLen);
        sr.triangle(lx + hw, ly, lx - hw, ly - bodyLen, lx + hw, ly - bodyLen);

        float fhw = Constants.DART_WIDTH / 2f;
        sr.setColor(flight);
        sr.triangle(lx, ly - bodyLen,
                    lx + fhw, ly - bodyLen - flightLen,
                    lx - fhw, ly - bodyLen - flightLen);

        sr.setColor(Color.WHITE);
        sr.circle(lx, ly, 2.5f, 8);
    }
}
