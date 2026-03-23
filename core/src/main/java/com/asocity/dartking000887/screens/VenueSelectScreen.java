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
import com.asocity.dartking000887.SaveData;
import com.asocity.dartking000887.UiFactory;
import com.asocity.dartking000887.gameplay.VenueConfig;

public class VenueSelectScreen implements Screen {

    private final MainGame game;
    private Stage stage;

    private static final String BG = "ui/venue_select_screen.png";

    private static final VenueConfig[] VENUES = {
        VenueConfig.VENUE_LOCAL_PUB,
        VenueConfig.VENUE_TOURNAMENT,
        VenueConfig.VENUE_CHAMPIONS,
    };
    private static final String[] DIFFICULTY = { "EASY", "MEDIUM", "HARD" };

    public VenueSelectScreen(MainGame game) {
        this.game = game;
        buildStage();
    }

    private void buildStage() {
        StretchViewport vp = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage = new Stage(vp, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        // Pre-load venue thumbnails
        for (VenueConfig v : VENUES) {
            if (!game.manager.isLoaded(v.bgTexture)) {
                game.manager.load(v.bgTexture, Texture.class);
            }
        }
        game.manager.finishLoading();

        Label.LabelStyle headStyle  = new Label.LabelStyle(game.fontHeading, Color.WHITE);
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,    new Color(1f, 0.85f, 0.6f, 1f));
        Label.LabelStyle smallStyle = new Label.LabelStyle(game.fontSmall,   new Color(0.9f, 0.7f, 0.4f, 1f));
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontHeading);

        // Header
        Label header = new Label("CHOOSE VENUE", headStyle);
        header.setSize(Constants.WORLD_WIDTH, 50f);
        header.setPosition(0, 785f);
        header.setAlignment(Align.center);
        stage.addActor(header);

        // 3 venue rows stacked vertically
        // Each row: Y positions from top of screen down
        float[] rowY    = { 640f, 500f, 360f };
        float rowH      = 120f;
        float thumbW    = 150f;  // venue preview thumbnail width
        float thumbH    = 110f;  // venue preview thumbnail height
        float thumbX    = 15f;
        float textX     = thumbX + thumbW + 15f;
        float textW     = Constants.WORLD_WIDTH - textX - 15f;

        for (int i = 0; i < VENUES.length; i++) {
            final VenueConfig v = VENUES[i];
            final float ry = rowY[i];

            // Tap-target button (full row width, transparent label — color is the tinted sprite)
            TextButton rowBtn = UiFactory.makeButton("", rectStyle, Constants.WORLD_WIDTH - 20f, rowH);
            rowBtn.setPosition(10f, ry);
            final int idx = i;
            rowBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent e, Actor a) {
                    game.playSound(Constants.SFX_BUTTON_CLICK);
                    game.setScreen(new GameplayScreen(game, v));
                }
            });
            stage.addActor(rowBtn);

            // Venue name
            Label nameLbl = new Label(v.displayName, headStyle);
            nameLbl.setFontScale(0.85f);
            nameLbl.setSize(textW, 36f);
            nameLbl.setPosition(textX, ry + rowH - 42f);
            nameLbl.setAlignment(Align.left);
            stage.addActor(nameLbl);

            // Difficulty
            Label diffLbl = new Label(DIFFICULTY[i], bodyStyle);
            diffLbl.setSize(textW, 24f);
            diffLbl.setPosition(textX, ry + rowH - 70f);
            diffLbl.setAlignment(Align.left);
            stage.addActor(diffLbl);

            // Best score
            int best = SaveData.getHiScore(v.prefHiScore);
            String bestStr = best > 0 ? "Best: " + best : "No record yet";
            Label bestLbl = new Label(bestStr, smallStyle);
            bestLbl.setSize(textW, 22f);
            bestLbl.setPosition(textX, ry + 10f);
            bestLbl.setAlignment(Align.left);
            stage.addActor(bestLbl);
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
        // Background
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Draw venue thumbnails on top of each row
        float[] rowY  = { 640f, 500f, 360f };
        float thumbW  = 150f;
        float thumbH  = 110f;
        float thumbX  = 15f;
        for (int i = 0; i < VENUES.length; i++) {
            Texture thumb = game.manager.get(VENUES[i].bgTexture, Texture.class);
            game.batch.draw(thumb, thumbX, rowY[i] + 5f, thumbW, thumbH);
        }
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
    @Override public void dispose() { stage.dispose(); }
}
