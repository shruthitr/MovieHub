package edu.uic.moviehub.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import edu.uic.moviehub.R;
import edu.uic.moviehub.model.User;
import edu.uic.moviehub.sql.DatabaseHelper;

public class UsersListActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatActivity activity = UsersListActivity.this;
    private AppCompatTextView textViewName;
    private List<User> listUsers;
    private DatabaseHelper databaseHelper;
    private AppCompatButton findMovieButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//create a toobar and set app name
        toolbar.setTitle("Movie Hub");
        setSupportActionBar(toolbar);
        initViews();
        initObjects();
        initListeners();

    }

    /**
     * This method is to initialize views
     */
    private void initViews() {
        textViewName = findViewById(R.id.textViewName);
        findMovieButton = findViewById(R.id.findMovies);

    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        listUsers = new ArrayList<>();
        databaseHelper = new DatabaseHelper(activity);
        String emailFromIntent = getIntent().getStringExtra("EMAIL");
        textViewName.setText(emailFromIntent);
        getDataFromSQLite();
    }

    private void initListeners() {
        findMovieButton.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.findMovies:
                //navigates to movie details to get the list of movies
                Intent intentRegister = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intentRegister);
                break;

        }
    }
    private void getDataFromSQLite() {
        // AsyncTask is used that SQLite operation not blocks the UI Thread.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                listUsers.clear();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }
        }.execute();
    }
}
