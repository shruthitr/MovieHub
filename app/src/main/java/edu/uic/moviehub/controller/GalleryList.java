package edu.uic.moviehub.controller;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;


import java.util.ArrayList;

import edu.uic.moviehub.activities.MainActivity;

import edu.uic.moviehub.R;

public class GalleryList extends Fragment implements AdapterView.OnItemClickListener {
    private MainActivity activity;
    private AbsListView listView;
    private String title;
    private GalleryPreviewSlide galleryPreview;
    private ArrayList<String> galleryPath;
    private Bundle save;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            save = savedInstanceState.getBundle("save");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        View rootView = inflater.inflate(R.layout.gallerylist, container, false);
        activity = ((MainActivity) getActivity());
        galleryPreview = new GalleryPreviewSlide();
        activity.getMDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        listView = (AbsListView) rootView.findViewById(R.id.gridView);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.isVisible()) {
            // Check orientation and lock to portrait if we are on phone
            if (getResources().getBoolean(R.bool.portrait_only)) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        if (save != null)
            setTitle(save.getString("title"));
        activity.setTitle(getTitle());
        activity.getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(activity, R.color.background_material_light));

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (activity.getSupportActionBar() != null)
                    activity.getSupportActionBar().show();
            }
        });

        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (Build.VERSION.SDK_INT >= 19)
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);


        listView.setOnItemClickListener(this);

        //setGallery();
        System.gc();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle args = new Bundle();
        args.putStringArrayList("galleryList", galleryPath);
        args.putInt("currPos", position);
        galleryPreview.setArguments(args);
        transaction.replace(R.id.frame_container, galleryPreview);
        // add the current transaction to the back stack:
        transaction.addToBackStack("galleryList");
        transaction.commit();

    }


    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the title.
     */
    private String getTitle() {
        return this.title;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (save != null)
            outState.putBundle("save", save);
        else {
            Bundle send = new Bundle();
            send.putString("title", getTitle());

            outState.putBundle("save", send);
        }
    }

    /**
     * Set empty adapter to free memory when this fragment is inactive
     */
    public void onDestroyView() {
        super.onDestroyView();
        listView.setAdapter(null);
    }

}
