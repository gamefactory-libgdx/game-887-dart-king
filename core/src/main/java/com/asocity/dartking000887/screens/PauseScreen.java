package com.asocity.dartking000887.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.asocity.dartking000887.Constants;
import com.asocity.dartking000887.MainGame;
import com.asocity.dartking000887.UiFactory;
import com.asocity.dartking000887.gameplay.VenueConfig;

/**
 * Pause overlay shown during gameplay.
 * Resume → returns to the SAME GameplayScreen instance.
 * Restart → creates a NEW GameplayScreen.
 * Main Menu → goes to MainMenuScreen.
 */
public class PauseScreen implements Screen {

    private final MainGame game;
    private final Screen   previousScreen;  // the GameplayScreen to resume
    private final VenueConfig venue;

    private Stage stage;

    // No dedicated pause_screen.png — reuse the local pub bg as neutral overlay bg
    private static final String BG = "ui/local_pub_screen.png";

    public PauseScreen(MainGame game, Screen previousScreen, VenueConfig venue) {
        this.game           = game;
        this.previousScreen = previousScreen;
        this.venue          = venue;
        buildStage();
    }

    private void buildStage() {
        StretchViewport vp = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage = new Stage(vp, game.batch);

        // Preload bg if needed
        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // ---- PAUSED title ----
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label titleLbl = new Label("PAUSED", titleStyle);
        titleLbl.setSize(Constants.WORLD_WIDTH, 60f);
        titleLbl.setPosition(0, 680f);
        titleLbl.setAlignment(Align.center);
        stage.addActor(titleLbl);

        // ---- RESUME ----
        TextButton resumeBtn = UiFactory.makeButton("RESUME", rectStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        resumeBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 530f);
        resumeBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_CLICK);
                game.setScreen(previousScreen);   // same instance → calls show()
            }
        });
        stage.addActor(resumeBtn);

        // ---- RESTART ----
        TextButton restartBtn = UiFactory.makeButton("RESTART", rectStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        restartBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 450f);
        restartBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_CLICK);
                game.setScreen(new GameplayScreen(game, venue));  // NEW instance
            }
        });
        stage.addActor(restartBtn);

        // ---- MAIN MENU ----
        TextButton menuBtn = UiFactory.makeButton("MAIN MENU", rectStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        menuBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 370f);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_BACK);
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuBtn);

        // Back key → resume
        stage.addListener(new InputListener() {
            @Override public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(previousScreen);
                    return true;
                }
                return false;
            }
        });
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background
        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Semi-transparent dark overlay
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        // (drawn via stage background label trick — keep it simple with just the bg)

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
    @Override public void dispose() { stage.dispose(); }
}
