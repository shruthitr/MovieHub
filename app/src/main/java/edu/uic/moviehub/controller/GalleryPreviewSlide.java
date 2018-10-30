package edu.uic.moviehub.controller;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.uic.moviehub.activities.MainActivity;
import edu.uic.moviehub.R;

import edu.uic.moviehub.adapter.GalleryPreviewSlideAdapter;


public class GalleryPreviewSlide extends Fragment {
    private MainActivity activity;
    private ArrayList<String> galleryList;
    private View rootView;
    private int mUIFlag = View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.gallerypreview, container, false);
        activity = ((MainActivity) getActivity());
        if (this.getArguments() != null)
            galleryList = this.getArguments().getStringArrayList("galleryList");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        GalleryPreviewSlideAdapter galleryPreviewSlideAdapter = new GalleryPreviewSlideAdapter(getFragmentManager(), getResources(), galleryList);
        ViewPager mViewPager = (ViewPager) rootView.findViewById(R.id.galleryPager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(galleryPreviewSlideAdapter);
        if (this.getArguments() != null)
            mViewPager.setCurrentItem(this.getArguments().getInt("currPos"));


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= 19) {
            mUIFlag ^= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        activity.getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            activity.getWindow().getDecorView().setSystemUiVisibility(mUIFlag);
        else
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }


}
