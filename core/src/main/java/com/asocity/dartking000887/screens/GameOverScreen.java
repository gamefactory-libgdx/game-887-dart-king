package com.asocity.dartking000887.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.asocity.dartking000887.Constants;
import com.asocity.dartking000887.MainGame;
import com.asocity.dartking000887.UiFactory;

/**
 * Game-over summary screen.
 *
 * @param score  final score this run
 * @param extra  trophies earned this run (floor(score / 100), capped at 50)
 */
public class GameOverScreen implements Screen {

    private static final String BG           = "ui/game_over_screen.png";
    private static final String PREF_HI_KEY  = "hiScoreAll";

    private final MainGame game;
    private final int      score;
    private final int      extra;          // trophies earned
    private final int      personalBest;
    private final boolean  isNewBest;

    private final Viewport viewport;
    private final Stage    stage;

    public GameOverScreen(MainGame game, int score, int extra) {
        this.game  = game;
        this.score = score;
        this.extra = extra;

        // Persist score
        LeaderboardScreen.addScore(score);

        // Update global hi-score
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int saved = prefs.getInteger(PREF_HI_KEY, 0);
        if (score > saved) {
            prefs.putInteger(PREF_HI_KEY, score);
            prefs.flush();
            personalBest = score;
            isNewBest    = true;
        } else {
            personalBest = saved;
            isNewBest    = false;
        }

        // Update trophy balance
        int balance = prefs.getInteger(Constants.PREF_TROPHIES_BALANCE, 0);
        prefs.putInteger(Constants.PREF_TROPHIES_BALANCE, balance + extra);
        prefs.flush();

        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage    = new Stage(viewport, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        buildUI();
        registerInput();
        game.playMusicOnce(Constants.MUSIC_GAME_OVER);
    }

    // -------------------------------------------------------------------------
    // UI construction
    // -------------------------------------------------------------------------

    private void buildUI() {
        // FIGMA coords: libgdxY = WORLD_HEIGHT - topY - elementH

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label.LabelStyle headStyle  = new Label.LabelStyle(game.fontHeading,
                isNewBest ? new Color(1f, 0.792f, 0.157f, 1f) : Color.WHITE);  // accent if new best
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody, Color.WHITE);
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // GAME OVER: topY=80, h=60 → libgdxY=714, w=300
        Label gameOverLbl = new Label("GAME OVER", titleStyle);
        gameOverLbl.setSize(300f, 60f);
        gameOverLbl.setPosition((Constants.WORLD_WIDTH - 300f) / 2f, 714f);
        gameOverLbl.setAlignment(Align.center);
        stage.addActor(gameOverLbl);

        // SCORE: topY=200, h=50 → libgdxY=604, w=240
        Label scoreLbl = new Label("SCORE: " + score, headStyle);
        scoreLbl.setSize(240f, 50f);
        scoreLbl.setPosition((Constants.WORLD_WIDTH - 240f) / 2f, 604f);
        scoreLbl.setAlignment(Align.center);
        stage.addActor(scoreLbl);

        // BEST: topY=265, h=40 → libgdxY=549, w=200
        String bestText = isNewBest ? "NEW BEST: " + personalBest : "BEST: " + personalBest;
        Label bestLbl = new Label(bestText, bodyStyle);
        bestLbl.setSize(200f, 40f);
        bestLbl.setPosition((Constants.WORLD_WIDTH - 200f) / 2f, 549f);
        bestLbl.setAlignment(Align.center);
        stage.addActor(bestLbl);

        // TROPHIES: topY=340, h=40 → libgdxY=474, w=200
        Label trophiesLbl = new Label("TROPHIES: +" + extra, bodyStyle);
        trophiesLbl.setSize(200f, 40f);
        trophiesLbl.setPosition((Constants.WORLD_WIDTH - 200f) / 2f, 474f);
        trophiesLbl.setAlignment(Align.center);
        stage.addActor(trophiesLbl);

        // RETRY: topY=560, h=56 → libgdxY=238, w=240
        TextButton retryBtn = UiFactory.makeButton("RETRY", rectStyle, 240f, 56f);
        retryBtn.setPosition((Constants.WORLD_WIDTH - 240f) / 2f, 238f);
        retryBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_CLICK);
                game.setScreen(new VenueSelectScreen(game));
            }
        });
        stage.addActor(retryBtn);

        // MENU: topY=636, h=56 → libgdxY=162, w=240
        TextButton menuBtn = UiFactory.makeButton("MENU", rectStyle, 240f, 56f);
        menuBtn.setPosition((Constants.WORLD_WIDTH - 240f) / 2f, 162f);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_CLICK);
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuBtn);
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private void registerInput() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.playSound(Constants.SFX_BUTTON_BACK);
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(stage);
    }

    // -------------------------------------------------------------------------
    // Screen
    // -------------------------------------------------------------------------

    @Override
    public void show() {
        registerInput();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
