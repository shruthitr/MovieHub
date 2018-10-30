
package edu.uic.moviehub.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import edu.uic.moviehub.MovieHub;
import edu.uic.moviehub.R;
import edu.uic.moviehub.controller.GalleryPreviewDetail;




public class GalleryPreviewSlideAdapter extends FragmentStatePagerAdapter {
    private final int mSize;
    private Resources res;
    private ArrayList<String> galleryList;

    public GalleryPreviewSlideAdapter(FragmentManager fm, Resources res, ArrayList<String> galleryList) {
        super(fm);
        this.res = res;
        this.galleryList = galleryList;
        mSize = galleryList.size();


    }


    @Override
    public int getCount() {
        return mSize;
    }


    @Override
    public Fragment getItem(int position) {
        return GalleryPreviewDetail.newInstance(MovieHub.imageUrl + res.getString(R.string.galleryPreviewImgSize) + galleryList.get(position));
    }
}
