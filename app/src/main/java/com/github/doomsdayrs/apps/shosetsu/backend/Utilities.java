package com.github.doomsdayrs.apps.shosetsu.backend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.webView.Actions;
import com.github.doomsdayrs.apps.shosetsu.ui.webView.WebViewApp;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 * shosetsu
 * 26 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class Utilities {

    public static final int SELECTED_STROKE_WIDTH = 8;
    public static String shoDir = "/Shosetsu/";

    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5);
    }

    // Preference objects
    public static SharedPreferences download;
    public static SharedPreferences view;
    public static SharedPreferences advanced;
    public static SharedPreferences tracking;
    public static SharedPreferences backup;

    /**
     * Initializes the settings
     */
    public static void initPreferences() {
        Settings.ReaderTextColor = view.getInt("ReaderTextColor", Color.BLACK);
        Settings.ReaderTextBackgroundColor = view.getInt("ReaderBackgroundColor", Color.WHITE);
        shoDir = download.getString("dir", "/storage/emulated/0/Shosetsu/");
        Settings.downloadPaused = download.getBoolean("paused", false);
        Settings.ReaderTextSize = view.getInt("ReaderTextSize", 14);
        Settings.themeMode = advanced.getInt("themeMode", 0);
        Settings.paragraphSpacing = view.getInt("paragraphSpacing", 1);
        Settings.indentSize = view.getInt("indentSize", 1);
    }

    public static boolean toggleTapToScroll() {
        if (isTapToScroll())
            view.edit().putBoolean("tapToScroll", false).apply();
        else view.edit().putBoolean("tapToScroll", true).apply();
        return isTapToScroll();
    }

    public static boolean isTapToScroll() {
        return view.getBoolean("tapToScroll", false);
    }

    public static boolean intToBoolean(int a) {
        return a == 1;
    }

    public static int booleanToInt(boolean a) {
        if (a)
            return 1;
        else return 0;
    }

    public static void changeIndentSize(int newIndent) {
        Settings.indentSize = newIndent;
        view.edit().putInt("indentSize", newIndent).apply();
    }

    public static void changeParagraphSpacing(int newSpacing) {
        Settings.paragraphSpacing = newSpacing;
        view.edit().putInt("paragraphSpacing", newSpacing).apply();
    }


    public static void changeMode(Activity activity, int newMode) {
        if (!(newMode >= 0 && newMode <= 2))
            throw new IndexOutOfBoundsException("Non valid int passed");
        Settings.themeMode = newMode;
        advanced.edit()
                .putInt("themeMode", newMode)
                .apply();

        switch (Settings.themeMode) {
            case 0:
                activity.setTheme(R.style.Theme_MaterialComponents_Light_NoActionBar);
                break;
            case 1:
                activity.setTheme(R.style.Theme_MaterialComponents_NoActionBar);
                break;
            case 2:
                activity.setTheme(R.style.ThemeOverlay_MaterialComponents_Dark);
        }
    }


    /**
     * Toggles paused downloads
     *
     * @return if paused or not
     */
    public static boolean togglePause() {
        Settings.downloadPaused = !Settings.downloadPaused;
        download.edit()
                .putBoolean("paused", Settings.downloadPaused)
                .apply();
        return Settings.downloadPaused;
    }

    /**
     * Checks if online
     *
     * @return true if so, otherwise false
     */
    public static boolean isOnline() {
        NetworkInfo activeNetwork = Settings.connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null)
            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        return false;
    }

    /**
     * Is reader in night mode
     *
     * @return true if so, otherwise false
     */
    public static boolean isReaderNightMode() {
        //TODO: Check this also, this doesn't seem to be a nice way to do things.
        return Settings.ReaderTextColor == Color.WHITE;
    }

    public static void setNightNode() {
        setReaderColor(Color.WHITE, Color.BLACK);
    }

    public static void unsetNightMode() {
        setReaderColor(Color.BLACK, Color.WHITE);
    }

    /**
     * Sets the reader color
     *
     * @param text       Color of text
     * @param background Color of background
     */
    private static void setReaderColor(int text, int background) {
        Settings.ReaderTextColor = text;
        Settings.ReaderTextBackgroundColor = background;
        view.edit()
                .putInt("ReaderTextColor", text)
                .putInt("ReaderBackgroundColor", background)
                .apply();
    }

    /**
     * Swaps the reader colors
     */
    public static void swapReaderColor() {
        if (isReaderNightMode()) {
            setReaderColor(Color.BLACK, Color.WHITE);
        } else {
            setReaderColor(Color.WHITE, Color.BLACK);
        }
    }


    /**
     * Gets y position of a bookmark
     *
     * @param chapterURL chapter chapterURL
     * @return y position
     */
    public static int getYBookmark(String chapterURL) {
        return Database.DatabaseChapter.getY(chapterURL);
    }

    /**
     * Toggles bookmark
     *
     * @param chapterURL imageURL of chapter
     * @return true means added, false means removed
     */
    public static boolean toggleBookmarkChapter(String chapterURL) {
        if (Database.DatabaseChapter.isBookMarked(chapterURL)) {
            Database.DatabaseChapter.setBookMark(chapterURL, 0);
            return false;
        } else {
            Database.DatabaseChapter.setBookMark(chapterURL, 1);
            return true;
        }
    }


    public static void setTextSize(int size) {
        Settings.ReaderTextSize = size;
        view.edit()
                .putInt("ReaderTextSize", size)
                .apply();
    }

    public static void openChapter(Activity activity, NovelChapter novelChapter, String nurl, int formatterID) {
        Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.READING);
        Intent intent = new Intent(activity, ChapterReader.class);
        intent.putExtra("title", novelChapter.chapterNum);
        intent.putExtra("chapterURL", novelChapter.link);
        intent.putExtra("novelURL", nurl);
        intent.putExtra("formatter", formatterID);
        activity.startActivity(intent);
    }

    public static void openInWebview(@NotNull Activity activity, @NotNull String url) {
        Intent intent = new Intent(activity, WebViewApp.class);
        intent.putExtra("url", url);
        intent.putExtra("action", Actions.VIEW.getAction());
        activity.startActivity(intent);
    }

    public static void openInBrowser(@NotNull Activity activity, @NotNull String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }


    /**
     * Freezes the thread for x time
     *
     * @param time time in MS
     */
    public static void wait(int time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            if (e.getMessage() != null)
                Log.e("Error", e.getMessage());
        }
    }

    //TODO Online Trackers
    //Methods below when tracking system setup

    @SuppressWarnings({"EmptyMethod", "unused"})
    public static boolean isTrackingEnabled() {
        return tracking.getBoolean("enabled", false);
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    public static void addTracker() {
    }

}