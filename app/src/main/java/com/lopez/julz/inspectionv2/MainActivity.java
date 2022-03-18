package com.lopez.julz.inspectionv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.lopez.julz.inspectionv2.api.RequestPlaceHolder;
import com.lopez.julz.inspectionv2.api.RetrofitBuilder;
import com.lopez.julz.inspectionv2.api.ServiceConnections;
import com.lopez.julz.inspectionv2.database.AppDatabase;
import com.lopez.julz.inspectionv2.database.Barangays;
import com.lopez.julz.inspectionv2.database.LocalServiceConnectionInspections;
import com.lopez.julz.inspectionv2.database.LocalServiceConnections;
import com.lopez.julz.inspectionv2.database.ServiceConnectionInspectionsDao;
import com.lopez.julz.inspectionv2.database.ServiceConnectionsDao;
import com.lopez.julz.inspectionv2.database.Towns;
import com.lopez.julz.inspectionv2.helpers.ObjectHelpers;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    public TextView dashboard_download_total, dashboard_archive, dashboard_ununploaded;
    public CircularProgressIndicator dashboard_download_progress;
    public MaterialButton dashboard_refresh_downloadables;

    public MaterialCardView dashboard_unuploaded_card, dashboard_download_card, dashboard_archive_card;

    public AppDatabase db;

    public String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofitBuilder = new RetrofitBuilder();
        requestPlaceHolder = retrofitBuilder.getRetrofit().create(RequestPlaceHolder.class);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        userid = getIntent().getExtras().getString("USERID");

        dashboard_download_total = (TextView) findViewById(R.id.dashboard_download_total);
        dashboard_download_progress = (CircularProgressIndicator) findViewById(R.id.dashboard_download_progress);
        dashboard_archive = (TextView) findViewById(R.id.dashboard_archive);
        dashboard_refresh_downloadables = (MaterialButton) findViewById(R.id.dashboard_refresh_downloadables);
        dashboard_ununploaded = (TextView) findViewById(R.id.dashboard_ununploaded);
        dashboard_unuploaded_card  = findViewById(R.id.dashboard_unuploaded_card);
        dashboard_download_card = findViewById(R.id.dashboard_download_card);
        dashboard_archive_card = findViewById(R.id.dashboard_archive_card);

        downloadBarangays();
        downloadTowns();

        fetchTotalServiceConnections(userid);
        new FetchTotalArchive().execute();
        new FetchUnunploadedInspections().execute();

        dashboard_download_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Download.class);
                intent.putExtra("USERID", userid);
                startActivity(intent);
            }
        });

        dashboard_archive_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ArchiveIndex.class));
            }
        });

        dashboard_refresh_downloadables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchTotalServiceConnections(userid);
            }
        });

        dashboard_unuploaded_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Upload.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchTotalServiceConnections(userid);
        new FetchTotalArchive().execute();
        new FetchUnunploadedInspections().execute();
    }

    public void fetchTotalServiceConnections(String userId) {
        try {
            Call<List<ServiceConnections>> svcConnectionCall = requestPlaceHolder.getServiceConnections(userId);

            svcConnectionCall.enqueue(new Callback<List<ServiceConnections>>() {
                @Override
                public void onResponse(Call<List<ServiceConnections>> call, Response<List<ServiceConnections>> response) {
                    if (!response.isSuccessful()) {
                        Log.e("DWNLD_SVC_CON_ERR", response.message());
                    } else {
                        if (response.code() == 200) {
                            List<ServiceConnections> serviceConnections = response.body();

                            if (serviceConnections != null) {
                                new FetchDownloadableServiceConnections().execute(serviceConnections);
                            } else {
                                dashboard_download_total.setText("0");
                            }
                            dashboard_download_progress.setVisibility(View.GONE);
                        } else {
                            Log.e("DWNLD_SVC_CON_ERR", response.message());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ServiceConnections>> call, Throwable t) {
                    Log.e("DWNLD_SVC_CON_ERR", t.getMessage());
                }
            });
        } catch (Exception e ){
            Log.e("DWNLD_SVC_CON_ERR", e.getMessage());
        }
    }

    class FetchTotalArchive extends AsyncTask<Void, Void, Void> {

        public int total = 0;

        @Override
        protected Void doInBackground(Void... voids) {
            ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
            List<LocalServiceConnections> localServiceConnections = serviceConnectionsDao.getAll();

            total = localServiceConnections.size();

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            dashboard_archive.setText(total + "");
        }
    }

    class FetchDownloadableServiceConnections extends AsyncTask<List<ServiceConnections>, Void, Void> {

        public int counter = 0;

        @Override
        protected Void doInBackground(List<ServiceConnections>... lists) {
            for (int i=0; i<lists[0].size(); i++) {
                ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
                LocalServiceConnections localServiceConnections = serviceConnectionsDao.getOne(lists[0].get(i).getId());

                if (localServiceConnections == null) {
                    counter += 1;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (counter == 0) {
//                dashboard_view_downloadables.setVisibility(View.GONE);
            } else {
//                dashboard_view_downloadables.setVisibility(View.VISIBLE);
            }
            dashboard_download_total.setText(counter + "");
        }
    }

    class FetchUnunploadedInspections extends AsyncTask<Void, Void, Void> {

        List<LocalServiceConnectionInspections> localServiceConnectionInspections;

        @Override
        protected Void doInBackground(Void... voids) {
            ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();
            localServiceConnectionInspections = serviceConnectionInspectionsDao.getAllByStatus("Approved");
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (localServiceConnectionInspections.size() == 0) {
            } else {
            }
            dashboard_ununploaded.setText(localServiceConnectionInspections.size() + "");
        }
    }

    /**
     * DOWNLOAD ASSETS
     */
    public void downloadBarangays() {
        try {
            Call<List<Barangays>> brgyCall = requestPlaceHolder.getBarangays();

            brgyCall.enqueue(new Callback<List<Barangays>>() {
                @Override
                public void onResponse(Call<List<Barangays>> call, Response<List<Barangays>> response) {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            new SaveBarangays().execute(response.body());
                        }
                    } else {
                        Log.e("ERR_GET_BRGYS", response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Barangays>> call, Throwable t) {
                    Log.e("ERR_GET_BRGYS", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ERR_GET_BRGYS", e.getMessage());
        }
    }

    public class SaveBarangays extends AsyncTask<List<Barangays>, Void, Void> {

        @Override
        protected Void doInBackground(List<Barangays>... lists) {
            try {
                if (lists != null) {
                    List<Barangays> barangaysList = lists[0];

                    int size = barangaysList.size();

                    for (int i=0; i<size; i++) {
                        Barangays brgy = db.barangaysDao().getOne(barangaysList.get(i).getId());

                        if (brgy == null) {
                            db.barangaysDao().insertAll(barangaysList.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("BRGY_DONE_DL", "Barangays downloaded");
        }
    }

    public void downloadTowns() {
        try {
            Call<List<Towns>> townCall = requestPlaceHolder.getTowns();

            townCall.enqueue(new Callback<List<Towns>>() {
                @Override
                public void onResponse(Call<List<Towns>> call, Response<List<Towns>> response) {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            new SaveTowns().execute(response.body());
                        }
                    } else {
                        Log.e("ERR_DL_TWNS", response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Towns>> call, Throwable t) {
                    Log.e("ERR_DL_TWNS", t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ERR_DL_TWNS", e.getMessage());
        }
    }

    public class SaveTowns extends AsyncTask<List<Towns>, Void, Void> {

        @Override
        protected Void doInBackground(List<Towns>... lists) {
            try {
                if (lists != null) {
                    List<Towns> townsList = lists[0];

                    int size = townsList.size();

                    for(int i=0; i<size; i++) {
                        Towns town = db.townsDao().getOne(townsList.get(i).getId());

                        if (town == null) {
                            db.townsDao().insertAll(townsList.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ERR_SV_TWNS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("TOWNS_DL_DONE", "Towns downloaded");
        }
    }
}