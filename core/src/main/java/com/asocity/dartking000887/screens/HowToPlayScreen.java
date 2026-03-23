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

public class HowToPlayScreen implements Screen {

    private final MainGame game;
    private Stage stage;
    private static final String BG = "ui/how_to_play_screen.png";

    private static final String[] STEP_TITLES = {
        "Watch the Board", "Tap to Throw", "Land the Dart", "Avoid Stuck Darts"
    };
    private static final String[] STEP_DESCS = {
        "The dartboard spins. Timing is everything!",
        "Tap anywhere to release your dart toward the board.",
        "Hit rings for points. Bullseye = 100 pts!",
        "If your dart hits a stuck dart — game over!"
    };

    public HowToPlayScreen(MainGame game) {
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

        // Styles
        Label.LabelStyle headStyle  = new Label.LabelStyle(game.fontHeading, new Color(1f, 0.85f, 0.5f, 1f));
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,    Color.WHITE);
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // The background image has 4 panels in a 2x2 grid.
        // Measured from screenshot (libgdx Y = bottom of panel):
        float panelW = 195f;
        float panelH = 215f;
        float leftX  = 22f;
        float rightX = Constants.WORLD_WIDTH - 22f - panelW;
        float row1Y  = 495f;  // bottom of top panels
        float row2Y  = 258f;  // bottom of bottom panels

        addPanel(0, leftX,  row1Y, panelW, panelH, headStyle, bodyStyle);
        addPanel(1, rightX, row1Y, panelW, panelH, headStyle, bodyStyle);
        addPanel(2, leftX,  row2Y, panelW, panelH, headStyle, bodyStyle);
        addPanel(3, rightX, row2Y, panelW, panelH, headStyle, bodyStyle);

        // GOT IT button
        TextButton backBtn = UiFactory.makeButton("GOT IT!",
                rectStyle, Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        backBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 20f);
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

    private void addPanel(int index, float x, float y, float w, float h,
                          Label.LabelStyle headStyle, Label.LabelStyle bodyStyle) {
        // Step number + title at top of panel
        Label numLbl = new Label("STEP " + (index + 1), headStyle);
        numLbl.setSize(w, 30f);
        numLbl.setPosition(x, y + h - 34f);
        numLbl.setAlignment(Align.center);
        stage.addActor(numLbl);

        // Step title
        Label titleLbl = new Label(STEP_TITLES[index], bodyStyle);
        titleLbl.setFontScale(1.0f);
        titleLbl.setSize(w - 8f, 26f);
        titleLbl.setPosition(x + 4f, y + h - 64f);
        titleLbl.setAlignment(Align.center);
        stage.addActor(titleLbl);

        // Description
        Label descLbl = new Label(STEP_DESCS[index], bodyStyle);
        descLbl.setFontScale(0.9f);
        descLbl.setSize(w - 12f, h - 74f);
        descLbl.setPosition(x + 6f, y + 8f);
        descLbl.setAlignment(Align.topLeft);
        descLbl.setWrap(true);
        stage.addActor(descLbl);
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
