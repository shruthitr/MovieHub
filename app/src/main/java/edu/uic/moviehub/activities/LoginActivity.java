package edu.uic.moviehub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import edu.uic.moviehub.R;
import edu.uic.moviehub.helper.InputValidation;
import edu.uic.moviehub.sql.DatabaseHelper;
/** used to infate login page and check for login ocnditions **/

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final AppCompatActivity activity = LoginActivity.this;
    private NestedScrollView nestedScrollView;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private AppCompatButton appCompatButtonLogin;
    private AppCompatTextView textViewLinkRegister;
    private TextView textViewName;
    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    private static final long maxMem = Runtime.getRuntime().maxMemory();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//create a toobar and set app name
        toolbar.setTitle("Movie Hub");
        setSupportActionBar(toolbar);
        initViews();
        initListeners();
        initObjects();
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {

        nestedScrollView = findViewById(R.id.nestedScrollView);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputEditTextEmail = findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = findViewById(R.id.textInputEditTextPassword);
        appCompatButtonLogin = findViewById(R.id.appCompatButtonLogin);
        textViewLinkRegister = findViewById(R.id.textViewLinkRegister);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);//listener for login button
        textViewLinkRegister.setOnClickListener(this);//listener for registration link
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);//used to access sqllite
        inputValidation = new InputValidation(activity);//helper class for validating input fields such as emailid,pwd

    }

    /**used to check and navigate to corresodning activity **/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appCompatButtonLogin:
                //used to check if button clicked is login and go to database to valid login details through this method
                verifyFromSQLite();
                break;
            case R.id.textViewLinkRegister:
                // used to navigate to register page through resigter activity class
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }

    /**
     * This method is to validate the input text fields and verify login credentials
     */
    private void verifyFromSQLite() {

        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        }

        if (databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim()
                , textInputEditTextPassword.getText().toString().trim())) {


            Intent accountsIntent = new Intent(activity, UsersListActivity.class);
            accountsIntent.putExtra("EMAIL", textInputEditTextEmail.getText().toString().trim());//used to display logged in user email id
            emptyInputEditText();
            startActivity(accountsIntent);


        } else {
            //  show errors  message that record is wrong
            Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * reset all input fields
     */
    private void emptyInputEditText() {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }
}
