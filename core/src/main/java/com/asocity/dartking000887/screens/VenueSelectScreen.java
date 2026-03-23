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

/**
 * Screen for choosing which venue (difficulty) to play.
 */
public class VenueSelectScreen implements Screen {

    private final MainGame game;
    private Stage stage;

    private static final String BG = "ui/venue_select_screen.png";

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

        TextButton.TextButtonStyle rectStyle  = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // ---- Header ----
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontHeading, Color.WHITE);
        Label header = new Label("CHOOSE VENUE", titleStyle);
        header.setWidth(Constants.WORLD_WIDTH);
        header.setPosition(0, 854f - 60f - 50f);
        header.setAlignment(Align.center);
        stage.addActor(header);

        // ---- Venue cards: three equal columns ----
        // top-Y=250, size=120x160 → libgdxY = 854 - 250 - 160 = 444
        float cardW = 120f;
        float cardH = 160f;
        float cardY = 444f;
        float margin = 40f;

        // LOCAL PUB
        addVenueCard(VenueConfig.VENUE_LOCAL_PUB,
                margin, cardY, cardW, cardH, rectStyle, "★", Constants.PREF_HISCORE_LOCAL_PUB);

        // TOURNAMENT HALL
        addVenueCard(VenueConfig.VENUE_TOURNAMENT,
                (Constants.WORLD_WIDTH - cardW) / 2f, cardY, cardW, cardH, rectStyle,
                "★★", Constants.PREF_HISCORE_TOURNAMENT);

        // CHAMPIONS ARENA
        addVenueCard(VenueConfig.VENUE_CHAMPIONS,
                Constants.WORLD_WIDTH - margin - cardW, cardY, cardW, cardH, rectStyle,
                "★★★", Constants.PREF_HISCORE_CHAMPIONS);

        // ---- BACK ----
        // top-Y=790, h=44 → libgdxY = 20
        TextButton backBtn = UiFactory.makeButton("BACK", rectStyle,
                Constants.BTN_SMALL_W, Constants.BTN_SMALL_H);
        backBtn.setPosition(20f, 20f);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_BACK);
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backBtn);

        // Back key
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

    private void addVenueCard(final VenueConfig venue, float x, float y,
                               float w, float h,
                               TextButton.TextButtonStyle style,
                               String stars, String hiScorePref) {
        // Card button — tapping launches gameplay
        TextButton card = UiFactory.makeButton(
                venue.displayName + "\n" + stars + "\nBest: " + SaveData.getHiScore(hiScorePref),
                style, w, h);
        card.setPosition(x, y);
        card.getLabel().setWrap(true);
        card.getLabel().setAlignment(Align.center);
        card.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound(Constants.SFX_BUTTON_CLICK);
                game.setScreen(new GameplayScreen(game, venue));
            }
        });
        stage.addActor(card);
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
    @Override public void dispose() { stage.dispose(); }
}
