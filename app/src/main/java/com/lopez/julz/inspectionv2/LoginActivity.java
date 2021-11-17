package com.lopez.julz.inspectionv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.lopez.julz.inspectionv2.api.RequestPlaceHolder;
import com.lopez.julz.inspectionv2.api.RetrofitBuilder;
import com.lopez.julz.inspectionv2.api.ServiceConnections;
import com.lopez.julz.inspectionv2.classes.Login;
import com.lopez.julz.inspectionv2.database.AppDatabase;
import com.lopez.julz.inspectionv2.database.OfflineUsers;
import com.lopez.julz.inspectionv2.database.OfflineUsersDao;
import com.lopez.julz.inspectionv2.helpers.ObjectHelpers;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        retrofitBuilder = new RetrofitBuilder();
        requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        username = (TextInputEditText) findViewById(R.id.username);
        password = (TextInputEditText) findViewById(R.id.password);
        loginBtn = (MaterialButton) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().equals("") | null == username.getText() | password.getText().equals("") | null == password.getText()) {
                    Snackbar.make(username, "Please fill in the fields to login", Snackbar.LENGTH_LONG).show();
                } else {
                    login();
                }
            }
        });
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
}