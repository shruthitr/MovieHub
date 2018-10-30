package edu.uic.moviehub.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.uic.moviehub.activities.MainActivity;
import edu.uic.moviehub.MovieHub;
import edu.uic.moviehub.R;
import edu.uic.moviehub.adapter.GenresAdapter;
import edu.uic.moviehub.model.GenresModel;

public class GenresList extends Fragment implements AdapterView.OnItemClickListener {
    private MainActivity activity;
    private ProgressBar spinner;
    private AbsListView listView;
    private ArrayList<GenresModel> genresList;
    private GenresAdapter genresAdapter;
    private MovieList movieList;
    private int backState;
    private View rootView;

    private HttpURLConnection conn;
    private Bundle save;

    public GenresList() {
    }


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

        rootView = inflater.inflate(R.layout.genreslist, container, false);
        activity = ((MainActivity) getActivity());
        spinner = (ProgressBar) rootView.findViewById(R.id.progressBar);
        listView = (AbsListView) rootView.findViewById(R.id.genresList);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        movieList = new MovieList();

        if (save != null) {
            backState = save.getInt("backState");
            if (backState == 1) {
                genresList = save.getParcelableArrayList("listData");
                genresAdapter = new GenresAdapter(getActivity(), R.layout.genresrow, genresList);
            }
        }
        if (backState == 0) {
            updateList();
        } else {
            listView.setAdapter(genresAdapter);
        }

        getActivity().setTitle(getResources().getString(R.string.genresTitle));
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (movieList.getCurrentList().equals("genre/" + genresList.get(position).getId() + "/movies"))
            movieList.setBackState(1);
        else {
            movieList.setCurrentList("genre/" + genresList.get(position).getId() + "/movies");
            movieList.setBackState(0);
        }
        movieList.setTitle(genresList.get(position).getName());
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle args = new Bundle();
        args.putString("currentList", "genresList");
        movieList.setArguments(args);
        transaction.replace(R.id.frame_container, movieList);
        // add the current transaction to the back stack:
        transaction.addToBackStack("genresList");
        transaction.commit();
        backState = 1;
    }


    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.showView(spinner);
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

                    JSONObject jsonData = new JSONObject(sb.toString());
                    JSONArray genresArray = jsonData.getJSONArray("genres");

                    for (int i = 0; i < genresArray.length(); i++) {
                        JSONObject object = genresArray.getJSONObject(i);
                        GenresModel genre = new GenresModel();
                        genre.setName(object.getString("name"));
                        genre.setId(object.getInt("id"));
                        genresList.add(genre);
                    }

                    return true;
                }


            } catch (IOException | JSONException e) {
                if (conn != null)
                    conn.disconnect();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            activity.hideView(spinner);

            if (!result) {
                if (getResources() != null) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                    backState = 0;
                }
            } else {
                genresAdapter.notifyDataSetChanged();
                backState = 1;
            }
        }
    }

    public void updateList() {
        if (getActivity() != null) {
            listView = (AbsListView) rootView.findViewById(R.id.genresList);
            genresList = new ArrayList<>();
            genresAdapter = new GenresAdapter(getActivity(), R.layout.genresrow, genresList);
            listView.setAdapter(genresAdapter);
            final JSONAsyncTask request = new JSONAsyncTask();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        request.execute(MovieHub.url + "genre/movie/list?&api_key=" + MovieHub.key).get(10000, TimeUnit.MILLISECONDS);
                    } catch (TimeoutException | ExecutionException | InterruptedException e) {
                        request.cancel(true);

                        if (conn != null)
                            conn.disconnect();
                        if (spinner != null)
                            activity.hideView(spinner);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        backState = 0;
                    }
                }
            }).start();
        }
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (save != null)
            outState.putBundle("save", save);
        else {
            Bundle send = new Bundle();
            send.putInt("backState", backState);
            if (backState == 1) {
                send.putParcelableArrayList("listData", genresList);

                if (listView != null) {
                    Parcelable listState = listView.onSaveInstanceState();
                    send.putParcelable("listViewScroll", listState);
                }

            }
            outState.putBundle("save", send);

        }
    }


    public int getBackState() {
        return backState;
    }

    public MovieList getMovieListView() {
        return movieList;
    }


    public void onDestroyView() {
        super.onDestroyView();
    }
}
