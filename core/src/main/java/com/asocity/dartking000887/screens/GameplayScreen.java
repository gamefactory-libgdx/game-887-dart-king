package com.asocity.dartking000887.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.asocity.dartking000887.Constants;
import com.asocity.dartking000887.MainGame;
import com.asocity.dartking000887.UiFactory;
import com.asocity.dartking000887.gameplay.GameplayStage;
import com.asocity.dartking000887.gameplay.VenueConfig;

/**
 * Main gameplay screen — handles all three venues via VenueConfig parameter.
 * Layout: background image → dartboard (ShapeRenderer) → HUD stage.
 *
 * CRITICAL pause pattern: stage.act() runs ALWAYS; update() is gated by !paused.
 */
public class GameplayScreen implements Screen, GameplayStage.Listener {

    private final MainGame game;
    private final VenueConfig venue;

    private Stage stage;
    private ShapeRenderer shapeRenderer;

    private GameplayStage gameplay;

    // HUD labels (updated each frame)
    private Label scoreLbl;
    private Label dartsLbl;
    private Label venueLbl;

    // Red vignette state
    private float vignetteAlpha   = 0f;
    private boolean showVignette  = false;
    private float vignetteTimer   = 0f;

    // Near-miss red edge flash
    private float edgeFlashAlpha  = 0f;

    private boolean gameOverHandled = false;

    public GameplayScreen(MainGame game, VenueConfig venue) {
        this.game  = game;
        this.venue = venue;
    }

    // -----------------------------------------------------------------------
    // Screen lifecycle
    // -----------------------------------------------------------------------

    @Override
    public void show() {
        // Preload bg
        if (!game.manager.isLoaded(venue.bgTexture)) {
            game.manager.load(venue.bgTexture, Texture.class);
            game.manager.finishLoading();
        }

        shapeRenderer = new ShapeRenderer();

        // Build HUD stage
        StretchViewport vp = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage = new Stage(vp, game.batch);

        buildHud();

        // CRITICAL: always re-register input in show()
        Gdx.input.setInputProcessor(stage);

        // Init gameplay
        gameplay = new GameplayStage(game, venue);
        gameplay.listener = this;
        gameplay.init();

        gameOverHandled = false;
        vignetteAlpha = 0f;
        showVignette  = false;
        edgeFlashAlpha = 0f;

        game.playMusic(Constants.MUSIC_GAMEPLAY);
    }

    private void buildHud() {
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody, Color.WHITE);
        Label.LabelStyle smallStyle = new Label.LabelStyle(game.fontSmall, Color.WHITE);

        // FIGMA: SCORE label | top-Y=30, x=right@20, h=44 → libgdxY = 854-30-44 = 780
        scoreLbl = new Label("0", bodyStyle);
        scoreLbl.setSize(140f, 44f);
        scoreLbl.setPosition(Constants.WORLD_WIDTH - 160f, 780f);
        scoreLbl.setAlignment(Align.right);
        stage.addActor(scoreLbl);

        // FIGMA: DARTS label | top-Y=30, x=left@20, h=44 → libgdxY = 780
        dartsLbl = new Label("0 thrown", bodyStyle);
        dartsLbl.setSize(140f, 44f);
        dartsLbl.setPosition(20f, 780f);
        dartsLbl.setAlignment(Align.left);
        stage.addActor(dartsLbl);

        // Venue name — center top
        venueLbl = new Label(venue.displayName, smallStyle);
        venueLbl.setSize(Constants.WORLD_WIDTH, 30f);
        venueLbl.setPosition(0, 810f);
        venueLbl.setAlignment(Align.center);
        stage.addActor(venueLbl);

        // FIGMA: PAUSE | top-Y=20, x=centered, size=60x44 → libgdxY = 854-20-44 = 790
        TextButton.TextButtonStyle roundStyle = UiFactory.makeRoundStyle(game.manager, game.fontBody);
        TextButton pauseBtn = UiFactory.makeButton("||", roundStyle,
                Constants.BTN_ROUND_W, Constants.BTN_ROUND_H);
        pauseBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_ROUND_W) / 2f, 790f);
        pauseBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                if (gameplay.gameOver) return;
                game.playSound(Constants.SFX_BUTTON_CLICK);
                gameplay.pause();
                game.setScreen(new PauseScreen(game, GameplayScreen.this, venue));
            }
        });
        stage.addActor(pauseBtn);

        // Back key → pause → menu
        stage.addListener(new InputListener() {
            @Override public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    if (!gameplay.gameOver) {
                        gameplay.pause();
                        game.setScreen(new PauseScreen(game, GameplayScreen.this, venue));
                    } else {
                        game.setScreen(new MainMenuScreen(game));
                    }
                    return true;
                }
                return false;
            }
        });

        // TAP to throw dart — anywhere on stage (not captured by a button)
        stage.addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y,
                                               int pointer, int button) {
                // Only throw if tap is in lower portion (not on HUD buttons)
                if (y < 760f && !gameplay.gameOver) {
                    gameplay.throwDart();
                }
                return true;
            }
        });
    }

    // -----------------------------------------------------------------------
    // Render
    // -----------------------------------------------------------------------

    @Override
    public void render(float delta) {
        // Update game logic
        if (!gameplay.gameOver && !gameplay.paused) {
            gameplay.update(delta);
        }

        // Update visual timers
        if (edgeFlashAlpha > 0) edgeFlashAlpha = Math.max(0, edgeFlashAlpha - delta * 3f);
        if (showVignette) {
            vignetteTimer += delta;
            vignetteAlpha = Math.min(vignetteTimer / 0.8f, 0.55f);
        }

        // Update HUD labels
        scoreLbl.setText(String.valueOf(gameplay.score));
        dartsLbl.setText(gameplay.dartsThrown + " thrown");

        // Check game over → navigate after particles settle (or immediately)
        if (gameplay.gameOver && !gameOverHandled && vignetteTimer > 1.5f) {
            handleGameOver();
        }

        // --- Clear ---
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- Background ---
        game.batch.begin();
        game.batch.draw(game.manager.get(venue.bgTexture, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // --- Dartboard + dart (ShapeRenderer) ---
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        gameplay.dartboard.draw(shapeRenderer);

        // Draw flying dart (or dart at launch ready position)
        if (gameplay.flyingDart.active) {
            gameplay.flyingDart.draw(shapeRenderer);
        } else if (!gameplay.gameOver) {
            gameplay.flyingDart.drawAtLaunch(shapeRenderer);
        }

        // Particles
        gameplay.particles.draw(shapeRenderer);
        shapeRenderer.end();

        // --- Edge flash (near-miss) ---
        if (edgeFlashAlpha > 0) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.9f, 0.1f, 0.1f, edgeFlashAlpha * 0.4f);
            shapeRenderer.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
            shapeRenderer.end();
        }

        // --- Red vignette (game over) ---
        if (vignetteAlpha > 0) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.5f, 0.0f, 0.0f, vignetteAlpha);
            shapeRenderer.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
            shapeRenderer.end();
        }

        // --- HUD stage --- ALWAYS runs, outside any paused guard
        stage.act(delta);
        stage.draw();
    }

    // -----------------------------------------------------------------------
    // Game Over Handling
    // -----------------------------------------------------------------------

    private void handleGameOver() {
        gameOverHandled = true;
        int trophiesEarned = Math.min(gameplay.score / 100, Constants.MAX_TROPHIES_PER_SESSION);
        // GameOverScreen handles persistence (hi-score, trophies, leaderboard)
        game.setScreen(new GameOverScreen(game, gameplay.score, trophiesEarned));
    }

    // -----------------------------------------------------------------------
    // GameplayStage.Listener
    // -----------------------------------------------------------------------

    @Override
    public void onDartLanded(float worldX, float worldY, int points) {
        // flash handled by particle system in GameplayStage
    }

    @Override
    public void onNearMiss() {
        edgeFlashAlpha = 1f;
    }

    @Override
    public void onGameOver() {
        showVignette  = true;
        vignetteTimer = 0f;
        vignetteAlpha = 0f;
    }

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause()  { gameplay.pause(); }
    @Override public void resume() {}   // resume only via PauseScreen button
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
