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
import com.asocity.dartking000887.SaveData;
import com.asocity.dartking000887.UiFactory;
import com.asocity.dartking000887.gameplay.VenueConfig;

public class VenueSelectScreen implements Screen {

    private final MainGame game;
    private Stage stage;
    private ShapeRenderer sr;

    private static final String BG = "ui/venue_select_screen.png";

    // Card colors per venue
    private static final Color[] CARD_COLORS = {
        new Color(0.55f, 0.25f, 0.05f, 0.85f),  // pub  — dark amber
        new Color(0.70f, 0.35f, 0.06f, 0.85f),  // tour — orange-brown
        new Color(0.85f, 0.20f, 0.05f, 0.85f),  // champ— deep red
    };
    private static final String[] STARS = { "\u2605", "\u2605\u2605", "\u2605\u2605\u2605" };
    private static final VenueConfig[] VENUES = {
        VenueConfig.VENUE_LOCAL_PUB,
        VenueConfig.VENUE_TOURNAMENT,
        VenueConfig.VENUE_CHAMPIONS
    };

    public VenueSelectScreen(MainGame game) {
        this.game = game;
        sr = new ShapeRenderer();
        buildStage();
    }

    private void buildStage() {
        StretchViewport vp = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage = new Stage(vp, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontHeading);
        Label.LabelStyle headStyle  = new Label.LabelStyle(game.fontHeading, Color.WHITE);
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,    new Color(1f, 0.9f, 0.7f, 1f));

        // Header
        Label header = new Label("CHOOSE VENUE", headStyle);
        header.setSize(Constants.WORLD_WIDTH, 50f);
        header.setPosition(0, 780f);
        header.setAlignment(Align.center);
        stage.addActor(header);

        // Three vertical venue rows: full-width tap buttons
        // Row positions from top: 680, 555, 430 (each 100px tall, 15px gap)
        float[] rowY = { 630f, 500f, 370f };
        float cardW  = 440f;
        float cardH  = 110f;
        float cardX  = (Constants.WORLD_WIDTH - cardW) / 2f;

        for (int i = 0; i < VENUES.length; i++) {
            final VenueConfig v = VENUES[i];
            int best = SaveData.getHiScore(v.prefHiScore);
            String bestStr = best > 0 ? "Best: " + best : "Not played yet";
            String label   = v.displayName + "\n" + STARS[i] + "   " + bestStr;

            TextButton btn = UiFactory.makeButton(label, rectStyle, cardW, cardH);
            btn.setPosition(cardX, rowY[i]);
            btn.getLabel().setWrap(false);
            btn.getLabel().setAlignment(Align.center);
            btn.getLabel().setFontScale(i == 2 ? 0.75f : 0.85f); // champ name is longer
            final int idx = i;
            btn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent e, Actor a) {
                    game.playSound(Constants.SFX_BUTTON_CLICK);
                    game.setScreen(new GameplayScreen(game, v));
                }
            });
            stage.addActor(btn);
        }

        // BACK button
        TextButton backBtn = UiFactory.makeButton("BACK",
                UiFactory.makeRectStyle(game.manager, game.fontBody),
                Constants.BTN_SMALL_W, Constants.BTN_SMALL_H);
        backBtn.setPosition(20f, 20f);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_BACK);
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backBtn);

        stage.addListener(new InputListener() {
            @Override public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        });
    }

    @Override public void show() {
        game.playMusic(Constants.MUSIC_MENU);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
    @Override public void dispose() { stage.dispose(); if (sr != null) sr.dispose(); }
}
