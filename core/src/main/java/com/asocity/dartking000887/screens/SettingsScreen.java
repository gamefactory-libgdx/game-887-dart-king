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

public class SettingsScreen implements Screen {

    private static final String BG = "ui/settings_screen.png";

    private final MainGame  game;
    private final Viewport  viewport;
    private final Stage     stage;

    // Toggle buttons — updated when state changes
    private TextButton musicToggleBtn;
    private TextButton sfxToggleBtn;

    public SettingsScreen(MainGame game) {
        this.game = game;
        viewport  = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage     = new Stage(viewport, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        // Restore saved prefs into game state
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        game.musicEnabled = prefs.getBoolean(Constants.PREF_MUSIC_ON, true);
        game.sfxEnabled   = prefs.getBoolean(Constants.PREF_SFX_ON,   true);

        buildUI();
        registerInput();
        game.playMusic(Constants.MUSIC_MENU);
    }

    // -------------------------------------------------------------------------
    // UI construction
    // -------------------------------------------------------------------------

    private void buildUI() {
        // FIGMA coords: libgdxY = WORLD_HEIGHT - topY - elementH

        Label.LabelStyle headStyle  = new Label.LabelStyle(game.fontHeading, Color.WHITE);
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody, Color.WHITE);
        TextButton.TextButtonStyle rectStyle  = UiFactory.makeRectStyle(game.manager, game.fontBody);
        TextButton.TextButtonStyle roundStyle = UiFactory.makeRoundStyle(game.manager, game.fontSmall);

        // SETTINGS title: topY=60, h=50 → libgdxY=744, w=260
        Label titleLbl = new Label("SETTINGS", headStyle);
        titleLbl.setSize(260f, 50f);
        titleLbl.setPosition((Constants.WORLD_WIDTH - 260f) / 2f, 744f);
        titleLbl.setAlignment(Align.center);
        stage.addActor(titleLbl);

        // ---- MUSIC row: topY=200, h=44 → libgdxY=610 ----
        // Label: left@60, w=200
        Label musicLbl = new Label("MUSIC", bodyStyle);
        musicLbl.setSize(200f, 44f);
        musicLbl.setPosition(60f, 610f);
        musicLbl.setAlignment(Align.left | Align.center);
        stage.addActor(musicLbl);

        // Toggle button: right@60, w=80, x = 480-60-80=340
        musicToggleBtn = UiFactory.makeButton(
                game.musicEnabled ? "ON" : "OFF", roundStyle, 80f, 44f);
        musicToggleBtn.setPosition(340f, 610f);
        musicToggleBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.musicEnabled = !game.musicEnabled;
                musicToggleBtn.setText(game.musicEnabled ? "ON" : "OFF");
                Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
                prefs.putBoolean(Constants.PREF_MUSIC_ON, game.musicEnabled);
                prefs.flush();
                if (game.currentMusic != null) {
                    if (game.musicEnabled) game.currentMusic.play();
                    else game.currentMusic.pause();
                }
                game.playSound(Constants.SFX_TOGGLE);
            }
        });
        stage.addActor(musicToggleBtn);

        // ---- SFX row: topY=270, h=44 → libgdxY=540 ----
        Label sfxLbl = new Label("SFX", bodyStyle);
        sfxLbl.setSize(200f, 44f);
        sfxLbl.setPosition(60f, 540f);
        sfxLbl.setAlignment(Align.left | Align.center);
        stage.addActor(sfxLbl);

        sfxToggleBtn = UiFactory.makeButton(
                game.sfxEnabled ? "ON" : "OFF", roundStyle, 80f, 44f);
        sfxToggleBtn.setPosition(340f, 540f);
        sfxToggleBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.sfxEnabled = !game.sfxEnabled;
                sfxToggleBtn.setText(game.sfxEnabled ? "ON" : "OFF");
                Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
                prefs.putBoolean(Constants.PREF_SFX_ON, game.sfxEnabled);
                prefs.flush();
                // Play toggle sound only if sfx was just enabled
                if (game.sfxEnabled) game.playSound(Constants.SFX_TOGGLE);
            }
        });
        stage.addActor(sfxToggleBtn);

        // RESET DATA: topY=580, h=50 → libgdxY=224, w=240
        TextButton resetBtn = UiFactory.makeButton("RESET DATA", rectStyle, 240f, 50f);
        resetBtn.setPosition((Constants.WORLD_WIDTH - 240f) / 2f, 224f);
        resetBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_CLICK);
                Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
                prefs.clear();
                prefs.flush();
                game.musicEnabled = true;
                game.sfxEnabled   = true;
                musicToggleBtn.setText("ON");
                sfxToggleBtn.setText("ON");
            }
        });
        stage.addActor(resetBtn);

        // BACK: topY=800, h=44 → libgdxY=10, left@20, w=120
        TextButton backBtn = UiFactory.makeButton("BACK", rectStyle, 120f, 44f);
        backBtn.setPosition(20f, 10f);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_BACK);
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backBtn);
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
        game.playMusic(Constants.MUSIC_MENU);
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
