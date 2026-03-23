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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

    // Row geometry — thumbnail on left, text on right
    private static final float ROW_H    = 120f;
    private static final float THUMB_W  = 155f;
    private static final float THUMB_H  = 108f;
    private static final float THUMB_X  = 12f;
    private static final float[] ROW_Y  = { 636f, 494f, 352f };

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
        for (VenueConfig v : VENUES) {
            if (!game.manager.isLoaded(v.bgTexture)) {
                game.manager.load(v.bgTexture, Texture.class);
            }
        }
        game.manager.finishLoading();

        Label.LabelStyle headStyle  = new Label.LabelStyle(game.fontHeading, Color.WHITE);
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,    new Color(1f, 0.85f, 0.6f, 1f));
        Label.LabelStyle smallStyle = new Label.LabelStyle(game.fontSmall,   new Color(0.9f, 0.7f, 0.4f, 1f));

        // Header
        Label header = new Label("CHOOSE VENUE", headStyle);
        header.setSize(Constants.WORLD_WIDTH, 50f);
        header.setPosition(0, 790f);
        header.setAlignment(Align.center);
        stage.addActor(header);

        float textX = THUMB_X + THUMB_W + 12f;
        float textW = Constants.WORLD_WIDTH - textX - 10f;

        for (int i = 0; i < VENUES.length; i++) {
            final VenueConfig v = VENUES[i];
            final float ry = ROW_Y[i];

            // Invisible touch-area covering the full row
            Image touchArea = new Image();
            touchArea.setSize(Constants.WORLD_WIDTH, ROW_H);
            touchArea.setPosition(0, ry);
            touchArea.setTouchable(Touchable.enabled);
            touchArea.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float mx, float my) {
                    game.playSound(Constants.SFX_BUTTON_CLICK);
                    game.setScreen(new GameplayScreen(game, v));
                }
            });
            stage.addActor(touchArea);

            // Venue name
            Label nameLbl = new Label(v.displayName, headStyle);
            nameLbl.setFontScale(0.9f);
            nameLbl.setSize(textW, 36f);
            nameLbl.setPosition(textX, ry + ROW_H - 42f);
            nameLbl.setAlignment(Align.left);
            stage.addActor(nameLbl);

            // Difficulty
            Label diffLbl = new Label(DIFFICULTY[i], bodyStyle);
            diffLbl.setSize(textW, 26f);
            diffLbl.setPosition(textX, ry + ROW_H - 72f);
            diffLbl.setAlignment(Align.left);
            stage.addActor(diffLbl);

            // Best score
            int best = SaveData.getHiScore(v.prefHiScore);
            String bestStr = best > 0 ? "Best: " + best : "No record yet";
            Label bestLbl = new Label(bestStr, bodyStyle);
            bestLbl.setSize(textW, 28f);
            bestLbl.setPosition(textX, ry + 8f);
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
        // 1. Background
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        // 2. Venue thumbnails (drawn BEFORE stage so stage labels appear on top)
        for (int i = 0; i < VENUES.length; i++) {
            Texture thumb = game.manager.get(VENUES[i].bgTexture, Texture.class);
            game.batch.draw(thumb, THUMB_X, ROW_Y[i] + (ROW_H - THUMB_H) / 2f, THUMB_W, THUMB_H);
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
