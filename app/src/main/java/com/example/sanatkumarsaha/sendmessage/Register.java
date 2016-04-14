package com.example.sanatkumarsaha.sendmessage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class Register extends AppCompatActivity implements LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:password:example_foo:9933890398", "bar@example.com:password:example_bar:9999999999"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mCPasswordView;
    private EditText mUsernameView;
    private EditText mMobileView;
    private EditText mAgeView;
    private Spinner mGenderView;
    private Spinner mBloodView;
    private TextView mSpinnerTextView;
    private TextView mSpinner2TextView;
    private EditText mNameView;
    private View mProgressView;
    private View mLoginFormView;

    int flag = 0, b;
    String webPage, data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mEmailView.setError(null);
                    final String email = mEmailView.getText().toString();
                    if (TextUtils.isEmpty(email)) {
                        mEmailView.setError(getString(R.string.error_field_required));
                    } else if (!isEmailValid(email)) {
                        mEmailView.setError(getString(R.string.error_invalid_email2));
                    }
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                String urlParams = "email=" + URLEncoder.encode(email, "utf-8") + "&username=";
                                URL url = new URL("http://ziglrapp.com/api/register/check_user");
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setDoOutput(true);
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Content-Type",
                                        "application/x-www-form-urlencoded");

                                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                                dataOutputStream.writeBytes(urlParams);
                                dataOutputStream.flush();
                                dataOutputStream.close();
                                conn.connect();

                                InputStream is = conn.getInputStream();
                                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                                int b = 0;
                                String webPage="";
                                String data;
                                while ((data = buffer.readLine()) != null) {
                                    if (b == 0) {
                                        webPage += data;
                                    } else {
                                        webPage += "\n";
                                        webPage += data;
                                    }
                                    b++;
                                }
                                if(webPage.equals("Duplicate Email")) {
                                    mEmailView.post(new Runnable() {
                                        public void run() {
                                            mEmailView.setError(getString(R.string.error_email_exists));
                                        }
                                    });

                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();


                }
            }
        });

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mPasswordView.setError(null);
                    String password = mPasswordView.getText().toString();
                    if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
                        mPasswordView.setError(getString(R.string.error_invalid_password));
                    }
                    if (isConfirmPasswordValid(mPasswordView.getText().toString(), mCPasswordView.getText().toString())) {
                        mCPasswordView.setError(null);
                    }
                }
            }
        });

        mCPasswordView = (EditText) findViewById(R.id.cpassword);
        mCPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mCPasswordView.setError(null);
                    String cpassword = mCPasswordView.getText().toString();
                    if (!isConfirmPasswordValid(mPasswordView.getText().toString(), cpassword)) {
                        mCPasswordView.setError(getString(R.string.error_invalid_cpassword));
                    }

                }
            }
        });

        mNameView = (EditText) findViewById(R.id.name);
        mNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mNameView.setError(null);
                    String name = mNameView.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        mNameView.setError(getString(R.string.error_field_required));
                    } else if (!isNameValid(name)) {
                        mNameView.setError(getString(R.string.error_invalid_name));
                    }

                }
            }
        });

        mUsernameView = (EditText) findViewById(R.id.username);
        mUsernameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mUsernameView.setError(null);
                    final String username = mUsernameView.getText().toString();
                    if (TextUtils.isEmpty(username)) {
                        mUsernameView.setError(getString(R.string.error_field_required));
                    }
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                String urlParams = "email=&username=" + URLEncoder.encode(username, "utf-8");
                                URL url = new URL("http://ziglrapp.com/api/register/check_user");
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setDoOutput(true);
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Content-Type",
                                        "application/x-www-form-urlencoded");

                                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                                dataOutputStream.writeBytes(urlParams);
                                dataOutputStream.flush();
                                dataOutputStream.close();
                                conn.connect();

                                InputStream is = conn.getInputStream();
                                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                                int b = 0;
                                String webPage="";
                                String data;
                                while ((data = buffer.readLine()) != null) {
                                    if (b == 0) {
                                        webPage += data;
                                    } else {
                                        webPage += "\n";
                                        webPage += data;
                                    }
                                    b++;
                                }
                                if(webPage.equals("Duplicate Username")) {

                                    mUsernameView.post(new Runnable() {
                                        public void run() {
                                            mUsernameView.setError(getString(R.string.error_username_exists));
                                        }
                                    });

                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
            }
        });

        mAgeView = (EditText) findViewById(R.id.age);
        mAgeView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mAgeView.setError(null);
                    String age = mAgeView.getText().toString();
                    if (TextUtils.isEmpty(age)) {
                        mAgeView.setError(getString(R.string.error_field_required));
                    }
                }
            }
        });

        mGenderView = (Spinner) findViewById(R.id.spinner);
        mBloodView = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.custom_list_item_2);
        adapter.setDropDownViewResource(R.layout.custom_list_item_2);
        mGenderView.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.blood_group, R.layout.custom_list_item_2);
        adapter.setDropDownViewResource(R.layout.custom_list_item_2);
        mBloodView.setAdapter(adapter2);

        mMobileView = (EditText) findViewById(R.id.mobile);
        mMobileView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.signup || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mMobileView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mMobileView.setError(null);
                    String mobile = mMobileView.getText().toString();
                    if (TextUtils.isEmpty(mobile)) {
                        mMobileView.setError(getString(R.string.error_field_required));
                    } else if (!isMobileValid(mobile)) {
                        mMobileView.setError(getString(R.string.error_invalid_mobile));
                    }
                }
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_up);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    //Autocomplete email attempt

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
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
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
                populateAutoComplete();
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

        //get selected textView from spinner
        mSpinnerTextView = (TextView) mGenderView.getSelectedView();
        mSpinner2TextView = (TextView) mBloodView.getSelectedView();

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mCPasswordView.setError(null);
        mNameView.setError(null);
        mAgeView.setError(null);
        mUsernameView.setError(null);
        mMobileView.setError(null);
        mSpinnerTextView.setError(null);
        mSpinner2TextView.setError(null);


        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String cpassword = mCPasswordView.getText().toString();
        String mobile = mMobileView.getText().toString();
        final String username = mUsernameView.getText().toString();
        String name = mNameView.getText().toString();
        String age = mAgeView.getText().toString();
        String gender = mGenderView.getSelectedItem().toString();
        String blood_group = mBloodView.getSelectedItem().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email2));
            focusView = mEmailView;
            cancel = true;
        }

        //check for a valid Name
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        } else if (!isNameValid(name)) {
            mNameView.setError(getString(R.string.error_invalid_name));
            focusView = mNameView;
            cancel = true;
        }

        //check for correct password confirmation

        if (!isConfirmPasswordValid(password, cpassword)) {
            mCPasswordView.setError(getString(R.string.error_invalid_cpassword));
            focusView = mCPasswordView;
            cancel = true;
        }

        //check for empty username or mobile

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }


        //check for a valid age

        if (TextUtils.isEmpty(age)) {
            mAgeView.setError(getString(R.string.error_field_required));
            focusView = mAgeView;
            cancel = true;
        }

        //check for empty gender

        if (gender.equals("Select Gender")) {
            mSpinnerTextView.setTextColor(Color.RED);
            mSpinnerTextView.setError("");
            mSpinnerTextView.setText(getString(R.string.error_field_required));
            focusView = mSpinnerTextView;
            cancel = true;
        }
        if (blood_group.equals("Select Blood Group")) {
            mSpinner2TextView.setTextColor(Color.RED);
            mSpinner2TextView.setError("");
            mSpinner2TextView.setText(getString(R.string.error_field_required));
            focusView = mSpinner2TextView;
            cancel = true;
        }
        if (TextUtils.isEmpty(mobile)) {
            mMobileView.setError(getString(R.string.error_field_required));
            focusView = mMobileView;
            cancel = true;
        } else if (!isMobileValid(mobile)) {
            mMobileView.setError(getString(R.string.error_invalid_mobile));
            focusView = mMobileView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String urlParams = "email=" + URLEncoder.encode(email, "utf-8") + "&username=" + URLEncoder.encode(username, "utf-8");
                        URL url = new URL("http://ziglrapp.com/api/register/check_user");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded");

                        DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                        dataOutputStream.writeBytes(urlParams);
                        dataOutputStream.flush();
                        dataOutputStream.close();
                        conn.connect();

                        InputStream is = conn.getInputStream();
                        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                        int b = 0;
                        String webPage="";
                        String data;
                        while ((data = buffer.readLine()) != null) {
                            if (b == 0) {
                                webPage += data;
                            } else {
                                webPage += "\n";
                                webPage += data;
                            }
                            b++;
                        }
                        if(webPage.equals("Duplicate Username")) {

                            mUsernameView.post(new Runnable() {
                                public void run() {
                                    mUsernameView.setError(getString(R.string.error_username_exists));
                                    return;
                                }
                            });}
                        if(webPage.equals("Duplicate Email")) {
                            mEmailView.post(new Runnable() {
                                public void run() {
                                    mEmailView.setError(getString(R.string.error_email_exists));
                                    return;
                                }
                            });

                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, username, mobile, name, age, gender, blood_group);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isNameValid(String name) {
        return name.length() > 2;
    }

    /*private boolean isAgeValid(String age) {
        return Integer.parseInt(age) >= 1 && Integer.parseInt(age) <= 150;
    }*/

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private boolean isConfirmPasswordValid(String password, String cpassword) {
        return cpassword.equals(password);
    }

    private boolean isMobileValid(String mobile) {
        return mobile.length() == 10;
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

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(Register.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mAgeView.setText(dayOfMonth+"-"+new DateFormatSymbols().getMonths()[monthOfYear]+"-"+year);
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
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mUsername;
        private final String mMobile;
        private final String mName;
        private final String mAge;
        private final String mGender;
        private final String mBloodGroup;

        UserLoginTask(String email, String password, String username, String mobile, String name, String age, String gender, String blood_group) {
            mEmail = email;
            mPassword = password;
            mUsername = username;
            mMobile = mobile;
            mName = name;
            mAge = age;
            mGender = gender;
            mBloodGroup = blood_group;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                String urlParams = "name=" + URLEncoder.encode(mName, "utf-8") + "&username=" + URLEncoder.encode(mUsername, "utf-8") + "&email=" + URLEncoder.encode(mEmail, "utf-8") + "&age=" + URLEncoder.encode(mAge, "utf-8") + "&gender=" + URLEncoder.encode(mGender, "utf-8") + "&password=" + URLEncoder.encode(mPassword, "utf-8") + "&mobile=" + URLEncoder.encode(mMobile, "utf-8");
                URL url = new URL("http://ziglrapp.com/api/register");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
                dataOutputStream.writeBytes(urlParams);
                dataOutputStream.flush();
                dataOutputStream.close();
                conn.connect();

                InputStream is = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                b = 0;
                webPage = "";
                while ((data = buffer.readLine()) != null) {
                    if (b == 0) {
                        webPage += data;
                    } else {
                        webPage += "\n";
                        webPage += data;
                    }
                    b++;
                }


            } catch (IOException e) {
                Log.d("Ram", "463");
                return false;
            }

            if (webPage.equals("Success"))
                return true;
            else
                return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                Toast.makeText(Register.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                Intent i = new Intent(Register.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
            else
                Toast.makeText(Register.this,webPage,Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main2, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void pick(View v){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                Register.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "DOB");
    }
}

