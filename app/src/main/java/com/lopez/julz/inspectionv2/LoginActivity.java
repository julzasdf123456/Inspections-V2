package com.lopez.julz.inspectionv2;

import static com.lopez.julz.inspectionv2.helpers.ObjectHelpers.hasPermissions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.lopez.julz.inspectionv2.api.RequestPlaceHolder;
import com.lopez.julz.inspectionv2.api.RetrofitBuilder;
import com.lopez.julz.inspectionv2.api.ServiceConnections;
import com.lopez.julz.inspectionv2.classes.Login;
import com.lopez.julz.inspectionv2.database.AppDatabase;
import com.lopez.julz.inspectionv2.database.OfflineUsers;
import com.lopez.julz.inspectionv2.database.OfflineUsersDao;
import com.lopez.julz.inspectionv2.database.Settings;
import com.lopez.julz.inspectionv2.database.Users;
import com.lopez.julz.inspectionv2.database.UsersDao;
import com.lopez.julz.inspectionv2.helpers.ObjectHelpers;

import java.lang.reflect.Method;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public TextInputEditText username, password;
    public MaterialButton loginBtn;

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    public AppDatabase db;

    public Settings settings;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    public FloatingActionButton settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        username = (TextInputEditText) findViewById(R.id.username);
        password = (TextInputEditText) findViewById(R.id.password);
        loginBtn = (MaterialButton) findViewById(R.id.loginBtn);
        settingsBtn = findViewById(R.id.settingsBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                // CHECK MOBILE DATA
                boolean mobileDataEnabled = false;
                try {
                    Class cmClass = Class.forName(connManager.getClass().getName());
                    Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                    method.setAccessible(true); // Make the method callable
                    // get the setting for "mobile data"
                    mobileDataEnabled = (Boolean)method.invoke(connManager);
                } catch (Exception e) {
                    // Some problem accessible private API
                    // TODO do whatever error handling you want here
                }

                if (mWifi.isConnected()) {
                    // PERFORM ONLINE LOGIN USING WIFI
                    if (username.getText().equals("") | null == username.getText() | password.getText().equals("") | null == password.getText()) {
                        Snackbar.make(username, "Please fill in the fields to login", Snackbar.LENGTH_LONG).show();
                    } else {
                        login();
                    }
                } else {
                    if (mobileDataEnabled) {
                        // PERFORM ONLINE LOGIN USING MOBILE DATA
                        if (username.getText().equals("") | null == username.getText() | password.getText().equals("") | null == password.getText()) {
                            Snackbar.make(username, "Please fill in the fields to login", Snackbar.LENGTH_LONG).show();
                        } else {
                            login();
                        }
                    } else {
                        // PERFORM OFFLINE LOGIN
                        if (username.getText().equals("") | null == username.getText() | password.getText().equals("") | null == password.getText()) {
                            Snackbar.make(username, "Please fill in the fields to login", Snackbar.LENGTH_LONG).show();
                        } else {
                            new LoginOffline().execute(username.getText().toString(), password.getText().toString());
                        }
                    }

                }
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchSettings().execute();
    }

    private void login() {
        Login login = new Login(username.getText().toString(), password.getText().toString());

        Call<Login> call = requestPlaceHolder.login(login);

//        login_progressbar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (!response.isSuccessful()) {
//                    login_progressbar.setVisibility(View.INVISIBLE);
                    if (response.code() == 401) {
                        Snackbar.make(username, "The username and password you entered doesn't match our records. Kindly review and try again.", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(username,  "Failed to login. Try again later.", Snackbar.LENGTH_LONG).show();
                    }
                    Log.e("LOGIN_ERR", "Code: " + response.code() + "\nMessage: " + response.message());
                } else {
                    if (response.code() == 200) {
                        new SaveUser().execute(response.body().getId(), username.getText().toString(), password.getText().toString());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("USERID", response.body().getId());
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("LOGIN_FAILED", response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
//                login_progressbar.setVisibility(View.INVISIBLE);
//                AlertBuilders.infoDialog(LoginActivity.this, "Internal Server Error", "Failed to login. Try again later.");
                Log.e("ERR", t.getLocalizedMessage());
            }
        });
    }

    class SaveUser extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            OfflineUsersDao offlineUsersDao = db.offlineUsersDao();

            OfflineUsers offlineUsers = offlineUsersDao.getOne(strings[0]);

            if (offlineUsers == null) {
                offlineUsersDao.insertAll(new OfflineUsers(0, strings[0], strings[1], strings[2]));
                Log.e("SAVE_USR", "User saved");
            } else {
                offlineUsersDao.updateAll(offlineUsers);
                Log.e("SAVE_USR", "User updated");
            }

            return null;
        }
    }

    public class LoginOffline extends AsyncTask<String, Void, Void> {

        boolean doesUserExists = false;
        String userid = "";

        @Override
        protected Void doInBackground(String... strings) {
            UsersDao usersDao = db.usersDao();
            Users existing = usersDao.getOne(strings[0], strings[1]);

            if (existing == null) {
                doesUserExists = false;
            } else {
                doesUserExists = true;
                userid = existing.getId();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if (doesUserExists) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("USERID", userid);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "User not found on this device!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class FetchSettings extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                settings = db.settingsDao().getSettings();
            } catch (Exception e) {
                Log.e("ERR_FETCH_SETTINGS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (settings != null) {
                retrofitBuilder = new RetrofitBuilder(settings.getDefaultServer());
                requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);
            } else {
                startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
            }
        }
    }
}