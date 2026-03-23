package com.asocity.dartking000887;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Static helpers for all SharedPreferences reads/writes.
 * Preferences file: Constants.PREFS_NAME ("dart_king").
 */
public class SaveData {

    private static Preferences prefs() {
        return Gdx.app.getPreferences(Constants.PREFS_NAME);
    }

    // ---------- Trophies ----------

    public static int getTrophies() {
        return prefs().getInteger(Constants.PREF_TROPHIES_BALANCE, 0);
    }

    public static void addTrophies(int amount) {
        Preferences p = prefs();
        p.putInteger(Constants.PREF_TROPHIES_BALANCE, getTrophies() + amount);
        p.flush();
    }

    public static void spendTrophies(int amount) {
        Preferences p = prefs();
        int cur = getTrophies();
        p.putInteger(Constants.PREF_TROPHIES_BALANCE, Math.max(0, cur - amount));
        p.flush();
    }

    // ---------- Skins ----------

    /** Bitmask: bit N = skin N is owned. Bit 0 is always 1 (free classic dart). */
    public static int getOwnedSkins() {
        return prefs().getInteger(Constants.PREF_OWNED_SKINS, 1);
    }

    public static boolean isSkinOwned(int index) {
        return (getOwnedSkins() & (1 << index)) != 0;
    }

    public static void unlockSkin(int index) {
        Preferences p = prefs();
        int owned = getOwnedSkins() | (1 << index);
        p.putInteger(Constants.PREF_OWNED_SKINS, owned);
        p.flush();
    }

    public static int getEquippedSkin() {
        return prefs().getInteger(Constants.PREF_SKIN, 0);
    }

    public static void setEquippedSkin(int index) {
        Preferences p = prefs();
        p.putInteger(Constants.PREF_SKIN, index);
        p.flush();
    }

    // ---------- Audio ----------

    public static boolean isMusicOn() {
        return prefs().getBoolean(Constants.PREF_MUSIC_ON, true);
    }

    public static void setMusicOn(boolean on) {
        Preferences p = prefs();
        p.putBoolean(Constants.PREF_MUSIC_ON, on);
        p.flush();
    }

    public static boolean isSfxOn() {
        return prefs().getBoolean(Constants.PREF_SFX_ON, true);
    }

    public static void setSfxOn(boolean on) {
        Preferences p = prefs();
        p.putBoolean(Constants.PREF_SFX_ON, on);
        p.flush();
    }

    // ---------- High Scores ----------

    public static int getHiScore(String prefKey) {
        return prefs().getInteger(prefKey, 0);
    }

    public static void saveHiScore(String prefKey, int score) {
        int cur = getHiScore(prefKey);
        if (score > cur) {
            Preferences p = prefs();
            p.putInteger(prefKey, score);
            p.flush();
        }
    }

    // ---------- Score Lists ----------

    public static String getScoreListJson(String prefKey) {
        return prefs().getString(prefKey, "[]");
    }

    public static void saveScoreListJson(String prefKey, String json) {
        Preferences p = prefs();
        p.putString(prefKey, json);
        p.flush();
    }

    // ---------- Reset ----------

    public static void resetAll() {
        Preferences p = prefs();
        p.clear();
        p.flush();
    }

    private SaveData() {}
}
