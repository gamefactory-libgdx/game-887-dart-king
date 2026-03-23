package com.asocity.dartking000887.gameplay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.asocity.dartking000887.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * The rotating dartboard — drawn procedurally with ShapeRenderer.
 */
public class Dartboard {

    public float centerX;
    public float centerY;

    /** Current rotation angle in degrees. */
    public float currentAngle = 0f;

    /** Current rotation speed in RPM (may be negative for reversed direction). */
    public float currentRpm;

    /** Target RPM during easing (direction change). */
    private float targetRpm;

    /** True while reversing direction. */
    private boolean isEasing = false;
    private float easeTimer  = 0f;

    /** Darts that have landed and rotate with the board. */
    public List<StuckDart> stuckDarts = new ArrayList<>();

    // Dart segment numbers in clockwise order from top (standard regulation layout)
    private static final int[] SEGMENTS = Constants.SEGMENT_ORDER;

    // Board colors
    private static final Color COLOR_BLACK  = new Color(0.10f, 0.10f, 0.10f, 1f);
    private static final Color COLOR_CREAM  = new Color(0.94f, 0.90f, 0.82f, 1f);
    private static final Color COLOR_RED    = new Color(0.75f, 0.10f, 0.10f, 1f);
    private static final Color COLOR_GREEN  = new Color(0.05f, 0.42f, 0.12f, 1f);
    private static final Color COLOR_WIRE   = new Color(0.70f, 0.70f, 0.70f, 1f);
    private static final Color COLOR_GLOW   = new Color(1f, 0.84f, 0.10f, 0.6f);

    /** Glow timer for bullseye hits. */
    private float glowTimer = 0f;

    public Dartboard(float centerX, float centerY, float initialRpm) {
        this.centerX    = centerX;
        this.centerY    = centerY;
        this.currentRpm = initialRpm;
        this.targetRpm  = initialRpm;
    }

    // -----------------------------------------------------------------------
    // Update
    // -----------------------------------------------------------------------

    public void update(float delta) {
        if (isEasing) {
            easeTimer += delta;
            float t = Math.min(easeTimer / Constants.DIRECTION_EASE_DURATION, 1f);
            currentRpm = lerp(currentRpm, targetRpm, t * 0.1f);  // gradual lerp each frame
            if (easeTimer >= Constants.DIRECTION_EASE_DURATION) {
                currentRpm = targetRpm;
                isEasing   = false;
            }
        }

        // Rotate: degrees per second = (rpm / 60) * 360
        float degreesPerSecond = (currentRpm / 60f) * 360f;
        currentAngle += degreesPerSecond * delta;
        // Keep in [0, 360)
        currentAngle = currentAngle % 360f;
        if (currentAngle < 0) currentAngle += 360f;

        // Update all stuck darts
        for (StuckDart sd : stuckDarts) {
            sd.updateWorldTransform(centerX, centerY, currentAngle);
        }

        if (glowTimer > 0) glowTimer -= delta;
    }

    /** Smoothly reverse the rotation direction. */
    public void reverseDirection() {
        targetRpm  = -currentRpm;
        isEasing   = true;
        easeTimer  = 0f;
    }

    /** Increase absolute speed by increment. */
    public void incrementSpeed(float increment) {
        if (currentRpm >= 0) currentRpm += increment;
        else                 currentRpm -= increment;

        if (!isEasing) targetRpm = currentRpm;
    }

    /** Add a stuck dart to the board. */
    public void addStuckDart(StuckDart dart) {
        stuckDarts.add(dart);
        dart.updateWorldTransform(centerX, centerY, currentAngle);
    }

    /** Trigger a brief glow effect (for bullseye hits). */
    public void triggerGlow(float duration) {
        glowTimer = duration;
    }

    /** Freeze the board (game over). */
    public void freeze() {
        currentRpm = 0f;
        targetRpm  = 0f;
        isEasing   = false;
    }

    // -----------------------------------------------------------------------
    // Scoring
    // -----------------------------------------------------------------------

    /**
     * Compute the score for a dart landing at (worldX, worldY).
     * Both coordinates are in world space.
     */
    public int scoreForImpact(float worldX, float worldY) {
        // Convert to board-local coordinates
        float localX = worldX - centerX;
        float localY = worldY - centerY;

        float dist = (float) Math.sqrt(localX * localX + localY * localY);

        // --- Radial zone ---
        if (dist <= Constants.ZONE_BULLSEYE_RADIUS) {
            return Constants.BULLSEYE_SCORE;
        }
        if (dist <= Constants.ZONE_OUTER_BULL_RADIUS) {
            return Constants.OUTER_BULL_SCORE;
        }
        if (dist > Constants.ZONE_DOUBLE_OUTER_RADIUS) {
            return 0;  // miss
        }

        // Determine face value from board-local angle
        // atan2 gives angle from positive x-axis; board top = 90 degrees
        // We subtract currentAngle to get board-local angle from board's own reference
        float worldAngleDeg = (float) Math.toDegrees(Math.atan2(localY, localX));
        float boardLocalDeg = worldAngleDeg - currentAngle;
        // Normalize to [0, 360)
        boardLocalDeg = boardLocalDeg % 360f;
        if (boardLocalDeg < 0) boardLocalDeg += 360f;

        // Board segments start from top (90 degrees from +x), go clockwise
        // Segment 0 center is at 90 degrees from +x axis (top), width = 18 degrees each
        // Adjust so that top = 0 index start
        float fromTop = (90f - boardLocalDeg + 360f) % 360f;
        // Each segment covers 18 degrees; offset by half segment so boundary is at 0
        int segIndex = (int) ((fromTop + 9f) / 18f) % 20;
        int faceValue = SEGMENTS[segIndex];

        // Determine multiplier from radial zone
        if (dist <= Constants.ZONE_TRIPLE_INNER_RADIUS) {
            return faceValue;           // single (inner)
        }
        if (dist <= Constants.ZONE_TRIPLE_OUTER_RADIUS) {
            return faceValue * 3;       // triple ring
        }
        if (dist <= Constants.ZONE_DOUBLE_INNER_RADIUS) {
            return faceValue;           // single (outer)
        }
        // dist <= ZONE_DOUBLE_OUTER_RADIUS
        return faceValue * 2;           // double ring
    }

    // -----------------------------------------------------------------------
    // Draw
    // -----------------------------------------------------------------------

    public void draw(ShapeRenderer sr) {
        // --- Draw filled segments alternating black/cream for the single areas ---
        float r = Constants.BOARD_RADIUS;
        int segments = 20;
        float segDeg = 360f / segments;

        // Outer frame (dark circle)
        sr.setColor(new Color(0.08f, 0.05f, 0.02f, 1f));
        sr.circle(centerX, centerY, r + 8f, 64);

        // Draw each of the 20 segments (single + double ring at same angle)
        for (int i = 0; i < segments; i++) {
            // Angle for this segment center (board space: top = 90 degrees, clockwise = decreasing)
            float segCenterFromTop = i * segDeg;
            float startDeg = 90f - segCenterFromTop - segDeg / 2f + currentAngle;

            // Single area (inner) — alternating black / cream
            Color singleColor = (i % 2 == 0) ? COLOR_CREAM : COLOR_BLACK;
            sr.setColor(singleColor);
            drawSector(sr, centerX, centerY,
                       Constants.ZONE_OUTER_BULL_RADIUS, Constants.ZONE_TRIPLE_INNER_RADIUS,
                       startDeg, segDeg);

            // Triple ring — alternating red / green
            Color tripleColor = (i % 2 == 0) ? COLOR_RED : COLOR_GREEN;
            sr.setColor(tripleColor);
            drawSector(sr, centerX, centerY,
                       Constants.ZONE_TRIPLE_INNER_RADIUS, Constants.ZONE_TRIPLE_OUTER_RADIUS,
                       startDeg, segDeg);

            // Outer single — alternating black / cream
            sr.setColor(singleColor);
            drawSector(sr, centerX, centerY,
                       Constants.ZONE_TRIPLE_OUTER_RADIUS, Constants.ZONE_DOUBLE_INNER_RADIUS,
                       startDeg, segDeg);

            // Double ring — alternating red / green
            sr.setColor(tripleColor);
            drawSector(sr, centerX, centerY,
                       Constants.ZONE_DOUBLE_INNER_RADIUS, Constants.ZONE_DOUBLE_OUTER_RADIUS,
                       startDeg, segDeg);
        }

        // Outer bull (green)
        sr.setColor(COLOR_GREEN);
        sr.circle(centerX, centerY, Constants.ZONE_OUTER_BULL_RADIUS, 32);

        // Bullseye (red)
        sr.setColor(COLOR_RED);
        sr.circle(centerX, centerY, Constants.ZONE_BULLSEYE_RADIUS, 24);

        // Glow overlay on bullseye hit
        if (glowTimer > 0) {
            float alpha = Math.min(glowTimer / 0.5f, 1f);
            sr.setColor(COLOR_GLOW.r, COLOR_GLOW.g, COLOR_GLOW.b, alpha * 0.6f);
            sr.circle(centerX, centerY, Constants.ZONE_OUTER_BULL_RADIUS * 2f, 32);
        }

        // Wire divider lines (thin effect using line mode — caller switches mode)
        // (Not drawing wires to keep ShapeRenderer state simpler)

        // Draw stuck darts
        for (StuckDart sd : stuckDarts) {
            sd.draw(sr);
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /**
     * Draw an annular sector (ring segment) using triangles.
     * startAngleDeg and sweepDeg are in degrees (CCW from +x).
     */
    private void drawSector(ShapeRenderer sr, float cx, float cy,
                             float innerR, float outerR,
                             float startDeg, float sweepDeg) {
        int steps = 4; // steps per segment (fine enough for 20 segs)
        float stepDeg = sweepDeg / steps;
        for (int s = 0; s < steps; s++) {
            float a1 = (float) Math.toRadians(startDeg + s * stepDeg);
            float a2 = (float) Math.toRadians(startDeg + (s + 1) * stepDeg);

            float ix1 = cx + innerR * MathUtils.cos(a1);
            float iy1 = cy + innerR * MathUtils.sin(a1);
            float ix2 = cx + innerR * MathUtils.cos(a2);
            float iy2 = cy + innerR * MathUtils.sin(a2);
            float ox1 = cx + outerR * MathUtils.cos(a1);
            float oy1 = cy + outerR * MathUtils.sin(a1);
            float ox2 = cx + outerR * MathUtils.cos(a2);
            float oy2 = cy + outerR * MathUtils.sin(a2);

            sr.triangle(ix1, iy1, ox1, oy1, ox2, oy2);
            sr.triangle(ix1, iy1, ox2, oy2, ix2, iy2);
        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
