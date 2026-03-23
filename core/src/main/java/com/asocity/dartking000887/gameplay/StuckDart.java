package com.asocity.dartking000887.gameplay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.asocity.dartking000887.Constants;

/**
 * A dart that has landed and is now stuck to the rotating dartboard.
 * Position is computed each frame from the board's current angle plus this dart's offset.
 */
public class StuckDart {

    /** Angle offset (degrees) from board's reference angle at the moment of impact. */
    public float boardAngleOffset;
    /** Distance from board center at impact (world units). */
    public float boardRadiusOffset;
    /** 0-based skin index — controls draw color. */
    public int skinIndex;

    /** Current world tip position (updated each frame). */
    public float worldX;
    public float worldY;
    /** Current world angle (degrees) of the dart — same as board angle + offset. */
    public float worldAngle;

    /**
     * Collision body: thin rectangle centered on the dart barrel.
     * 8 px wide × 53 px tall in world units, axis-aligned (conservative AABB).
     * We use the bounding box of the rotated dart for collision to keep things simple.
     */
    public Rectangle bodyRect = new Rectangle();

    /** Dart skin colors: index 0-5 */
    private static final Color[] BARREL_COLORS = {
        new Color(0.75f, 0.75f, 0.80f, 1f),  // 0 Classic — silver
        new Color(0.72f, 0.40f, 0.15f, 1f),  // 1 Copper Strike
        new Color(0.95f, 0.80f, 0.10f, 1f),  // 2 Golden Arrow
        new Color(0.10f, 0.50f, 0.95f, 1f),  // 3 Neon Flash
        new Color(0.12f, 0.08f, 0.08f, 1f),  // 4 Dragon Fire
        new Color(0.85f, 0.85f, 0.90f, 1f),  // 5 Platinum King
    };

    private static final Color[] FLIGHT_COLORS = {
        new Color(0.60f, 0.60f, 0.65f, 1f),  // Classic
        new Color(0.90f, 0.40f, 0.05f, 1f),  // Copper
        new Color(0.95f, 0.70f, 0.05f, 1f),  // Gold
        new Color(0.20f, 0.80f, 1.00f, 1f),  // Neon
        new Color(0.80f, 0.10f, 0.05f, 1f),  // Dragon
        new Color(0.70f, 0.80f, 0.95f, 1f),  // Platinum
    };

    public StuckDart(float boardAngleOffset, float boardRadiusOffset, int skinIndex) {
        this.boardAngleOffset  = boardAngleOffset;
        this.boardRadiusOffset = boardRadiusOffset;
        this.skinIndex         = Math.max(0, Math.min(5, skinIndex));
    }

    /**
     * Called every frame: recompute world position from board center and current board angle,
     * then rebuild the AABB collision rectangle.
     */
    public void updateWorldTransform(float boardCenterX, float boardCenterY, float boardAngle) {
        float totalAngle = boardAngle + boardAngleOffset;
        float rad = totalAngle * MathUtils.degreesToRadians;

        // Tip position = center + offset along the totalAngle direction
        worldX     = boardCenterX + boardRadiusOffset * MathUtils.cos(rad);
        worldY     = boardCenterY + boardRadiusOffset * MathUtils.sin(rad);
        worldAngle = totalAngle;

        // AABB body rect: half-width = DART_BODY_WIDTH/2, half-height = DART_BODY_HEIGHT/2
        // centered slightly behind the tip
        float hw = Constants.DART_BODY_WIDTH / 2f;
        float hh = Constants.DART_BODY_HEIGHT / 2f;

        // Body center is DART_BODY_HEIGHT/2 below the tip
        float cosA = MathUtils.cos(rad);
        float sinA = MathUtils.sin(rad);
        float bodyCX = worldX - cosA * hh;
        float bodyCY = worldY - sinA * hh;

        // Expand AABB to fully contain rotated dart (worst case: diagonal)
        float diagHalf = (float) Math.sqrt(hw * hw + hh * hh);
        bodyRect.set(bodyCX - diagHalf, bodyCY - diagHalf, diagHalf * 2, diagHalf * 2);
    }

    /** Draw dart using ShapeRenderer (must be in FILLED mode for barrel, LINE for flight). */
    public void draw(ShapeRenderer sr) {
        Color barrel = BARREL_COLORS[skinIndex];
        Color flight = FLIGHT_COLORS[skinIndex];

        float rad  = worldAngle * MathUtils.degreesToRadians;
        float cosA = MathUtils.cos(rad);
        float sinA = MathUtils.sin(rad);

        // Perpendicular direction for width
        float perpX = -sinA;
        float perpY =  cosA;

        float hw = Constants.DART_BODY_WIDTH / 2f;

        // Tip at (worldX, worldY), body extends backward
        float bodyLen = Constants.DART_BODY_HEIGHT;
        float bx1 = worldX - cosA * bodyLen;
        float by1 = worldY - sinA * bodyLen;

        // Draw barrel as thick line (filled rect)
        sr.setColor(barrel);
        // four corners of the barrel rect
        float ax = worldX + perpX * hw;
        float ay = worldY + perpY * hw;
        float bx = worldX - perpX * hw;
        float by = worldY - perpY * hw;
        float cx = bx1 - perpX * hw;
        float cy = by1 - perpY * hw;
        float dx = bx1 + perpX * hw;
        float dy = by1 + perpY * hw;
        sr.triangle(ax, ay, bx, by, cx, cy);
        sr.triangle(ax, ay, cx, cy, dx, dy);

        // Draw flight (small triangle at tail)
        float flightLen = Constants.DART_HEIGHT - Constants.DART_BODY_HEIGHT;
        float fx  = bx1 - cosA * flightLen;
        float fy  = by1 - sinA * flightLen;
        float fhw = Constants.DART_WIDTH / 2f;
        sr.setColor(flight);
        sr.triangle(bx1, by1,
                    fx + perpX * fhw, fy + perpY * fhw,
                    fx - perpX * fhw, fy - perpY * fhw);

        // Tip highlight
        sr.setColor(Color.WHITE);
        sr.circle(worldX, worldY, 2f, 6);
    }
}
