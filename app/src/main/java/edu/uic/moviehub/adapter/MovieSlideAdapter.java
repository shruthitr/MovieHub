
package edu.uic.moviehub.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import edu.uic.moviehub.R;
import edu.uic.moviehub.controller.MovieList;


public class MovieSlideAdapter extends FragmentPagerAdapter {
    private String[] navMenuTitles;
    private FragmentManager manager;
    private FragmentTransaction mCurTransaction = null;
    private Resources res;

    public MovieSlideAdapter(FragmentManager fm, Resources res) {
        super(fm);
        this.manager = fm;
        navMenuTitles = res.getStringArray(R.array.moviesTabs);
        this.res = res;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 4;
    }


    @Override
    public Fragment getItem(int position) {
        String upcoming = "movie/upcoming";
        String nowPlaying = "movie/now_playing";
        String popular = "movie/popular";
        String topRated = "movie/top_rated";
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                args.putString("currentList", "upcoming");
                MovieList upcomingList = new MovieList();
                upcomingList.setTitle(res.getString(R.string.moviesTitle));
                upcomingList.setArguments(args);
                upcomingList.setCurrentList(upcoming);
                return upcomingList;
            case 1:
                args.putString("currentList", "nowPlaying");
                MovieList nowPlayingList = new MovieList();
                nowPlayingList.setTitle(res.getString(R.string.moviesTitle));
                nowPlayingList.setArguments(args);
                nowPlayingList.setCurrentList(nowPlaying);
                return nowPlayingList;
            case 2:
                args.putString("currentList", "popular");
                MovieList popularList = new MovieList();
                popularList.setTitle(res.getString(R.string.moviesTitle));
                popularList.setArguments(args);
                popularList.setCurrentList(popular);
                return popularList;
            case 3:
                args.putString("currentList", "topRated");
                MovieList topRatedList = new MovieList();
                topRatedList.setTitle(res.getString(R.string.moviesTitle));
                topRatedList.setArguments(args);
                topRatedList.setCurrentList(topRated);
                return topRatedList;
            default:
                return null;
        }

    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return navMenuTitles[0];
            case 1:
                return navMenuTitles[1];
            case 2:
                return navMenuTitles[2];
            case 3:
                return navMenuTitles[3];
            default:
                return navMenuTitles[1];
        }
    }


    public void reAttachFragments(ViewGroup container) {
        if (mCurTransaction == null) {
            mCurTransaction = manager.beginTransaction();
        }

        for (int i = 0; i < getCount(); i++) {

            final long itemId = getItemId(i);

                        String name = "android:switcher:" + container.getId() + ":" + itemId;
            Fragment fragment = manager.findFragmentByTag(name);

            if (fragment != null) {
                mCurTransaction.detach(fragment);
            }
        }

        try {
            mCurTransaction.commit();
        } catch (java.lang.IllegalStateException e) {
        }
        mCurTransaction = null;
    }


}