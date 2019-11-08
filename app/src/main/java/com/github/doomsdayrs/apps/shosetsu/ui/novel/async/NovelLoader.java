package com.github.doomsdayrs.apps.shosetsu.ui.novel.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentInfo;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;

import java.io.IOException;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelIDFromNovelURL;

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
 */

/**
 * Shosetsu
 * 17 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 * <p>
 * This task loads a novel for the novel fragment
 * </p>
 */
public class NovelLoader extends AsyncTask<Activity, Void, Boolean> {
    private String novelURL;
    private Formatter formatter;
    private NovelPage novelPage;
    private int novelID;

    // References
    private final NovelFragment novelFragment;
    private final NovelFragmentInfo novelFragmentInfo;

    private boolean loadAll;

    /**
     * Constructor
     *
     * @param novelFragment reference to the fragment
     */
    public NovelLoader(NovelFragment novelFragment, boolean loadAll) {
        this.novelFragment = novelFragment;
        this.loadAll = loadAll;
        this.novelFragmentInfo = null;
    }

    public NovelLoader(NovelFragmentInfo novelFragmentInfo, boolean loadAll) {
        this.novelFragment = null;
        this.loadAll = loadAll;
        this.novelFragmentInfo = novelFragmentInfo;
    }

    private NovelLoader(NovelLoader novelLoader) {
        this.novelURL = novelLoader.novelURL;
        this.formatter = novelLoader.formatter;
        this.novelPage = novelLoader.novelPage;
        this.novelID = novelLoader.novelID;
        this.novelFragment = novelLoader.novelFragment;
        this.loadAll = novelLoader.loadAll;
        this.novelFragmentInfo = novelLoader.novelFragmentInfo;
    }

    /**
     * Background process
     *
     * @param voids activity to work with
     * @return if completed
     */
    @Override

    protected Boolean doInBackground(Activity... voids) {
        Activity activity = voids[0];
        Log.d("Loading", novelURL);
        if (loadAll) {
            if (novelFragment != null && novelFragment.getActivity() != null)
                novelFragment.getActivity().runOnUiThread(() -> novelFragment.errorView.setVisibility(View.GONE));

        } else if (novelFragmentInfo != null && novelFragmentInfo.getActivity() != null)
            novelFragmentInfo.getActivity().runOnUiThread(() -> novelFragmentInfo.novelFragment.errorView.setVisibility(View.GONE));


        try {
            novelPage = formatter.parseNovel(novelURL);
            if (!activity.isDestroyed() && !Database.DatabaseNovels.inDatabase(novelID)) {
                Database.DatabaseNovels.addToLibrary(formatter.getID(), novelPage, novelURL, com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD.getA());
            }
            //TODO The getNovelID in this method likely will cause slowdowns due to IO
            int novelID = getNovelIDFromNovelURL(novelURL);
            for (NovelChapter novelChapter : novelPage.novelChapters)
                if (!activity.isDestroyed() && !Database.DatabaseChapter.inChapters(novelChapter.link))
                    Database.DatabaseChapter.addToChapters(novelID, novelChapter);


            Log.d("Loaded Novel:", novelPage.title);
            return true;
        } catch (IOException e) {
            if (loadAll) {
                if (novelFragment != null && novelFragment.getActivity() != null)
                    novelFragment.getActivity().runOnUiThread(() -> {
                        novelFragment.errorView.setVisibility(View.VISIBLE);
                        novelFragment.errorMessage.setText(e.getMessage());
                        novelFragment.errorButton.setOnClickListener(view -> refresh(activity));
                    });
            } else if (novelFragmentInfo != null && novelFragmentInfo.getActivity() != null)
                novelFragmentInfo.getActivity().runOnUiThread(() -> {
                    novelFragmentInfo.novelFragment.errorView.setVisibility(View.VISIBLE);
                    novelFragmentInfo.novelFragment.errorMessage.setText(e.getMessage());
                    novelFragmentInfo.novelFragment.errorButton.setOnClickListener(view -> refresh(activity));
                });


        }
        return false;
    }

    private void refresh(Activity activity) {
        new NovelLoader(this).execute(activity);
    }

    /**
     * Show progress bar
     */
    @Override
    protected void onPreExecute() {
        if (loadAll) {
            assert novelFragment != null;
            novelFragment.progressBar.setVisibility(View.VISIBLE);
        } else {
            assert novelFragmentInfo != null;
            novelFragmentInfo.swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    protected void onCancelled() {
        onPostExecute(false);
    }

    /**
     * Hides progress and sets data
     *
     * @param aBoolean if completed
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Activity activity = null;
        if (novelFragmentInfo != null)
            activity = novelFragmentInfo.getActivity();
        else if (novelFragment != null) {
            activity = novelFragment.getActivity();
        }

        if (activity != null) {
            if (loadAll) {
                if (novelFragment != null) {
                    novelFragment.progressBar.setVisibility(View.GONE);
                }
            } else {
                if (novelFragmentInfo != null) {
                    novelFragmentInfo.swipeRefreshLayout.setRefreshing(false);
                }
                if (novelFragment != null && Database.DatabaseNovels.inDatabase(novelFragment.novelID)) {
                    try {
                        Database.DatabaseNovels.updateData(novelURL, novelPage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (aBoolean) {
                Statics.mainActionBar.setTitle(novelPage.title);
                activity.runOnUiThread(() -> {
                    if (loadAll)
                        if (novelFragment != null) {
                            novelFragment.novelFragmentInfo.setData();
                        } else {
                            novelFragmentInfo.setData();
                        }
                });
                if (loadAll) {
                    activity.runOnUiThread(() -> new ChapterLoader(novelPage, novelURL, formatter).execute());
                }
            }
        }

    }
}