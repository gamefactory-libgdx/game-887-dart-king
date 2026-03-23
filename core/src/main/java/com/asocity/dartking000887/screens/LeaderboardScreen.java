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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.asocity.dartking000887.Constants;
import com.asocity.dartking000887.MainGame;
import com.asocity.dartking000887.UiFactory;

public class LeaderboardScreen implements Screen {

    private static final String BG         = "ui/leaderboard_screen.png";
    private static final String SCORES_KEY = "scoresAll";

    private final MainGame game;
    private final Viewport viewport;
    private final Stage    stage;

    // -------------------------------------------------------------------------
    // Static persistence helpers
    // -------------------------------------------------------------------------

    /**
     * Saves {@code score} into the top-10 global leaderboard.
     * Scores are stored as a comma-separated list sorted descending.
     */
    public static void addScore(int score) {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        String raw = prefs.getString(SCORES_KEY, "");
        Array<Integer> list = new Array<>();
        if (!raw.isEmpty()) {
            for (String token : raw.split(",")) {
                token = token.trim();
                if (!token.isEmpty()) {
                    try { list.add(Integer.parseInt(token)); }
                    catch (NumberFormatException ignored) {}
                }
            }
        }
        list.add(score);
        list.sort((a, b) -> b - a);  // descending
        if (list.size > Constants.LEADERBOARD_MAX_ENTRIES) {
            list.removeRange(Constants.LEADERBOARD_MAX_ENTRIES, list.size - 1);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size; i++) {
            if (i > 0) sb.append(',');
            sb.append(list.get(i));
        }
        prefs.putString(SCORES_KEY, sb.toString());
        prefs.flush();
    }

    /** Returns up to {@link Constants#LEADERBOARD_MAX_ENTRIES} scores, sorted descending. */
    public static Array<Integer> getScores() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        String raw = prefs.getString(SCORES_KEY, "");
        Array<Integer> list = new Array<>();
        if (!raw.isEmpty()) {
            for (String token : raw.split(",")) {
                token = token.trim();
                if (!token.isEmpty()) {
                    try { list.add(Integer.parseInt(token)); }
                    catch (NumberFormatException ignored) {}
                }
            }
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public LeaderboardScreen(MainGame game) {
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
        // FIGMA coords: libgdxY = WORLD_HEIGHT - topY - elementH

        Label.LabelStyle headStyle  = new Label.LabelStyle(game.fontHeading, Color.WHITE);
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody, Color.WHITE);
        Label.LabelStyle accentStyle = new Label.LabelStyle(game.fontBody,
                new Color(1f, 0.792f, 0.157f, 1f));  // #FFCA28 accent
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // HALL OF FAME title: topY=50, h=50 → libgdxY=754, w=300
        Label titleLbl = new Label("HALL OF FAME", headStyle);
        titleLbl.setSize(300f, 50f);
        titleLbl.setPosition((Constants.WORLD_WIDTH - 300f) / 2f, 754f);
        titleLbl.setAlignment(Align.center);
        stage.addActor(titleLbl);

        // Score rows: topY=175, h=54 each, w=440, centered → x=(480-440)/2=20
        // libgdxY of row i = 854 - 175 - (i+1)*54  +  i*54  — but simpler:
        // row 0 bottom = 854 - 175 - 54 = 625; each subsequent row -54
        Array<Integer> scores = getScores();
        for (int i = 0; i < Constants.LEADERBOARD_MAX_ENTRIES; i++) {
            float rowY = 625f - i * 54f;
            String text;
            Label.LabelStyle style;
            if (i < scores.size) {
                text  = (i + 1) + ".   " + scores.get(i);
                style = (i == 0) ? accentStyle : bodyStyle;  // gold for #1
            } else {
                text  = (i + 1) + ".   ---";
                style = bodyStyle;
            }
            Label rowLbl = new Label(text, style);
            rowLbl.setSize(440f, 54f);
            rowLbl.setPosition(20f, rowY);
            rowLbl.setAlignment(Align.left | Align.center);
            stage.addActor(rowLbl);
        }

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
