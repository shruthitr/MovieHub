
package edu.uic.moviehub.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.wearable.view.CircledImageView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import edu.uic.moviehub.activities.MainActivity;
import edu.uic.moviehub.R;
import edu.uic.moviehub.view.ObservableParallaxScrollView;


public class MovieDetailsInfo extends Fragment implements AdapterView.OnItemClickListener {
    private MainActivity activity;
    private View rootView;
    private ImageView backDropPath;
    private int backDropCheck;
    private TextView titleText;
    private TextView releaseDate;
    private ImageView posterPath;
    private TextView tagline;
    private TextView statusText;
    private TextView runtime;
    private TextView genres;
    private TextView countries;
    private TextView companies;
    private RatingBar ratingBar;
    private TextView voteCount;
    private CircledImageView moreIcon;
    private CircledImageView homeIcon;
    private CircledImageView galleryIcon;
    private CircledImageView trailerIcon;
    private ObservableParallaxScrollView scrollView;
    private MovieDetails movieDetails = new MovieDetails();

    public MovieDetailsInfo() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        rootView = inflater.inflate(R.layout.moviedetailsinfo, container, false);
        activity = ((MainActivity) getActivity());
        backDropPath = (ImageView) rootView.findViewById(R.id.backDropPath);


        titleText = (TextView) rootView.findViewById(R.id.title);
        releaseDate = (TextView) rootView.findViewById(R.id.releaseDate);
        posterPath = (ImageView) rootView.findViewById(R.id.posterPath);
        tagline = (TextView) rootView.findViewById(R.id.tagline);
        statusText = (TextView) rootView.findViewById(R.id.status);
        runtime = (TextView) rootView.findViewById(R.id.runtime);
        genres = (TextView) rootView.findViewById(R.id.genres);
        countries = (TextView) rootView.findViewById(R.id.countries);
        companies = (TextView) rootView.findViewById(R.id.companies);
        ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
        voteCount = (TextView) rootView.findViewById(R.id.voteCount);

        homeIcon = (CircledImageView) rootView.findViewById(R.id.homeIcon);
        homeIcon.setVisibility(View.GONE);
        homeIcon.bringToFront();

        galleryIcon = (CircledImageView) rootView.findViewById(R.id.galleryIcon);
        galleryIcon.setVisibility(View.GONE);
        galleryIcon.bringToFront();

        trailerIcon = (CircledImageView) rootView.findViewById(R.id.trailerIcon);
        trailerIcon.setVisibility(View.GONE);
        trailerIcon.bringToFront();

        // Highest Z-index has to be declared last
        moreIcon = (CircledImageView) rootView.findViewById(R.id.moreIcon);
        moreIcon.bringToFront();

        scrollView = (ObservableParallaxScrollView) rootView.findViewById(R.id.moviedetailsinfo);
        View detailsLayout = rootView.findViewById(R.id.detailsLayout);
        ViewCompat.setElevation(detailsLayout, 2 * getResources().getDisplayMetrics().density);
        ViewCompat.setElevation(moreIcon, 2 * getResources().getDisplayMetrics().density);
        ViewCompat.setElevation(homeIcon, 2 * getResources().getDisplayMetrics().density);
        ViewCompat.setElevation(galleryIcon, 2 * getResources().getDisplayMetrics().density);
        ViewCompat.setElevation(trailerIcon, 2 * getResources().getDisplayMetrics().density);
        // Prevent event bubbling else if you touch on the details layout when the info tab is scrolled it will open gallery view
        detailsLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (activity.getMovieDetailsFragment() != null) {
            moreIcon.setOnClickListener(activity.getMovieDetailsFragment().getOnMoreIconClick());
            activity.getMovieDetailsFragment().getOnMoreIconClick().setKey(false);
        }


        if (activity.getMovieDetailsInfoBundle() != null)
            onOrientationChange(activity.getMovieDetailsInfoBundle());

        if (scrollView != null) {

            scrollView.setTouchInterceptionViewGroup((ViewGroup) activity.getMovieDetailsFragment().getView().findViewById(R.id.containerLayout));
            scrollView.setScrollViewCallbacks(activity.getMovieDetailsFragment());
        }
    }

    public TextView getTitleText() {
        return titleText;
    }

    public TextView getReleaseDate() {
        return releaseDate;
    }

    public ImageView getPosterPath() {
        return posterPath;
    }

    public TextView getStatusText() {
        return statusText;
    }

    public TextView getTagline() {
        return tagline;
    }


    public TextView getRuntime() {
        return runtime;
    }

    public TextView getGenres() {
        return genres;
    }

    public TextView getCountries() {
        return countries;
    }

    public TextView getCompanies() {
        return companies;
    }

    public RatingBar getRatingBar() {
        return ratingBar;
    }

    public TextView getVoteCount() {
        return voteCount;
    }

    public ImageView getBackDropPath() {
        return backDropPath;
    }

    public int getBackDropCheck() {
        return backDropCheck;
    }

    public void setBackDropCheck(int backDropCheck) {
        this.backDropCheck = backDropCheck;
    }

    public CircledImageView getMoreIcon() {
        return moreIcon;
    }

    public CircledImageView getHomeIcon() {
        return homeIcon;
    }

    public CircledImageView getGalleryIcon() {
        return galleryIcon;
    }

    public CircledImageView getTrailerIcon() {
        return trailerIcon;
    }

    public View getRootView() {
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private void onOrientationChange(Bundle args) {
        // BackDrop path
        backDropCheck = args.getInt("backDropCheck");
        if (backDropCheck == 0) {
            activity.setBackDropImage(backDropPath, args.getString("backDropUrl"));
            backDropPath.setTag(args.getString("backDropUrl"));
        }

        // Title
        activity.setText(titleText, args.getString("titleText"));

        // Release date
        activity.setText(releaseDate, args.getString("releaseDate"));

        // Status
        activity.setText(statusText, args.getString("status"));

        // Tag line
        if (!args.getString("tagline").isEmpty())
            tagline.setText(args.getString("tagline"));
        else
            activity.hideTextView(tagline);

        // RunTime
        if (!args.getString("runTime").isEmpty())
            activity.setText(runtime, args.getString("runTime"));
        else activity.hideView(runtime);

        // Genres
        if (!args.getString("genres").isEmpty())
            activity.setText(genres, args.getString("genres"));
        else activity.hideView(genres);

        // Production Countries
        if (!args.getString("productionCountries").isEmpty())
            activity.setText(countries, args.getString("productionCountries"));
        else activity.hideView(countries);

        // Production Companies
        if (!args.getString("productionCompanies").isEmpty()) {
            activity.setText(companies, args.getString("productionCompanies"));
            if (args.getString("productionCountries").isEmpty()) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) companies.getLayoutParams();
                lp.setMargins(0, (int) (28 * getResources().getDisplayMetrics().density), 0, 0);
            }
        } else activity.hideView(companies);


        // Poster path
        if (args.getString("posterPathURL") != null) {
            activity.setImage(posterPath, args.getString("posterPathURL"));
            activity.setImageTag(posterPath, args.getString("posterPathURL"));
        }


        // Rating
        if (args.getString("voteCount").isEmpty()) {
            activity.hideRatingBar(ratingBar);
            activity.hideTextView(voteCount);
        } else {
            ratingBar.setRating(args.getFloat("rating"));
            activity.setText(voteCount, args.getString("voteCount"));
        }

    }

    /**
     * Fired when fragment is destroyed.
     */
    public void onDestroyView() {
        super.onDestroyView();
        activity.setMovieDetailsInfoBundle(null);
        posterPath.setImageDrawable(null);
        backDropPath.setImageDrawable(null);
    }

    public ObservableParallaxScrollView getScrollView() {
        return scrollView;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        activity.setRestoreMovieDetailsAdapterState(true);
        activity.setRestoreMovieDetailsState(false);
        if (activity.getMovieDetailsSimFragment() != null && activity.getMovieDetailsSimFragment().getTimeOut() == 0) {

            activity.getMovieDetailsSimFragment().onSaveInstanceState(new Bundle());
            Bundle bundle = new Bundle();
            Bundle save = activity.getMovieDetailsSimFragment().getSave();

            movieDetails = new MovieDetails();
            movieDetails.setTimeOut(0);
            movieDetails.setSave(save);
            movieDetails.setArguments(bundle);
        } else movieDetails = new MovieDetails();

        activity.getMovieDetailsFragment().setAddToBackStack(true);
        activity.getMovieDetailsFragment().onSaveInstanceState(new Bundle());
        if (activity.getSearchViewCount())
            activity.incSearchMovieDetails();

        activity.setMovieDetailsFragment(null);
        activity.setSaveInMovieDetailsSimFragment(true);
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        movieDetails.setArguments(bundle);
        transaction.replace(R.id.frame_container, movieDetails);
        transaction.commit();


    }


    public boolean canScroll() {
        if (isAdded()) {
            View child = scrollView.getChildAt(0);
            if (child != null) {
                int childHeight = child.getHeight();
                return (scrollView.getHeight() + (119 * getResources().getDisplayMetrics().density)) < childHeight;
            }
        }
        return false;
    }
}
