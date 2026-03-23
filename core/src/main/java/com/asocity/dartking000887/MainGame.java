package com.asocity.dartking000887;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.asocity.dartking000887.screens.MainMenuScreen;

public class MainGame extends Game {

    public SpriteBatch  batch;
    public AssetManager manager;

    // Shared fonts — generated once, used by all screens
    public BitmapFont fontTitle;   // Born2bSporty — large titles / score display
    public BitmapFont fontHeading; // Born2bSporty — headings
    public BitmapFont fontBody;    // GasaltRegular — buttons / body text
    public BitmapFont fontSmall;   // GasaltRegular — small labels

    // Audio state
    public boolean musicEnabled = true;
    public boolean sfxEnabled   = true;
    public Music   currentMusic = null;

    @Override
    public void create() {
        batch   = new SpriteBatch();
        manager = new AssetManager();

        // Register FreeType loaders
        manager.setLoader(FreeTypeFontGenerator.class,
                new FreeTypeFontGeneratorLoader(new InternalFileHandleResolver()));
        manager.setLoader(BitmapFont.class, ".ttf",
                new FreetypeFontLoader(new InternalFileHandleResolver()));
        manager.setLoader(BitmapFont.class, ".otf",
                new FreetypeFontLoader(new InternalFileHandleResolver()));

        generateFonts();
        loadCoreAssets();
        manager.finishLoading();

        // Restore audio prefs
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        musicEnabled = prefs.getBoolean(Constants.PREF_MUSIC_ON, true);
        sfxEnabled   = prefs.getBoolean(Constants.PREF_SFX_ON,   true);

        setScreen(new MainMenuScreen(this));
    }

    // -----------------------------------------------------------------------
    // Font generation
    // -----------------------------------------------------------------------

    private void generateFonts() {
        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Born2bSporty.otf"));
        FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/GasaltRegular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter p =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.borderColor = new Color(0f, 0f, 0f, 0.85f);

        // Title font — large
        p.size        = Constants.FONT_SIZE_TITLE;
        p.borderWidth = Constants.BORDER_TITLE;
        fontTitle = titleGen.generateFont(p);

        // Heading font
        p.size        = Constants.FONT_SIZE_HEADING;
        p.borderWidth = Constants.BORDER_BODY;
        fontHeading = titleGen.generateFont(p);

        // Score font — extra large title variant
        p.size        = Constants.FONT_SIZE_SCORE;
        p.borderWidth = Constants.BORDER_TITLE;
        // reuse fontTitle slot for score display via a separate public field
        // (screens that need score size can generate from fontTitle scale,
        //  but we expose a dedicated scoreFont for convenience)

        // Body font
        p.size        = Constants.FONT_SIZE_BODY;
        p.borderWidth = Constants.BORDER_BODY;
        fontBody = bodyGen.generateFont(p);

        // Small font
        p.size        = Constants.FONT_SIZE_SMALL;
        p.borderWidth = Constants.BORDER_SMALL;
        fontSmall = bodyGen.generateFont(p);

        titleGen.dispose();
        bodyGen.dispose();
    }

    // -----------------------------------------------------------------------
    // Core asset loading (UI buttons loaded here so UiFactory is ready)
    // -----------------------------------------------------------------------

    private void loadCoreAssets() {
        manager.load(Constants.BTN_RECT_UP,   Texture.class);
        manager.load(Constants.BTN_RECT_DOWN, Texture.class);
        manager.load(Constants.BTN_ROUND_UP,  Texture.class);
        manager.load(Constants.BTN_ROUND_DOWN, Texture.class);

        // Music
        manager.load(Constants.MUSIC_MENU,      Music.class);
        manager.load(Constants.MUSIC_GAMEPLAY,  Music.class);
        manager.load(Constants.MUSIC_GAME_OVER, Music.class);

        // SFX
        manager.load(Constants.SFX_BUTTON_CLICK, Sound.class);
        manager.load(Constants.SFX_BUTTON_BACK,  Sound.class);
        manager.load(Constants.SFX_TOGGLE,       Sound.class);
        manager.load(Constants.SFX_GAME_OVER,    Sound.class);
        manager.load(Constants.SFX_POWER_UP,     Sound.class);
    }

    // -----------------------------------------------------------------------
    // Music helpers
    // -----------------------------------------------------------------------

    /** Starts looping music. Skips restart if already playing the same track. */
    public void playMusic(String path) {
        Music requested = manager.get(path, Music.class);
        if (requested == currentMusic && currentMusic.isPlaying()) return;
        if (currentMusic != null) currentMusic.stop();
        currentMusic = requested;
        currentMusic.setLooping(true);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    /** Plays music once (no loop). Use for game-over jingle. */
    public void playMusicOnce(String path) {
        if (currentMusic != null) currentMusic.stop();
        currentMusic = manager.get(path, Music.class);
        currentMusic.setLooping(false);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    /** Play a sound effect if SFX is enabled. */
    public void playSound(String path) {
        if (sfxEnabled) manager.get(path, Sound.class).play(1.0f);
    }

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        manager.dispose();
        if (fontTitle   != null) fontTitle.dispose();
        if (fontHeading != null) fontHeading.dispose();
        if (fontBody    != null) fontBody.dispose();
        if (fontSmall   != null) fontSmall.dispose();
    }
}
