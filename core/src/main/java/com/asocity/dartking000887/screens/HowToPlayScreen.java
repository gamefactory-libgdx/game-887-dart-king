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

/**
 * Tutorial screen — four illustrated step panels explaining the game.
 *
 * FIGMA layout (top-Y → libgdxY):
 *   HOW TO PLAY label  top-Y=50,  h=50  → 754
 *   STEP 1 panel       top-Y=155, h=170, x=left@30  → 529
 *   STEP 2 panel       top-Y=155, h=170, x=right@30 → 529
 *   STEP 3 panel       top-Y=345, h=170, x=left@30  → 339
 *   STEP 4 panel       top-Y=345, h=170, x=right@30 → 339
 *   Tip text           top-Y=550, h=120             → 184
 *   BACK               top-Y=790, h=44              → 20
 */
public class HowToPlayScreen implements Screen {

    private final MainGame game;
    private Stage stage;

    private static final String BG = "ui/how_to_play_screen.png";

    private static final String[] STEP_TITLES = {
        "Watch the Board",
        "Tap to Throw",
        "Land the Dart",
        "Avoid Stuck Darts"
    };

    private static final String[] STEP_DESCS = {
        "The dartboard spins continuously. Timing is everything!",
        "Tap anywhere on screen to release your dart toward the board.",
        "Score points by hitting rings. Bullseye = 100 pts!",
        "A dart hitting a stuck dart ends the game immediately!"
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

        TextButton.TextButtonStyle rectStyle  = UiFactory.makeRectStyle(game.manager, game.fontBody);
        Label.LabelStyle titleStyle  = new Label.LabelStyle(game.fontHeading, Color.WHITE);
        Label.LabelStyle bodyStyle   = new Label.LabelStyle(game.fontBody,    Color.WHITE);
        Label.LabelStyle smallStyle  = new Label.LabelStyle(game.fontSmall,   Color.WHITE);

        // ---- Title ----
        Label titleLbl = new Label("HOW TO PLAY", titleStyle);
        titleLbl.setSize(Constants.WORLD_WIDTH, 50f);
        titleLbl.setPosition(0, 754f);
        titleLbl.setAlignment(Align.center);
        stage.addActor(titleLbl);

        // ---- Step panels (2×2 grid) ----
        float panelW = 195f;
        float panelH = 170f;
        float leftX  = 30f;
        float rightX = Constants.WORLD_WIDTH - 30f - panelW;
        float row1Y  = 529f;
        float row2Y  = 339f;

        addStepPanel(0, leftX,  row1Y, panelW, panelH, titleStyle, smallStyle);
        addStepPanel(1, rightX, row1Y, panelW, panelH, titleStyle, smallStyle);
        addStepPanel(2, leftX,  row2Y, panelW, panelH, titleStyle, smallStyle);
        addStepPanel(3, rightX, row2Y, panelW, panelH, titleStyle, smallStyle);

        // ---- Tip area (top-Y=550, h=120 → libgdxY=184) ----
        Label tipLbl = new Label(
                "Score zones: Double ring x2\nTriple ring x3\nBullseye = 100 pts!",
                bodyStyle);
        tipLbl.setSize(420f, 120f);
        tipLbl.setPosition((Constants.WORLD_WIDTH - 420f) / 2f, 184f);
        tipLbl.setAlignment(Align.center);
        tipLbl.setWrap(true);
        stage.addActor(tipLbl);

        // ---- BACK / GOT IT ----
        TextButton backBtn = UiFactory.makeButton("GOT IT!", rectStyle,
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

    private void addStepPanel(int index, float x, float y, float w, float h,
                               Label.LabelStyle titleStyle, Label.LabelStyle smallStyle) {
        // Step number
        Label numLbl = new Label("Step " + (index + 1), titleStyle);
        numLbl.setFontScale(0.85f);
        numLbl.setSize(w, 24f);
        numLbl.setPosition(x, y + h - 28f);
        numLbl.setAlignment(Align.center);
        stage.addActor(numLbl);

        // Step title
        Label stepTitle = new Label(STEP_TITLES[index], smallStyle);
        stepTitle.setFontScale(1.1f);
        stepTitle.setSize(w, 22f);
        stepTitle.setPosition(x, y + h - 54f);
        stepTitle.setAlignment(Align.center);
        stage.addActor(stepTitle);

        // Description
        Label descLbl = new Label(STEP_DESCS[index], smallStyle);
        descLbl.setFontScale(1.0f);
        descLbl.setSize(w - 8f, h - 60f);
        descLbl.setPosition(x + 4f, y + 4f);
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
