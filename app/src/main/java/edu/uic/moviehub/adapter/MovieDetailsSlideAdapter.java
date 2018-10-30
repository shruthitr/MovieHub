

package edu.uic.moviehub.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.Parcelable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import edu.uic.moviehub.activities.MainActivity;
import edu.uic.moviehub.R;
import edu.uic.moviehub.activities.MainActivity;
import edu.uic.moviehub.controller.MovieDetailsInfo;
import edu.uic.moviehub.controller.MovieDetailsOverview;

/**
 * MovieDetailsSlide adapter used by the Movie Details Viewpager.
 *
 **/
public class MovieDetailsSlideAdapter extends FragmentStatePagerAdapter {
    private String[] navMenuTitles;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private MainActivity activity;

    public MovieDetailsSlideAdapter(FragmentManager fm, Resources res, MainActivity activity) {
        super(fm);
        navMenuTitles = res.getStringArray(R.array.detailTabs);
        this.activity = activity;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MovieDetailsInfo info = new MovieDetailsInfo();
                return info;
            case 1:
                MovieDetailsOverview overview = new MovieDetailsOverview();
                return overview;
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

            default:
                return navMenuTitles[1];
        }
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        try {
            if (activity.getRestoreMovieDetailsAdapterState()) {
                super.restoreState(state, loader);
            } else {
                activity.setRestoreMovieDetailsAdapterState(true);
            }
        } catch (java.lang.IllegalStateException e) {

        }
    }

}