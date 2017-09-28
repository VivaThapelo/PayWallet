package com.ktrlabs.thapelo.iwallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * A login screen that offers login via email/password.
 */
public class RegistrationActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private View mProgressView;
    private View mLoginFormView;
    private TextView smsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        // Set up the login form.



        Button button = (Button) findViewById(R.id.reg_signup);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getBaseContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        final Button mEmailSignInButton = (Button) findViewById(R.id.email_reg_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("first click"," got here");
                Random random = new Random();

                final int code = random.nextInt((99999 - 10000) + 1) + 10000;

                final TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.reg_sms_layout);
                textInputLayout.setVisibility(View.VISIBLE);
                textInputLayout.requestFocus();
                if (!((TextView) findViewById(R.id.reg_phone)).getText().toString().isEmpty()) {
                    String num  = "+27" + Integer.parseInt (((TextView) findViewById(R.id.reg_phone)).getText().toString());
                    Log.d("INTL NUMBER",num);
                    String[] sms = {num, Integer.toString(code)};
                    new SMS().execute(sms);
                }

                mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        smsView = (TextView) findViewById(R.id.reg_sms);
                        if (smsView.getText().toString()!=null || smsView.getText().toString()!="") {
                        if (true) {
                           // if (code == Integer.parseInt(smsView.getText().toString())) {
                                smsView.setText(null);
                                smsView.setVisibility(View.GONE);
                                attemptLogin();
                            } else {
                                Toast.makeText(getApplicationContext(), "SMS confirmation failed, start over", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(getIntent());
                            }
                        }
                    }
                    });
            }
        });

        mLoginFormView = findViewById(R.id.reg_form);
        mProgressView = findViewById(R.id.reg_progress);
    }

    public class saveBankDetails extends AsyncTask<String[],String,Boolean> {
        @Override
        protected Boolean doInBackground(String[]... params) {
            String[] result = null;
            String jString = null;
            String[] reza = null;
            String rezo = null;
            String lol = null;
            new BankAi().bankRegistration(getBaseContext(),params[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {

        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }

        if (requestCode ==MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("permission","granted");
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Store values at the time of the login attempt.
       // String email = mRegEmailView.getText().toString();
       // String password = mRegPasswordView.getText().toString();
        String em = "random@mail.com";
        String[] nameView = new String[2];
        nameView[0] = ((EditText) findViewById(R.id.reg_firstname)).getText().toString();
        nameView[1] = ((EditText) findViewById(R.id.reg_lastname)).getText().toString();
        String cell = ((EditText) findViewById(R.id.reg_phone)).getText().toString();
        String pw1 = cell;
        String id = cell;

        try {
            cell = "+27" + Integer.toString(Integer.parseInt(cell));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Log.d("cell changed?",cell);
        final String[] data = {em,nameView[0],nameView[1],id,cell,pw1};

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(pw1) && !isPasswordValid(pw1)) {
            ((EditText) findViewById(R.id.reg_phone)).setError(getString(R.string.error_invalid_password));
            focusView = ((EditText) findViewById(R.id.reg_phone));
            cancel = true;
        } else {
            Button reg_button = (Button) findViewById(R.id.email_reg_in_button);
            //reg_button.setEnabled(true);
        }

        /* Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mRegEmailView.setError(getString(R.string.error_field_required));
            focusView = mRegEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mRegEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mRegEmailView;
            cancel = true;
        } */

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(data);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length()==10 && email.charAt(0)=='0';
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 7;
    }

    public class SMS extends AsyncTask<String[],String,String> {
        @Override
        protected String doInBackground(String[]... params) {
            try {
                new BankAi().SendSms(params[0][0],params[0][1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }



    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        String[] datas;

        UserLoginTask(String[] data) {
            datas = data;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

       /*     for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mRegPassword);
                }
            } */
            Boolean res =new BankAi().bankRegistration(getBaseContext(),datas);
            if (res) {
                Intent intent = new Intent(getApplicationContext(), PinActivity.class);
                intent.putExtra("from", "registration");
                intent.putExtra("number", datas[3]);
                startActivity(intent);
                finish();
                return true;
            } else {
                return false;
            }
            // TODO: register the new account here.
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                ((EditText) findViewById(R.id.reg_phone)).setError(getString(R.string.error_incorrect_password));
                ((EditText) findViewById(R.id.reg_phone)).requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

