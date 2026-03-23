package com.asocity.dartking000887.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/** One entry in the per-venue top-10 leaderboard. */
public class ScoreEntry {
    public int score;
    public String date;   // "yyyy-MM-dd"

    public ScoreEntry() {}

    public ScoreEntry(int score, String date) {
        this.score = score;
        this.date  = date;
    }

    // -----------------------------------------------------------------------
    // JSON helpers
    // -----------------------------------------------------------------------

    /** Deserialize JSON string → sorted Array (descending by score). */
    public static Array<ScoreEntry> fromJson(String json) {
        if (json == null || json.isEmpty() || json.equals("[]")) {
            return new Array<>();
        }
        try {
            Json j = new Json();
            Array<ScoreEntry> arr = j.fromJson(Array.class, ScoreEntry.class, json);
            if (arr == null) return new Array<>();
            return arr;
        } catch (Exception e) {
            return new Array<>();
        }
    }

    /** Serialize Array → JSON string. */
    public static String toJson(Array<ScoreEntry> entries) {
        Json j = new Json();
        return j.toJson(entries, Array.class, ScoreEntry.class);
    }

    /**
     * Add a new entry, sort descending, truncate to maxEntries, return JSON string.
     */
    public static String addEntry(String existingJson, int score, String date, int maxEntries) {
        Array<ScoreEntry> list = fromJson(existingJson);
        list.add(new ScoreEntry(score, date));
        list.sort((a, b) -> b.score - a.score);
        if (list.size > maxEntries) {
            list.removeRange(maxEntries, list.size - 1);
        }
        return toJson(list);
    }
}
