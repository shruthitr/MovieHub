package edu.uic.moviehub.activities;import edu.uic.moviehub.MovieHub;import edu.uic.moviehub.R;
import edu.uic.moviehub.adapter.SearchDB;
import edu.uic.moviehub.controller.GenresList;import edu.uic.moviehub.controller.MovieDetails;import edu.uic.moviehub.controller.MovieSlideTab;import edu.uic.moviehub.controller.SearchList;import edu.uic.moviehub.controller.TrailerList;import edu.uic.moviehub.controller.GalleryList;import java.io.BufferedReader;import java.io.File;import java.io.IOException;import java.io.InputStreamReader;import java.lang.reflect.Field;import java.net.HttpURLConnection;import java.net.URL;import java.text.DateFormat;import java.text.SimpleDateFormat;import java.util.ArrayList;import java.util.Date;import java.util.Locale;import java.util.concurrent.CancellationException;import android.app.Fragment;import android.app.FragmentManager;import android.app.FragmentTransaction;import android.app.SearchManager;import android.content.Context;import android.content.Intent;import android.content.pm.ActivityInfo;import android.content.res.Configuration;import android.database.Cursor;import android.graphics.Bitmap;import android.net.ParseException;import android.net.Uri;import android.os.AsyncTask;import android.os.Bundle;import android.os.Handler;import android.support.v4.view.MenuItemCompat;import android.support.v4.widget.SimpleCursorAdapter;import android.support.v7.app.ActionBarDrawerToggle;import android.support.v4.widget.DrawerLayout;import android.support.v7.app.AppCompatActivity;import android.support.v7.widget.SearchView;import android.support.v7.widget.Toolbar;import android.text.Html;import android.util.DisplayMetrics;import android.view.Menu;import android.view.MenuItem;import android.view.View;import android.view.ViewGroup;import android.widget.AdapterView;import android.widget.ArrayAdapter;import android.widget.ImageView;import android.widget.ListView;import android.widget.RatingBar;import android.widget.TextView;import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;import com.nostra13.universalimageloader.core.DisplayImageOptions;import com.nostra13.universalimageloader.core.ImageLoader;import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;import com.nostra13.universalimageloader.core.assist.ImageScaleType;import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;import com.nostra13.universalimageloader.utils.StorageUtils;import net.hockeyapp.android.CrashManager;import net.hockeyapp.android.UpdateManager;import org.json.JSONArray;import org.json.JSONException;import org.json.JSONObject;public class MainActivity extends AppCompatActivity { private final int CacheSize = 52428800; /* 50MB*/private final int MinFreeSpace = 2048; /* 2MB*/private static final long maxMem = Runtime.getRuntime().maxMemory();private DrawerLayout mDrawerLayout;private ListView mDrawerList;private ActionBarDrawerToggle mDrawerToggle;/* nav drawer title*/private CharSequence mDrawerTitle;/* used to store app title*/private CharSequence mTitle;/* slide menu items*/private String[] navMenuTitles;private MovieSlideTab movieSlideTab = new MovieSlideTab();private GenresList genresList = new GenresList();private SearchList searchList = new SearchList();private SearchView searchView;private MenuItem searchViewItem;private ImageLoader imageLoader;/* Create search View listeners*/private SearchViewOnQueryTextListener searchViewOnQueryTextListener = new SearchViewOnQueryTextListener();private onSearchViewItemExpand onSearchViewItemExpand = new onSearchViewItemExpand();private SearchSuggestionListener searchSuggestionListener = new SearchSuggestionListener();private SearchDB searchDB;private Toolbar toolbar;private int oldPos = -1;private boolean isDrawerOpen = false;private DisplayImageOptions optionsWithFade;private DisplayImageOptions optionsWithoutFade;private DisplayImageOptions backdropOptionsWithFade;private DisplayImageOptions backdropOptionsWithoutFade;private int currentMovViewPagerPos;private int currentTVViewPagerPos;private boolean reAttachMovieFragments;private boolean reAttachTVFragments;private TrailerList trailerListView;private MovieDetails movieDetailsFragment;private MovieDetails movieDetailsSimFragment;private boolean saveInMovieDetailsSimFragment;private OnDrawerBackButton onDrawerBackButton = new OnDrawerBackButton();private Bundle movieDetailsInfoBundle;private Bundle movieDetailsCastBundle;private Bundle movieDetailsOverviewBundle;private Bundle castDetailsInfoBundle;private Bundle castDetailsCreditsBundle;private Bundle castDetailsBiographyBundle;private ArrayList<Bundle> movieDetailsBundle = new ArrayList<>();private ArrayList<Bundle> castDetailsBundle = new ArrayList<>();private ArrayList<Bundle> tvDetailsBundle = new ArrayList<>();private boolean restoreMovieDetailsAdapterState;private boolean restoreMovieDetailsState;private int currOrientation;private boolean orientationChanged;private boolean searchViewTap;private boolean searchViewCount;private static int searchMovieDetails;private int lastVisitedSimMovie;private int lastVisitedSimTV;private int lastVisitedMovieInCredits;private HttpURLConnection conn;private SimpleCursorAdapter searchAdapter;private String query;private JSONAsyncTask request;private SearchImgLoadingListener searchImgLoadingListener;private int iconMarginConstant;private int iconMarginLandscape;private int iconConstantSpecialCase;private int threeIcons;private int threeIconsToolbar;private int twoIcons;private int twoIconsToolbar;private int oneIcon;private int oneIconToolbar;private boolean phone;private DateFormat dateFormat;private GalleryList galleryListView;@Override protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);setContentView(R.layout.activity_main);mTitle = mDrawerTitle = getTitle();navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);/*slide menu*/mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);mDrawerList = (ListView) findViewById(R.id.list_slidermenu);ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.drawer_header, null, false);ImageView drawerBackButton = (ImageView) header.findViewById(R.id.drawerBackButton);drawerBackButton.setOnClickListener(onDrawerBackButton);mDrawerList.addHeaderView(header);mDrawerList.setOnItemClickListener(new SlideMenuClickListener());mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, R.id.title, navMenuTitles));toolbar = (Toolbar) findViewById(R.id.toolbar);if (toolbar != null) { setSupportActionBar(toolbar);toolbar.bringToFront(); }mDrawerToggle = new ActionBarDrawerToggle(this,  /* host Activity */ mDrawerLayout,  /* DrawerLayout object */ toolbar, R.string.app_name, /* nav drawer open - description for accessibility*/ R.string.app_name /* nav drawer close - description for accessibility*/) {public void onDrawerClosed(View drawerView) { super.onDrawerClosed(drawerView);/* calling onPrepareOptionsMenu() to show search view*/invalidateOptionsMenu();syncState(); }public void onDrawerOpened(View drawerView) { super.onDrawerOpened(drawerView);/* calling onPrepareOptionsMenu() to hide search view*/invalidateOptionsMenu();syncState(); }/* updates the title, toolbar transparency and search view*/public void onDrawerSlide(View drawerView, float slideOffset) { super.onDrawerSlide(drawerView, slideOffset);if (slideOffset > .55 && !isDrawerOpen) {/* opening drawer mDrawerTitle is app title*/getSupportActionBar().setTitle(mDrawerTitle);invalidateOptionsMenu();isDrawerOpen = true; } else if (slideOffset < .45 && isDrawerOpen) {/* closing drawer mTitle is title of the current view, can be movies, tv shows or movie title*/getSupportActionBar().setTitle(mTitle);invalidateOptionsMenu();isDrawerOpen = false; } }};mDrawerLayout.setDrawerListener(mDrawerToggle);/* Get the action bar title to set padding*/TextView titleTextView = null;try { Field f = toolbar.getClass().getDeclaredField("mTitleTextView");f.setAccessible(true);titleTextView = (TextView) f.get(toolbar); } catch (NoSuchFieldException e) { } catch (IllegalAccessException e) { }if (titleTextView != null) { float scale = getResources().getDisplayMetrics().density;titleTextView.setPadding((int) scale * 15, 0, 0, 0); }phone = getResources().getBoolean(R.bool.portrait_only);searchDB = new SearchDB(getApplicationContext());if (savedInstanceState == null) {/* Check orientation and lock to portrait if we are on phone*/if (phone) setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);/* on first time display view for first nav item*/displayView(1);checkForUpdates();/* Universal Loader options and configuration.*/DisplayImageOptions options = new DisplayImageOptions.Builder()/* Bitmaps in RGB_565 consume 2 times less memory than in ARGB_8888.*/.bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(false).showImageOnLoading(R.drawable.placeholder_default).showImageForEmptyUri(R.drawable.placeholder_default).showImageOnFail(R.drawable.placeholder_default).cacheOnDisk(true).build();Context context = this;File cacheDir = StorageUtils.getCacheDirectory(context);/* Create global configuration and initialize ImageLoader with this config*/ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).diskCache(new UnlimitedDiscCache(cacheDir)) /* default*/.defaultDisplayImageOptions(options).build();ImageLoader.getInstance().init(config);/* Check cache size*/long size = 0;File[] filesCache = cacheDir.listFiles();for (File file : filesCache) size += file.length();if (cacheDir.getUsableSpace() < MinFreeSpace || size > CacheSize) { ImageLoader.getInstance().getDiskCache().clear();searchDB.cleanSuggestionRecords(); } } else { oldPos = savedInstanceState.getInt("oldPos");currentMovViewPagerPos = savedInstanceState.getInt("currentMovViewPagerPos");currentTVViewPagerPos = savedInstanceState.getInt("currentTVViewPagerPos");restoreMovieDetailsState = savedInstanceState.getBoolean("restoreMovieDetailsState");restoreMovieDetailsAdapterState = savedInstanceState.getBoolean("restoreMovieDetailsAdapterState");movieDetailsBundle = savedInstanceState.getParcelableArrayList("movieDetailsBundle");castDetailsBundle = savedInstanceState.getParcelableArrayList("castDetailsBundle");tvDetailsBundle = savedInstanceState.getParcelableArrayList("tvDetailsBundle");currOrientation = savedInstanceState.getInt("currOrientation");lastVisitedSimMovie = savedInstanceState.getInt("lastVisitedSimMovie");lastVisitedSimTV = savedInstanceState.getInt("lastVisitedSimTV");lastVisitedMovieInCredits = savedInstanceState.getInt("lastVisitedMovieInCredits");saveInMovieDetailsSimFragment = savedInstanceState.getBoolean("saveInMovieDetailsSimFragment");FragmentManager fm = getFragmentManager();if (fm.getBackStackEntryCount() == 0 || !fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName().equals("galleryList")) new Handler().post(new Runnable() {@Override public void run() { if (getSupportActionBar() != null && !getSupportActionBar().isShowing()) getSupportActionBar().show(); }}); }/* Get reference for the imageLoader*/imageLoader = ImageLoader.getInstance();optionsWithFade = new DisplayImageOptions.Builder()/* Bitmaps in RGB_565 consume 2 times less memory than in ARGB_8888.*/.bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(500)).imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(false).showImageOnLoading(R.color.black).showImageForEmptyUri(R.color.black).showImageOnFail(R.color.black).cacheOnDisk(true).build();optionsWithoutFade = new DisplayImageOptions.Builder()/* Bitmaps in RGB_565 consume 2 times less memory than in ARGB_8888.*/.bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(false).showImageOnLoading(R.color.black).showImageForEmptyUri(R.color.black).showImageOnFail(R.color.black).cacheOnDisk(true).build();/* Options used for the backdrop image in movie and tv details and gallery*/backdropOptionsWithFade = new DisplayImageOptions.Builder()/* Bitmaps in RGB_565 consume 2 times less memory than in ARGB_8888.*/.bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(500)).imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(false).showImageOnLoading(R.drawable.placeholder_backdrop).showImageForEmptyUri(R.drawable.placeholder_backdrop).showImageOnFail(R.drawable.placeholder_backdrop).cacheOnDisk(true).build();backdropOptionsWithoutFade = new DisplayImageOptions.Builder()/* Bitmaps in RGB_565 consume 2 times less memory than in ARGB_8888.*/.bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(false).showImageOnLoading(R.drawable.placeholder_backdrop).showImageForEmptyUri(R.drawable.placeholder_backdrop).showImageOnFail(R.drawable.placeholder_backdrop).cacheOnDisk(true).build();trailerListView = new TrailerList();galleryListView = new GalleryList();if (currOrientation != getResources().getConfiguration().orientation) orientationChanged = true;currOrientation = getResources().getConfiguration().orientation;iconConstantSpecialCase = 0;if (phone) { iconMarginConstant = 0;iconMarginLandscape = 0;DisplayMetrics displayMetrics = getResources().getDisplayMetrics();int width = displayMetrics.widthPixels;int height = displayMetrics.heightPixels;if (width <= 480 && height <= 800) iconConstantSpecialCase = -70;threeIcons = 128;threeIconsToolbar = 72;twoIcons = 183;twoIconsToolbar = 127;oneIcon = 238;oneIconToolbar = 182; } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { iconMarginConstant = 232;iconMarginLandscape = 300;threeIcons = 361;threeIconsToolbar = 295;twoIcons = 416;twoIconsToolbar = 351;oneIcon = 469;oneIconToolbar = 407; } else { iconMarginConstant = 82;iconMarginLandscape = 0;threeIcons = 209;threeIconsToolbar = 146;twoIcons = 264;twoIconsToolbar = 200;oneIcon = 319;oneIconToolbar = 256; }dateFormat = android.text.format.DateFormat.getDateFormat(this); }@Override public void onBackPressed() { FragmentManager fm = getFragmentManager();if (mDrawerLayout.isDrawerOpen(mDrawerList)) mDrawerLayout.closeDrawer(mDrawerList);else if (searchViewItem.isActionViewExpanded()) searchViewItem.collapseActionView();else if (fm.getBackStackEntryCount() > 0) { String backStackEntry = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();if (backStackEntry.equals("movieList")) reAttachMovieFragments = true;if (backStackEntry.equals("searchList:1")) reAttachMovieFragments = true;restoreMovieDetailsState = true;restoreMovieDetailsAdapterState = false;if (orientationChanged) restoreMovieDetailsAdapterState = true;fm.popBackStack(); } else super.onBackPressed(); }@Override protected void onPause() { super.onPause();UpdateManager.unregister(); }@Override protected void onResume() { super.onResume();checkForCrashes(); }private void checkForCrashes() {
        CrashManager.register(this, MovieHub.appId);
    }


    private void checkForUpdates() {
        // Remove this for store / production builds!
        UpdateManager.register(this, MovieHub.appId);
    }

    /**
     * Slide menu item click listener.
     * Fired when you click on item from the slide menu.
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchViewItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(searchViewOnQueryTextListener);
        searchView.setOnSuggestionListener(searchSuggestionListener);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchViewItemC =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchViewItemC.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        String[] from = {SearchManager.SUGGEST_COLUMN_ICON_1, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2};
        int[] to = {R.id.posterPath, R.id.title, R.id.info};
        searchAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.suggestionrow, null, from, to, 0) {
            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }
        };
        searchViewItemC.setSuggestionsAdapter(searchAdapter);

        MenuItemCompat.setOnActionExpandListener(searchViewItem, onSearchViewItemExpand);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isDrawerOpen)
            menu.findItem(R.id.search).setVisible(false);
        else if (oldPos == 4)
            menu.findItem(R.id.search).setVisible(false);
        else menu.findItem(R.id.search).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }



    private void displayView(int position) {
        if (position != 0) {

            FragmentManager fm = getFragmentManager();
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Fragment fragment = null;
            searchMovieDetails = 0;
            searchViewCount = false;
            resetMovieDetailsBundle();
            resetCastDetailsBundle();

            switch (position) {

                case 1:
                    reAttachMovieFragments = true;
                    if (oldPos == position) {
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    }
                    fragment = movieSlideTab;
                    break;

                case 2:
                    if (oldPos == position) {
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    }
                    fragment = getFragmentManager().findFragmentByTag("genres");
                    if (fragment == null)
                        fragment = genresList;
                    if (genresList.getBackState() == 0)
                        genresList.updateList();
                    break;
                case 3:

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    finish();
                    break;
                case 4:

                    Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent1);
                    finish();

                    break;

                default:
                    break;
            }
            oldPos = position;
            if (fragment != null) {
                fm.beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();
                mDrawerList.setItemChecked(position, true);
                mDrawerList.setSelection(position);
                setTitle(navMenuTitles[position - 1]);
                mDrawerLayout.closeDrawer(mDrawerList);
                try {
                    movieSlideTab.showInstantToolbar();
                    } catch (NullPointerException e) {
                }
                System.gc();
            }
        } else {
            mDrawerList.setItemChecked(oldPos, true);
        }
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(mTitle);

        if (!searchViewTap) {
            invalidateOptionsMenu();
        } else searchViewTap = false;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    private class SearchViewOnQueryTextListener implements SearchView.OnQueryTextListener {


        @Override
        public boolean onQueryTextChange(String newText) {
            query = newText;
            query = query.replaceAll("[\\s%\"^#<>{}\\\\|`]", "%20");

            if (query.length() > 1) {
                new Thread(new Runnable() {
                    public void run() {
                        try {

                            if (request != null)
                                request.cancel(true);

                            if (conn != null)
                                conn.disconnect();

                            request = new JSONAsyncTask();
                            request.execute(MovieHub.url + "search/multi?query=" + query + "?&api_key=" + MovieHub.key);
                            request.setQuery(query);
                        } catch (CancellationException e) {
                            if (request != null)
                                request.cancel(true);
                            // we abort the http request, else it will cause problems and slow connection later
                            if (conn != null)
                                conn.disconnect();
                        }
                    }
                }).start();
            } else {
                String[] selArgs = {query};
                searchDB.cleanAutoCompleteRecords();
                Cursor c = searchDB.getSuggestions(selArgs);
                searchAdapter.changeCursor(c);
            }
            return true;
        }


        @Override
        public boolean onQueryTextSubmit(String query) {
            searchList.setQuery(query);
            searchView.clearFocus();
            return true;
        }
    }


    public class onSearchViewItemExpand implements MenuItemCompat.OnActionExpandListener {
        FragmentManager fm = getFragmentManager();


        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {

            // search view key
            searchViewTap = true;

            if (searchMovieDetails > 0)
                clearMovieDetailsBackStack();


            // check if we are already in the search view to prevent double adding in the back stack
            if (fm.getBackStackEntryCount() == 0 || !fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName().startsWith("searchList")) {
                // checks if the search view has been created, if it is created this method pops it from the back stack
                // also this clears back stack history until the search list
                boolean fragmentPopped = false;
                if (fm.popBackStackImmediate("searchList:1", 0))
                    fragmentPopped = true;

                if (fm.popBackStackImmediate("searchList:2", 0))
                    fragmentPopped = true;


                if (!fragmentPopped) {
                    if (movieDetailsFragment != null && movieDetailsFragment.getId() != 0) {
                        // check if the movie is already in our backStack
                        if (movieDetailsBundle.size() > 0) {
                            if (!getSupportActionBar().getTitle().equals(movieDetailsBundle.get(movieDetailsBundle.size() - 1).getString("title"))) {
                                movieDetailsFragment.setAddToBackStack(true);
                                movieDetailsFragment.onSaveInstanceState(new Bundle());
                            }
                        } else {
                            movieDetailsFragment.setAddToBackStack(true);
                            movieDetailsFragment.onSaveInstanceState(new Bundle());
                        }
                    }

                    FragmentTransaction transaction = fm.beginTransaction();
                    searchList.setTitle(getResources().getString(R.string.search_title));
                    transaction.replace(R.id.frame_container, searchList);
                    // add the current transaction to the back stack:
                    transaction.addToBackStack("searchList:" + oldPos);
                    transaction.commit();
                }
            }
            return true;
        }


        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            return true;
        }
    }


    public void collapseSearchView() {
        searchViewItem.collapseActionView();
    }

    public void setText(final TextView text, final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }


    public void setTextFromHtml(final TextView text, final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(Html.fromHtml(value));
            }
        });
    }


    public void setImage(final ImageView img, final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageLoader.displayImage(MovieHub.imageUrl + getResources().getString(R.string.imageSize) + url, img);
            }
        });
    }


    public void setImageTag(final ImageView img, final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                img.setTag(url);
            }
        });
    }


    public void setBackDropImage(final ImageView img, final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (imageLoader.getDiskCache().get(MovieHub.imageUrl + getResources().getString(R.string.backDropImgSize) + url).exists())
                    imageLoader.displayImage(MovieHub.imageUrl + getResources().getString(R.string.backDropImgSize) + url, img, backdropOptionsWithoutFade);
                else
                    imageLoader.displayImage(MovieHub.imageUrl + getResources().getString(R.string.backDropImgSize) + url, img, backdropOptionsWithFade);
            }
        });
    }


    public void setRatingBarValue(final RatingBar ratingBar, final float value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ratingBar.setRating(value);
            }
        });
    }



    public void hideLayout(final ViewGroup layout) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(View.GONE);
            }
        });
    }


    public void hideView(final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.GONE);
            }
        });
    }



    public void showView(final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        });
    }



    public void hideTextView(final TextView textView) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setVisibility(View.GONE);
            }
        });
    }



    public void hideRatingBar(final RatingBar ratingBar) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ratingBar.setVisibility(View.GONE);
            }
        });
    }

    public void invisibleView(final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.INVISIBLE);
            }
        });
    }


    public int getCurrentMovViewPagerPos() {
        return currentMovViewPagerPos;
    }


    public void setCurrentMovViewPagerPos(int currentMovViewPagerPos) {
        this.currentMovViewPagerPos = currentMovViewPagerPos;
    }


    public DrawerLayout getMDrawerLayout() {
        return mDrawerLayout;
    }


    public boolean getReAttachMovieFragments() {
        return reAttachMovieFragments;
    }

    public void setReAttachMovieFragments(boolean reAttachMovieFragments) {
        this.reAttachMovieFragments = reAttachMovieFragments;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("oldPos", oldPos);
        outState.putInt("currentMovViewPagerPos", currentMovViewPagerPos);
        outState.putBoolean("restoreMovieDetailsAdapterState", restoreMovieDetailsAdapterState);
        outState.putBoolean("restoreMovieDetailsState", restoreMovieDetailsState);
        outState.putParcelableArrayList("movieDetailsBundle", movieDetailsBundle);
        outState.putParcelableArrayList("castDetailsBundle", castDetailsBundle);
        outState.putInt("currOrientation", currOrientation);
        outState.putInt("lastVisitedSimMovie", lastVisitedSimMovie);
        outState.putInt("lastVisitedSimTV", lastVisitedSimTV);
        outState.putInt("lastVisitedMovieInCredits", lastVisitedMovieInCredits);
        outState.putBoolean("saveInMovieDetailsSimFragment", saveInMovieDetailsSimFragment);
    }


    public static long getMaxMem() {
        return maxMem;
    }


    public TrailerList getTrailerListView() {
        return trailerListView;
    }


    public GalleryList getGalleryListView() {
        return galleryListView;
    }



    public void setMovieDetailsFragment(MovieDetails movieDetailsFragment) {
        this.movieDetailsFragment = movieDetailsFragment;
    }


    public MovieDetails getMovieDetailsFragment() {
        return movieDetailsFragment;

    }


    public DisplayImageOptions getOptionsWithFade() {
        return optionsWithFade;
    }


    public DisplayImageOptions getOptionsWithoutFade() {
        return optionsWithoutFade;
    }


    public class OnDrawerBackButton implements View.OnClickListener {
        public OnDrawerBackButton() {

        }

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    }


    public Bundle getMovieDetailsInfoBundle() {
        return movieDetailsInfoBundle;
    }


    public void setMovieDetailsInfoBundle(Bundle movieDetailsInfoBundle) {
        this.movieDetailsInfoBundle = movieDetailsInfoBundle;
    }


    public Bundle getMovieDetailsCastBundle() {
        return movieDetailsCastBundle;
    }


    public void setMovieDetailsCastBundle(Bundle movieDetailsCastBundle) {
        this.movieDetailsCastBundle = movieDetailsCastBundle;
    }


    public Bundle getMovieDetailsOverviewBundle() {
        return movieDetailsOverviewBundle;
    }


    public void setMovieDetailsOverviewBundle(Bundle movieDetailsOverviewBundle) {
        this.movieDetailsOverviewBundle = movieDetailsOverviewBundle;
    }


    public Bundle getCastDetailsInfoBundle() {
        return castDetailsInfoBundle;
    }


    public void setCastDetailsInfoBundle(Bundle castDetailsInfoBundle) {
        this.castDetailsInfoBundle = castDetailsInfoBundle;
    }


    public Bundle getCastDetailsCreditsBundle() {
        return castDetailsCreditsBundle;
    }


    public void setCastDetailsCreditsBundle(Bundle castDetailsCreditsBundle) {
        this.castDetailsCreditsBundle = castDetailsCreditsBundle;
    }


    public Bundle getCastDetailsBiographyBundle() {
        return castDetailsBiographyBundle;
    }


    public void setCastDetailsBiographyBundle(Bundle castDetailsBiographyBundle) {
        this.castDetailsBiographyBundle = castDetailsBiographyBundle;
    }


    public void addMovieDetailsBundle(Bundle movieDetailsBundle) {
        this.movieDetailsBundle.add(movieDetailsBundle);
    }


    public void removeMovieDetailsBundle(int pos) {
        movieDetailsBundle.remove(pos);
    }


    public void resetMovieDetailsBundle() {
        movieDetailsBundle = new ArrayList<>();
    }


    public ArrayList<Bundle> getMovieDetailsBundle() {
        return movieDetailsBundle;
    }



    public void removeCastDetailsBundle(int pos) {
        castDetailsBundle.remove(pos);
    }


    public void resetCastDetailsBundle() {
        castDetailsBundle = new ArrayList<>();
    }





    public void setRestoreMovieDetailsState(boolean restoreMovieDetailsState) {
        this.restoreMovieDetailsState = restoreMovieDetailsState;
    }

    public boolean getRestoreMovieDetailsState() {
        return restoreMovieDetailsState;
    }


    public void setRestoreMovieDetailsAdapterState(boolean restoreMovieDetailsAdapterState) {
        this.restoreMovieDetailsAdapterState = restoreMovieDetailsAdapterState;
    }


    public boolean getRestoreMovieDetailsAdapterState() {
        return restoreMovieDetailsAdapterState;
    }


    public void setOrientationChanged(boolean orientationChanged) {
        this.orientationChanged = orientationChanged;
    }


    public boolean getSearchViewCount() {
        return searchViewCount;
    }


    public void setSearchViewCount(boolean searchViewCount) {
        this.searchViewCount = searchViewCount;
    }


    public void incSearchMovieDetails() {
        searchMovieDetails++;
    }


    public void decSearchMovieDetails() {
        searchMovieDetails--;
    }




    public void clearMovieDetailsBackStack() {
        if (movieDetailsBundle.size() > 0) {
            for (int i = 0; i < searchMovieDetails; i++) {
                removeMovieDetailsBundle(movieDetailsBundle.size() - 1);
            }
        }
        searchMovieDetails = 0;
    }




    public MovieSlideTab getMovieSlideTab() {
        return movieSlideTab;
    }

    public void setMovieSlideTab(MovieSlideTab movieSlideTab) {
        this.movieSlideTab = movieSlideTab;
    }

    public GenresList getGenresList() {
        return genresList;
    }


    public Toolbar getToolbar() {
        return toolbar;
    }


    private class SearchSuggestionListener implements SearchView.OnSuggestionListener {


        @Override
        public boolean onSuggestionClick(int position) {
            Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
            if (searchView.getQuery().length() > 1)
                addSuggestion(cursor);

            searchList.onSuggestionClick(cursor.getInt(4), cursor.getString(5), cursor.getString(1));
            return true;
        }


        public boolean onSuggestionSelect(int position) {
            Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
            if (searchView.getQuery().length() > 1)
                addSuggestion(cursor);

            searchList.onSuggestionClick(cursor.getInt(4), cursor.getString(5), cursor.getString(1));
            return true;
        }

    }

    private void addSuggestion(Cursor cursor) {
        if (searchDB.getSuggestionSize() > 9) {
            searchDB.cleanSuggestionRecords();
        }

        searchDB.insertSuggestion(cursor.getInt(4), cursor.getString(1), Uri.parse(cursor.getString(3)), cursor.getString(2), cursor.getString(5));
    }


    public void clearSearchCount() {
        searchMovieDetails = 0;


    }

    public int getOldPos() {
        return oldPos;
    }




    public void setLastVisitedSimMovie(int lastVisitedSimMovie) {
        this.lastVisitedSimMovie = lastVisitedSimMovie;
    }





    public boolean getSaveInMovieDetailsSimFragment() {
        return saveInMovieDetailsSimFragment;
    }

    public void setSaveInMovieDetailsSimFragment(boolean saveInMovieDetailsSimFragment) {
        this.saveInMovieDetailsSimFragment = saveInMovieDetailsSimFragment;
    }

    public MovieDetails getMovieDetailsSimFragment() {
        return movieDetailsSimFragment;
    }

    public void setMovieDetailsSimFragment(MovieDetails movieDetailsSimFragment) {
        this.movieDetailsSimFragment = movieDetailsSimFragment;
    }





    /**
     * This class handles the connection to our backend server.
     * If the connection is successful we set our list data.
     */
    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {
        private ArrayList<Integer> idsList;
        private ArrayList<String> posterPathList;
        private String queryZ;

        public void setQuery(String query) {
            this.queryZ = query;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(10000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                int status = conn.getResponseCode();
                if (status == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();

                    JSONObject searchData = new JSONObject(sb.toString());
                    JSONArray searchResultsArray = searchData.getJSONArray("results");
                    int length = searchResultsArray.length();
                    if (length > 10)
                        length = 10;

                    searchDB.cleanAutoCompleteRecords();
                    idsList = new ArrayList<>();
                    posterPathList = new ArrayList<>();
                    for (int i = 0; i < length; i++) {
                        JSONObject object = searchResultsArray.getJSONObject(i);

                        int id = 0;
                        String title = "", posterPath = "", releaseDate = "", mediaType = "";


                        if (object.has("id") && object.getInt("id") != 0)
                            id = object.getInt("id");

                        if (object.has("title"))
                            title = object.getString("title");

                        if (object.has("name"))
                            title = object.getString("name");
                        title = title.replaceAll("'", "");

                        if (object.has("poster_path") && !object.getString("poster_path").equals("null") && !object.getString("poster_path").isEmpty())
                            posterPath = MovieHub.imageUrl + "w154" + object.getString("poster_path");


                        if (object.has("profile_path") && !object.getString("profile_path").equals("null") && !object.getString("profile_path").isEmpty())
                            posterPath = MovieHub.imageUrl + "w154" + object.getString("profile_path");

                        if (object.has("release_date") && !object.getString("release_date").equals("null") && !object.getString("release_date").isEmpty()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            try {
                                Date date = sdf.parse(object.getString("release_date"));
                                String formattedDate = dateFormat.format(date);
                                releaseDate = "(" + formattedDate + ")";
                            } catch (java.text.ParseException e) {
                            }
                        }

                        if (object.has("first_air_date") && !object.getString("first_air_date").equals("null") && !object.getString("first_air_date").isEmpty()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            try {
                                Date date = sdf.parse(object.getString("first_air_date"));
                                String formattedDate = dateFormat.format(date);
                                releaseDate = "(" + formattedDate + ")";
                            } catch (java.text.ParseException e) {
                            }
                        }

                        if (object.has("media_type") && !object.getString("media_type").isEmpty())
                            mediaType = object.getString("media_type");


                        Uri path = Uri.parse("android.resource://edu.uic.moviehub/" + R.drawable.placeholder_default);
                        if (!posterPath.isEmpty()) {
                            if (imageLoader.getDiskCache().get(posterPath).exists())
                                path = Uri.fromFile(new File(imageLoader.getDiskCache().get(posterPath).getPath()));
                            else {
                                idsList.add(id);
                                posterPathList.add(posterPath);
                            }
                        }

                        searchDB.insertAutoComplete(id, title, path, releaseDate, mediaType);


                    }

                    return true;
                }


            } catch (ParseException | IOException | JSONException e) {
                if (conn != null)
                    conn.disconnect();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (query.length() > 1) {
                searchAdapter.changeCursor(searchDB.autoComplete());

                if (posterPathList != null && posterPathList.size() > 0) {
                    for (int i = 0; i < posterPathList.size(); i++) {
                        searchImgLoadingListener = new SearchImgLoadingListener(idsList.get(i), queryZ);
                        imageLoader.loadImage(posterPathList.get(i), searchImgLoadingListener);
                    }
                }
            }


        }

    }

    private class SearchImgLoadingListener extends SimpleImageLoadingListener {
        private int currId;
        private String queryZ;

        public SearchImgLoadingListener(int currId, String query) {
            this.currId = currId;
            this.queryZ = query;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (query.equals(queryZ)) {
                Uri uriFile = Uri.fromFile(new File(imageLoader.getDiskCache().get(imageUri).getPath()));
                searchDB.updateImg(currId, uriFile);
                searchAdapter.changeCursor(searchDB.autoComplete());
            }
        }
    }


    public int getIconMarginConstant() {
        return iconMarginConstant;
    }

    public int getIconMarginLandscape() {
        return iconMarginLandscape;
    }

    public int getIconConstantSpecialCase() {
        return iconConstantSpecialCase;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public int getOneIcon() {
        return oneIcon;
    }

    public int getOneIconToolbar() {
        return oneIconToolbar;
    }

    public int getTwoIcons() {
        return twoIcons;
    }

    public int getTwoIconsToolbar() {
        return twoIconsToolbar;
    }

    public int getThreeIcons() {
        return threeIcons;
    }

    public int getThreeIconsToolbar() {
        return threeIconsToolbar;
    }

}