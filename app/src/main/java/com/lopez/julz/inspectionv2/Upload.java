package com.lopez.julz.inspectionv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.lopez.julz.inspectionv2.api.RequestPlaceHolder;
import com.lopez.julz.inspectionv2.api.RetrofitBuilder;
import com.lopez.julz.inspectionv2.api.ServiceConnections;
import com.lopez.julz.inspectionv2.classes.Login;
import com.lopez.julz.inspectionv2.classes.ServiceConnectionsAdapter;
import com.lopez.julz.inspectionv2.classes.UploadAdapter;
import com.lopez.julz.inspectionv2.database.AppDatabase;
import com.lopez.julz.inspectionv2.database.LocalServiceConnectionInspections;
import com.lopez.julz.inspectionv2.database.LocalServiceConnections;
import com.lopez.julz.inspectionv2.database.Photos;
import com.lopez.julz.inspectionv2.database.ServiceConnectionInspectionsDao;
import com.lopez.julz.inspectionv2.database.ServiceConnectionsDao;
import com.lopez.julz.inspectionv2.database.Settings;
import com.lopez.julz.inspectionv2.helpers.AlertHelpers;
import com.lopez.julz.inspectionv2.helpers.ObjectHelpers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Upload extends AppCompatActivity {

    public Toolbar upload_toolbar;
    public RecyclerView upload_recyclerview;
    public UploadAdapter uploadAdapter;
    public List<LocalServiceConnections> serviceConnectionsList;

    public AppDatabase db;

    public List<LocalServiceConnectionInspections> localServiceConnectionInspectionsList;
    public TextView total_upload, upload_progress_text;

    public FloatingActionButton upload_button;
    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    public LinearLayout upload_area;
    public LinearProgressIndicator upload_progress_bar;

    public Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        upload_toolbar = (Toolbar) findViewById(R.id.upload_toolbar);
        upload_recyclerview = (RecyclerView) findViewById(R.id.upload_recyclerview);
        total_upload = (TextView) findViewById(R.id.total_upload);
        upload_button = (FloatingActionButton) findViewById(R.id.upload_button);
        upload_area = (LinearLayout) findViewById(R.id.upload_area);
        upload_progress_text = (TextView) findViewById(R.id.upload_progress_text);
        upload_progress_bar = (LinearProgressIndicator) findViewById(R.id.upload_progress_bar);

        upload_area.setVisibility(View.GONE);

        localServiceConnectionInspectionsList = new ArrayList<>();
        serviceConnectionsList = new ArrayList<>();
        uploadAdapter = new UploadAdapter(serviceConnectionsList, this);
        upload_recyclerview.setAdapter(uploadAdapter);
        upload_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(upload_toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchSettings().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class FetchUnunploadedInspections extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();
            localServiceConnectionInspectionsList = serviceConnectionInspectionsDao.getAllByStatus("Approved");

            ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
            for (int i=0; i<localServiceConnectionInspectionsList.size(); i++) {
                LocalServiceConnections localServiceConnections = serviceConnectionsDao.getOne(localServiceConnectionInspectionsList.get(i).getServiceConnectionId());
                serviceConnectionsList.add(localServiceConnections);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            total_upload.setText("Total Size: " + localServiceConnectionInspectionsList.size());
            uploadAdapter.notifyDataSetChanged();
        }
    }

    class UploadData extends AsyncTask<Void, Integer, Void> {

        int size;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            upload_area.setVisibility(View.VISIBLE);

            upload_progress_text.setText("Uploading...");

            size = localServiceConnectionInspectionsList.size();
            upload_progress_bar.setMax(size);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            upload_progress_bar.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i=0; i<size; i++) {
                Log.e("UPLOADING", "Uploading ID " + localServiceConnectionInspectionsList.get(i).getId());


                Call<LocalServiceConnectionInspections> call = requestPlaceHolder.updateServiceConnections(localServiceConnectionInspectionsList.get(i));

                int finalI = i;
                call.enqueue(new Callback<LocalServiceConnectionInspections>() {
                    @Override
                    public void onResponse(Call<LocalServiceConnectionInspections> call, Response<LocalServiceConnectionInspections> response) {
                        if (!response.isSuccessful()) {
                            Log.e("ERR_UPLOAD", response.message());
//                            Toast.makeText(Upload.this, "Error uploading data! " + response.message(), Toast.LENGTH_LONG).show();
                        } else {
                            setProgress(finalI+1);
                            new UpdateUploadedData().execute(localServiceConnectionInspectionsList.get(finalI).getServiceConnectionId());
                            Log.e("UPLOADED", response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<LocalServiceConnectionInspections> call, Throwable t) {

                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            upload_progress_text.setText("Upload Complete");

            AlertDialog.Builder builder
                    = new AlertDialog
                    .Builder(Upload.this);

            builder.setTitle("Success");

            builder.setMessage("Upload successful!");

            builder.setCancelable(false);

            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,int which) {
                    dialog.cancel();
                    finish();
                }
            });

            AlertDialog alertDialog = builder.create();

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onShow(DialogInterface dialog) {
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.black);
                }
            });

            alertDialog.show();
        }
    }

    class UpdateUploadedData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
            ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();

            LocalServiceConnections localServiceConnections = serviceConnectionsDao.getOne(strings[0]);
            LocalServiceConnectionInspections localServiceConnectionInspections = serviceConnectionInspectionsDao.getOneBySvcId(strings[0]);

            localServiceConnections.setStatus("TRASH");
            localServiceConnectionInspections.setStatus("TRASH");

            serviceConnectionsDao.updateServiceConnections(localServiceConnections);
            serviceConnectionInspectionsDao.updateServiceConnectionInspections(localServiceConnectionInspections);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }

    class UploadImages extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (serviceConnectionsList != null) {
                    for (int i=0; i<serviceConnectionsList.size(); i++) {
                        List<Photos> photosList = db.photosDao().getAllPhotos(serviceConnectionsList.get(i).getId());

                        for (int j=0; j<photosList.size(); j++) {
                            File imgFile = new File(photosList.get(j).getPath());
                            if (imgFile.exists()) {
                                uploadFile(imgFile, serviceConnectionsList.get(i).getId());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ERR_GET_IMGS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }

    public void uploadFile(File file, String serviceConnectionId) {
        try {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            Call<ResponseBody> uploadCall = requestPlaceHolder.saveUploadedImages(serviceConnectionId, fileBody);

            uploadCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            Log.e("UPLD_IMG_OK", "Image uploaded : " + serviceConnectionId + " - " + file.getName());
                        } else {
                            Log.e("ERR_UPLOD", response.message());
                        }
                    } else {
                        Log.e("UPLD_IMG_ERR", response.message());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("UPLD_IMG_ERR", t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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

                localServiceConnectionInspectionsList.clear();
                serviceConnectionsList.clear();
                uploadAdapter.notifyDataSetChanged();
                new FetchUnunploadedInspections().execute();

                upload_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            new UploadData().execute();
                            new UploadImages().execute();
                        } catch (Exception e){
                            Log.e("ERR_UPLOAD", e.getMessage());
                        }
                    }
                });
            } else {
                startActivity(new Intent(Upload.this, SettingsActivity.class));
            }
        }
    }
}