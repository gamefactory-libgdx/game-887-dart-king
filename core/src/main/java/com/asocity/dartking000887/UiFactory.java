package com.asocity.dartking000887;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UiFactory {

    // Dark amber / orange-brown to match the game palette
    private static final Color BTN_UP   = new Color(0.80f, 0.36f, 0.05f, 1f);
    private static final Color BTN_DOWN = new Color(0.58f, 0.24f, 0.03f, 1f);

    public static TextButton.TextButtonStyle makeRectStyle(AssetManager mgr, BitmapFont font) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font  = font;
        // tint() returns a NEW tinted drawable — must assign the return value
        s.up   = new TextureRegionDrawable(new TextureRegion(mgr.get(Constants.BTN_RECT_UP,   Texture.class))).tint(BTN_UP);
        s.down = new TextureRegionDrawable(new TextureRegion(mgr.get(Constants.BTN_RECT_DOWN, Texture.class))).tint(BTN_DOWN);
        s.fontColor = Color.WHITE;
        return s;
    }

    public static TextButton.TextButtonStyle makeRoundStyle(AssetManager mgr, BitmapFont font) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font  = font;
        s.up   = new TextureRegionDrawable(new TextureRegion(mgr.get(Constants.BTN_ROUND_UP,   Texture.class))).tint(BTN_UP);
        s.down = new TextureRegionDrawable(new TextureRegion(mgr.get(Constants.BTN_ROUND_DOWN, Texture.class))).tint(BTN_DOWN);
        s.fontColor = Color.WHITE;
        return s;
    }

    public static TextButton makeButton(String label, TextButton.TextButtonStyle style,
                                        float w, float h) {
        TextButton btn = new TextButton(label, style);
        btn.setSize(w, h);
        return btn;
    }

    private UiFactory() {}
}
