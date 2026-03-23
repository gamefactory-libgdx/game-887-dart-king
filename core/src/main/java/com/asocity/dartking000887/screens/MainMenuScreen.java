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
import com.badlogic.gdx.utils.viewport.Viewport;
import com.asocity.dartking000887.Constants;
import com.asocity.dartking000887.MainGame;
import com.asocity.dartking000887.UiFactory;

public class MainMenuScreen implements Screen {

    private static final String BG = "ui/main_menu_screen.png";

    private final MainGame game;
    private final Viewport viewport;
    private final Stage    stage;

    public MainMenuScreen(MainGame game) {
        this.game = game;
        viewport  = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage     = new Stage(viewport, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        buildUI();
        registerInput();
        game.playMusic(Constants.MUSIC_MENU);
    }

    // -------------------------------------------------------------------------
    // UI construction
    // -------------------------------------------------------------------------

    private void buildUI() {
        // FIGMA coords converted: libgdxY = WORLD_HEIGHT - topY - elementH

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        TextButton.TextButtonStyle rectStyle  = UiFactory.makeRectStyle(game.manager, game.fontBody);
        TextButton.TextButtonStyle roundStyle = UiFactory.makeRoundStyle(game.manager, game.fontSmall);

        // Title: topY=80, h=70  → libgdxY=704, w=340
        Label titleLbl = new Label("DART KING", titleStyle);
        titleLbl.setSize(340f, 70f);
        titleLbl.setPosition((Constants.WORLD_WIDTH - 340f) / 2f, 704f);
        titleLbl.setAlignment(Align.center);
        stage.addActor(titleLbl);

        // PLAY: topY=320, h=60 → libgdxY=474, w=260
        TextButton playBtn = UiFactory.makeButton("PLAY", rectStyle, 260f, 60f);
        playBtn.setPosition((Constants.WORLD_WIDTH - 260f) / 2f, 474f);
        playBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_CLICK);
                game.setScreen(new VenueSelectScreen(game));
            }
        });
        stage.addActor(playBtn);

        // VENUE SELECT: topY=400, h=60 → libgdxY=394, w=260
        TextButton venueBtn = UiFactory.makeButton("VENUE SELECT", rectStyle, 260f, 60f);
        venueBtn.setPosition((Constants.WORLD_WIDTH - 260f) / 2f, 394f);
        venueBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_CLICK);
                game.setScreen(new VenueSelectScreen(game));
            }
        });
        stage.addActor(venueBtn);

        // SHOP: topY=480, h=60 → libgdxY=314, w=260
        TextButton shopBtn = UiFactory.makeButton("SHOP", rectStyle, 260f, 60f);
        shopBtn.setPosition((Constants.WORLD_WIDTH - 260f) / 2f, 314f);
        shopBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_CLICK);
                game.setScreen(new ShopScreen(game));
            }
        });
        stage.addActor(shopBtn);

        // LEADERBOARD: topY=560, h=60 → libgdxY=234, w=260
        TextButton lbBtn = UiFactory.makeButton("LEADERBOARD", rectStyle, 260f, 60f);
        lbBtn.setPosition((Constants.WORLD_WIDTH - 260f) / 2f, 234f);
        lbBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_CLICK);
                game.setScreen(new LeaderboardScreen(game));
            }
        });
        stage.addActor(lbBtn);

        // HOW TO PLAY: topY=640, h=60 → libgdxY=154, w=260
        TextButton helpBtn = UiFactory.makeButton("HOW TO PLAY", rectStyle, 260f, 60f);
        helpBtn.setPosition((Constants.WORLD_WIDTH - 260f) / 2f, 154f);
        helpBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_CLICK);
                game.setScreen(new HowToPlayScreen(game));
            }
        });
        stage.addActor(helpBtn);

        // SETTINGS: topY=790, h=44, w=100, right@20 → x=360, libgdxY=20
        TextButton settingsBtn = UiFactory.makeButton("SETTINGS", roundStyle, 100f, 44f);
        settingsBtn.setPosition(360f, 20f);
        settingsBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_CLICK);
                game.setScreen(new SettingsScreen(game));
            }
        });
        stage.addActor(settingsBtn);
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private void registerInput() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    Gdx.app.exit();
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
