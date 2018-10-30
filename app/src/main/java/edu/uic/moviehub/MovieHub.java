

package edu.uic.moviehub;

import android.app.Application;



public class MovieHub extends Application {
    public static final String url = "https://api.themoviedb.org/3/";
    public static final String key = "9efa8e2f53f5e67e66743396f186452e";
    public static final String imageUrl = "https://image.tmdb.org/t/p/";

    public static final String trailerImageUrl = "http://i1.ytimg.com/vi/";
    public static final String youtube = "https://www.youtube.com/watch?v=";
    public static final String appId = "95a38b92c5cb4bbfd779c0e2fcaef5a6";

    @Override
    public void onCreate() {
        super.onCreate();
    }

}