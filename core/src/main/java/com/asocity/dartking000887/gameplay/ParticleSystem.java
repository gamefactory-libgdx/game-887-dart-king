package com.asocity.dartking000887.gameplay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * Lightweight manual particle system drawn entirely with ShapeRenderer.
 * No .p files required.
 */
public class ParticleSystem {

    private static class Particle {
        float x, y;
        float vx, vy;
        float life;       // remaining life in seconds
        float maxLife;
        float size;
        float r, g, b;
        int type;         // 0=star 1=flash 2=shard 3=smoke
    }

    private final Array<Particle> particles = new Array<>();

    // -----------------------------------------------------------------------
    // Spawn methods
    // -----------------------------------------------------------------------

    /** Gold star burst — bullseye hit. */
    public void spawnBullseye(float x, float y) {
        for (int i = 0; i < 12; i++) {
            Particle p = new Particle();
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(60f, 180f);
            p.x = x; p.y = y;
            p.vx = MathUtils.cos(angle) * speed;
            p.vy = MathUtils.sin(angle) * speed;
            p.life = p.maxLife = MathUtils.random(0.5f, 1.0f);
            p.size = MathUtils.random(4f, 8f);
            p.r = 1f; p.g = 0.85f; p.b = 0.10f;
            p.type = 0;
            particles.add(p);
        }
    }

    /** White flash — normal hit. */
    public void spawnHit(float x, float y) {
        for (int i = 0; i < 6; i++) {
            Particle p = new Particle();
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(30f, 90f);
            p.x = x; p.y = y;
            p.vx = MathUtils.cos(angle) * speed;
            p.vy = MathUtils.sin(angle) * speed;
            p.life = p.maxLife = MathUtils.random(0.2f, 0.5f);
            p.size = MathUtils.random(2f, 5f);
            p.r = 1f; p.g = 1f; p.b = 1f;
            p.type = 1;
            particles.add(p);
        }
    }

    /** Dart shatter + smoke — game over. */
    public void spawnBreak(float x, float y) {
        // Shards
        for (int i = 0; i < 8; i++) {
            Particle p = new Particle();
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float speed = MathUtils.random(80f, 200f);
            p.x = x; p.y = y;
            p.vx = MathUtils.cos(angle) * speed;
            p.vy = MathUtils.sin(angle) * speed - 50f;
            p.life = p.maxLife = MathUtils.random(0.4f, 0.9f);
            p.size = MathUtils.random(3f, 7f);
            p.r = 0.7f; p.g = 0.7f; p.b = 0.75f;
            p.type = 2;
            particles.add(p);
        }
        // Smoke puffs
        for (int i = 0; i < 5; i++) {
            Particle p = new Particle();
            p.x = x + MathUtils.random(-15f, 15f);
            p.y = y + MathUtils.random(-15f, 15f);
            p.vx = MathUtils.random(-20f, 20f);
            p.vy = MathUtils.random(10f, 40f);
            p.life = p.maxLife = MathUtils.random(0.5f, 1.2f);
            p.size = MathUtils.random(8f, 18f);
            p.r = 0.55f; p.g = 0.52f; p.b = 0.50f;
            p.type = 3;
            particles.add(p);
        }
    }

    // -----------------------------------------------------------------------
    // Update / Draw
    // -----------------------------------------------------------------------

    public void update(float delta) {
        for (int i = particles.size - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.life -= delta;
            if (p.life <= 0) {
                particles.removeIndex(i);
                continue;
            }
            p.x += p.vx * delta;
            p.y += p.vy * delta;
            // Gravity on shards
            if (p.type == 2) p.vy -= 200f * delta;
        }
    }

    public void draw(ShapeRenderer sr) {
        for (Particle p : particles) {
            float alpha = p.life / p.maxLife;
            sr.setColor(p.r, p.g, p.b, alpha);
            if (p.type == 3) {
                // Smoke: growing circle
                float s = p.size * (1f + (1f - alpha) * 0.8f);
                sr.circle(p.x, p.y, s, 10);
            } else {
                sr.circle(p.x, p.y, p.size * alpha, 6);
            }
        }
    }

    public boolean hasParticles() {
        return particles.size > 0;
    }

    public void clear() {
        particles.clear();
    }
}
