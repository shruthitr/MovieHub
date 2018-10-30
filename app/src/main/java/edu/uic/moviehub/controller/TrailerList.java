

package edu.uic.moviehub.controller;


import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;



import java.util.ArrayList;

import edu.uic.moviehub.activities.MainActivity;
import edu.uic.moviehub.MovieHub;
import edu.uic.moviehub.R;
import edu.uic.moviehub.adapter.TrailerAdapter;
import edu.uic.moviehub.model.TrailerModel;

/**
 * This fragment is used in the trailer view.
 */
public class TrailerList extends Fragment implements AdapterView.OnItemClickListener {
    private MainActivity activity;
    private AbsListView listView;
    private String title;
    private ArrayList<String> trailerPath;
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


        View rootView;
        rootView = inflater.inflate(R.layout.gallerylist, container, false);
        listView = (AbsListView) rootView.findViewById(R.id.gridView);
        activity = ((MainActivity) getActivity());

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
        listView.setOnItemClickListener(this);

        setTrailer();
        System.gc();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MovieHub.youtube + trailerPath.get(position))));
    }


    private void setTrailer() {
        trailerPath = this.getArguments().getStringArrayList("trailerList");

        ArrayList<TrailerModel> trailerList = new ArrayList<>();
        TrailerAdapter trailerAdapter = new TrailerAdapter(getActivity(), R.layout.trailerview_row, trailerList);
        listView.setAdapter(trailerAdapter);

        for (int i = 0; i < trailerPath.size(); i++) {
            TrailerModel trailer = new TrailerModel();
            trailer.setFilePath(trailerPath.get(i));
            trailerList.add(trailer);
        }

        trailerAdapter.notifyDataSetChanged();

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


    public void onDestroyView() {
        super.onDestroyView();
        listView.setAdapter(null);
    }

}
