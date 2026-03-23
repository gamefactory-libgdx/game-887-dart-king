package com.asocity.dartking000887;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UiFactory {

    /** Rectangle (wide) button style — for main action buttons. */
    public static TextButton.TextButtonStyle makeRectStyle(AssetManager mgr, BitmapFont font) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font  = font;
        TextureRegionDrawable rectUp   = new TextureRegionDrawable(new TextureRegion(mgr.get(Constants.BTN_RECT_UP,   Texture.class)));
        TextureRegionDrawable rectDown = new TextureRegionDrawable(new TextureRegion(mgr.get(Constants.BTN_RECT_DOWN, Texture.class)));
        Color btnTint = new Color(0.82f, 0.38f, 0.06f, 1f);
        rectUp.tint(btnTint);
        rectDown.tint(new Color(0.65f, 0.28f, 0.04f, 1f));
        s.up   = rectUp;
        s.down = rectDown;
        return s;
    }

    /** Round button style — for icon / small buttons. */
    public static TextButton.TextButtonStyle makeRoundStyle(AssetManager mgr, BitmapFont font) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font  = font;
        TextureRegionDrawable rndUp   = new TextureRegionDrawable(new TextureRegion(mgr.get(Constants.BTN_ROUND_UP,   Texture.class)));
        TextureRegionDrawable rndDown = new TextureRegionDrawable(new TextureRegion(mgr.get(Constants.BTN_ROUND_DOWN, Texture.class)));
        rndUp.tint(new Color(0.82f, 0.38f, 0.06f, 1f));
        rndDown.tint(new Color(0.65f, 0.28f, 0.04f, 1f));
        s.up   = rndUp;
        s.down = rndDown;
        return s;
    }

    /**
     * Convenience: create a sized TextButton in one call.
     *
     * @param label  button text
     * @param style  style from makeRectStyle() or makeRoundStyle()
     * @param w      button width  (world units)
     * @param h      button height (world units)
     */
    public static TextButton makeButton(String label, TextButton.TextButtonStyle style,
                                        float w, float h) {
        TextButton btn = new TextButton(label, style);
        btn.setSize(w, h);
        return btn;
    }

    // Prevent instantiation
    private UiFactory() {}
}
