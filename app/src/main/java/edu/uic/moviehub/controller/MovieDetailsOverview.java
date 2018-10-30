

package edu.uic.moviehub.controller;

import android.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.uic.moviehub.activities.MainActivity;
import edu.uic.moviehub.MovieHub;
import edu.uic.moviehub.R;
import edu.uic.moviehub.activities.MainActivity;
import edu.uic.moviehub.helper.ObservableScrollView;


public class MovieDetailsOverview extends Fragment {
    private MainActivity activity;
    private TextView overview;
    private int trailerButtonCheck;
    private Button movieTrailerButton;
    private ObservableScrollView scrollView;
    private MovieDetails movieDetails;
    private ArrayList<String> trailerList;

    public MovieDetailsOverview() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.moviedetailsoverview, container, false);
        activity = ((MainActivity) getActivity());
        overview = (TextView) rootView.findViewById(R.id.overviewContent);
        movieTrailerButton = (Button) rootView.findViewById(R.id.movieTrailerButton);
        movieTrailerButton.setOnClickListener(trailerButtonListener);
        scrollView = (ObservableScrollView) rootView.findViewById(R.id.moviedetailsoverview);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                View toolbarView = activity.findViewById(R.id.toolbar);
                if (toolbarView != null) {
                    int toolbarHeight = toolbarView.getHeight();
                    DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
                    int height = displayMetrics.heightPixels;
                    overview.setMinHeight(height + toolbarHeight);
                }
            }
        });

        return rootView;
    }

    private View.OnClickListener trailerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(trailerButtonCheck==0)
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MovieHub.youtube + trailerList.get(0))));
            }
            else if(trailerButtonCheck==1) {
               Toast toast=Toast.makeText(getActivity(), R.string.toast_text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();

            }

        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (activity.getMovieDetailsOverviewBundle() != null)
            overview.setText(activity.getMovieDetailsOverviewBundle().getString("overview"));

        if (scrollView != null) {

            scrollView.setTouchInterceptionViewGroup((ViewGroup) activity.getMovieDetailsFragment().getView().findViewById(R.id.containerLayout));
            scrollView.setScrollViewCallbacks(activity.getMovieDetailsFragment());
        }

    }

    public TextView getOverview() {
        return overview;
    }

    public void setTrailerButtonCheck(int trailerButtonCheck) {
       this.trailerButtonCheck = trailerButtonCheck;
    }

    public void setTrailerList(ArrayList<String>trailerList) {
        this.trailerList = trailerList;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    public void onDestroyView() {
        super.onDestroyView();
        activity.setMovieDetailsOverviewBundle(null);
    }

    public ObservableScrollView getScrollView() {
        return scrollView;
    }
}
