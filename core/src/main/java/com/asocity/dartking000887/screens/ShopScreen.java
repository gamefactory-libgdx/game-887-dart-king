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

/**
 * Shop screen — 6 dart skins unlockable with trophies.
 *
 * Layout (FIGMA):
 *   DART SHOP label  top-Y=50, h=50  → libgdxY=754
 *   TROPHIES label   top-Y=30, h=40  → libgdxY=784
 *   SKIN 1 card      top-Y=160,h=180, x=left@40   → libgdxY=514
 *   SKIN 2 card      top-Y=160,h=180, x=right@40  → libgdxY=514
 *   SKIN 3 card      top-Y=360,h=180, x=left@40   → libgdxY=314
 *   SKIN 4 card      top-Y=360,h=180, x=right@40  → libgdxY=314
 *   SKIN 5 card      top-Y=560,h=180, x=left@40   → libgdxY=114
 *   SKIN 6 card      top-Y=560,h=180, x=right@40  → libgdxY=114
 *   BACK             top-Y=800,h=44  → libgdxY=10
 */
public class ShopScreen implements Screen {

    private final MainGame game;
    private Stage stage;
    private ShapeRenderer shapeRenderer;

    private static final String BG = "ui/shop_screen.png";

    private static final String[] SKIN_NAMES = {
        "Classic",
        "Copper Strike",
        "Golden Arrow",
        "Neon Flash",
        "Dragon Fire",
        "Platinum King"
    };

    // Barrel colors for preview (matches StuckDart/FlyingDart)
    private static final Color[] BARREL_COLORS = {
        new Color(0.75f, 0.75f, 0.80f, 1f),
        new Color(0.72f, 0.40f, 0.15f, 1f),
        new Color(0.95f, 0.80f, 0.10f, 1f),
        new Color(0.10f, 0.50f, 0.95f, 1f),
        new Color(0.12f, 0.08f, 0.08f, 1f),
        new Color(0.85f, 0.85f, 0.90f, 1f),
    };

    private Label trophyLbl;

    public ShopScreen(MainGame game) {
        this.game = game;
        buildStage();
    }

    private void buildStage() {
        StretchViewport vp = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage = new Stage(vp, game.batch);
        shapeRenderer = new ShapeRenderer();

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontHeading, Color.WHITE);
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,    Color.WHITE);
        Label.LabelStyle smallStyle = new Label.LabelStyle(game.fontSmall,   Color.WHITE);

        // Title
        Label titleLbl = new Label("DART SHOP", titleStyle);
        titleLbl.setSize(Constants.WORLD_WIDTH, 50f);
        titleLbl.setPosition(0, 754f);
        titleLbl.setAlignment(Align.center);
        stage.addActor(titleLbl);

        // Trophy balance
        trophyLbl = new Label("Trophies: " + SaveData.getTrophies(), bodyStyle);
        trophyLbl.setSize(160f, 40f);
        trophyLbl.setPosition(Constants.WORLD_WIDTH - 180f, 784f);
        trophyLbl.setAlignment(Align.right);
        stage.addActor(trophyLbl);

        // Card positions: rows of 2
        float cardW = 160f;
        float cardH = 180f;
        float leftX  = 40f;
        float rightX = Constants.WORLD_WIDTH - 40f - cardW;
        float[] rowY = { 514f, 314f, 114f };

        for (int i = 0; i < 6; i++) {
            float x = (i % 2 == 0) ? leftX : rightX;
            float y = rowY[i / 2];
            addSkinCard(i, x, y, cardW, cardH, rectStyle, smallStyle);
        }

        // BACK button — libgdxY = 10
        TextButton backBtn = UiFactory.makeButton("BACK", rectStyle,
                Constants.BTN_SMALL_W, Constants.BTN_SMALL_H);
        backBtn.setPosition(20f, 10f);
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

    private void addSkinCard(final int skinIndex, float x, float y,
                              float cardW, float cardH,
                              TextButton.TextButtonStyle rectStyle,
                              Label.LabelStyle smallStyle) {
        boolean owned    = SaveData.isSkinOwned(skinIndex);
        boolean equipped = SaveData.getEquippedSkin() == skinIndex;
        int price        = Constants.SKIN_PRICES[skinIndex];

        String statusText;
        if (equipped)      statusText = "EQUIPPED";
        else if (owned)    statusText = "EQUIP";
        else if (price == 0) statusText = "EQUIP";
        else               statusText = price + " \uD83C\uDFC6";

        // Skin name label
        Label nameLbl = new Label(SKIN_NAMES[skinIndex], smallStyle);
        nameLbl.setSize(cardW, 20f);
        nameLbl.setPosition(x, y + cardH - 22f);
        nameLbl.setAlignment(Align.center);
        stage.addActor(nameLbl);

        // Action button (bottom of card)
        TextButton btn = UiFactory.makeButton(statusText, rectStyle, cardW, 40f);
        btn.setPosition(x, y);
        btn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                onSkinButtonPressed(skinIndex);
            }
        });
        stage.addActor(btn);
    }

    private void onSkinButtonPressed(int skinIndex) {
        boolean owned = SaveData.isSkinOwned(skinIndex);
        int price     = Constants.SKIN_PRICES[skinIndex];

        if (owned || price == 0) {
            // Equip
            SaveData.setEquippedSkin(skinIndex);
            game.playSound(Constants.SFX_BUTTON_CLICK);
            // Rebuild stage to refresh button labels
            stage.dispose();
            buildStage();
            return;
        }

        // Try to purchase
        int trophies = SaveData.getTrophies();
        if (trophies >= price) {
            SaveData.spendTrophies(price);
            SaveData.unlockSkin(skinIndex);
            SaveData.setEquippedSkin(skinIndex);
            game.playSound(Constants.SFX_POWER_UP);
            trophyLbl.setText("Trophies: " + SaveData.getTrophies());
            // Rebuild to refresh labels
            stage.dispose();
            buildStage();
        } else {
            // Not enough — flash label red briefly (simple feedback)
            trophyLbl.setColor(Color.RED);
            game.playSound(Constants.SFX_BUTTON_BACK);
            // Reset color after short time via a simple action
            trophyLbl.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence(
                com.badlogic.gdx.scenes.scene2d.actions.Actions.delay(0.5f),
                com.badlogic.gdx.scenes.scene2d.actions.Actions.color(Color.WHITE, 0.3f)
            ));
        }
    }

    @Override public void show() {
        game.playMusic(Constants.MUSIC_MENU);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Draw dart previews (ShapeRenderer)
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawDartPreviews();
        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
    }

    private void drawDartPreviews() {
        // Cards at: row 0 (y=514), row 1 (y=314), row 2 (y=114)
        float[] rowY = { 514f, 314f, 114f };
        float leftX  = 40f;
        float rightX = Constants.WORLD_WIDTH - 40f - 160f;
        float cardH  = 180f;

        for (int i = 0; i < 6; i++) {
            float x     = (i % 2 == 0) ? leftX : rightX;
            float cardY = rowY[i / 2];

            // Center of card area (above the button)
            float previewCX = x + 80f;  // center of 160px card
            float previewCY = cardY + 80f; // middle of 180-40=140px area above button

            drawMiniDart(i, previewCX, previewCY);
        }
    }

    private void drawMiniDart(int skinIndex, float cx, float cy) {
        Color barrel = BARREL_COLORS[skinIndex];

        float hw      = 5f;   // half width
        float bodyLen = 40f;
        float flightH = 14f;
        float flightW = 10f;

        // Barrel: pointing up
        shapeRenderer.setColor(barrel);
        shapeRenderer.triangle(cx + hw, cy, cx - hw, cy, cx - hw, cy - bodyLen);
        shapeRenderer.triangle(cx + hw, cy, cx - hw, cy - bodyLen, cx + hw, cy - bodyLen);

        // Flight
        shapeRenderer.setColor(0.7f, 0.7f, 0.75f, 1f);
        shapeRenderer.triangle(cx, cy - bodyLen,
                               cx + flightW, cy - bodyLen - flightH,
                               cx - flightW, cy - bodyLen - flightH);

        // Tip
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(cx, cy, 2.5f, 8);
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
