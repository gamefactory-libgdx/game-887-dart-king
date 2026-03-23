package com.asocity.dartking000887;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UiFactory {

    /** Rectangle (wide) button style — for main action buttons. */
    public static TextButton.TextButtonStyle makeRectStyle(AssetManager mgr, BitmapFont font) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font  = font;
        s.up    = new TextureRegionDrawable(new TextureRegion(
                mgr.get(Constants.BTN_RECT_UP,   Texture.class)));
        s.down  = new TextureRegionDrawable(new TextureRegion(
                mgr.get(Constants.BTN_RECT_DOWN, Texture.class)));
        return s;
    }

    /** Round button style — for icon / small buttons. */
    public static TextButton.TextButtonStyle makeRoundStyle(AssetManager mgr, BitmapFont font) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font  = font;
        s.up    = new TextureRegionDrawable(new TextureRegion(
                mgr.get(Constants.BTN_ROUND_UP,   Texture.class)));
        s.down  = new TextureRegionDrawable(new TextureRegion(
                mgr.get(Constants.BTN_ROUND_DOWN, Texture.class)));
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
