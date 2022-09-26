package com.lopez.julz.inspectionv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lopez.julz.inspectionv2.api.RequestPlaceHolder;
import com.lopez.julz.inspectionv2.api.RetrofitBuilder;
import com.lopez.julz.inspectionv2.api.ServiceConnectionInspections;
import com.lopez.julz.inspectionv2.api.ServiceConnections;
import com.lopez.julz.inspectionv2.classes.ServiceConnectionsAdapter;
import com.lopez.julz.inspectionv2.database.AppDatabase;
import com.lopez.julz.inspectionv2.database.LocalServiceConnectionInspections;
import com.lopez.julz.inspectionv2.database.LocalServiceConnections;
import com.lopez.julz.inspectionv2.database.ServiceConnectionInspectionsDao;
import com.lopez.julz.inspectionv2.database.ServiceConnectionsDao;
import com.lopez.julz.inspectionv2.database.Settings;
import com.lopez.julz.inspectionv2.helpers.ObjectHelpers;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Download extends AppCompatActivity {

    public Toolbar download_toolbar;

    public RecyclerView download_recyclerview;
    public ServiceConnectionsAdapter serviceConnectionsAdapter;
    public List<ServiceConnections> serviceConnectionsList;
    public List<ServiceConnectionInspections> serviceConnectionInspectionsList;

    public RetrofitBuilder retrofitBuilder;
    private RequestPlaceHolder requestPlaceHolder;

    public FloatingActionButton download_button;

    public AppDatabase db;

    public String userid;

    public Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        download_toolbar = (Toolbar) findViewById(R.id.download_toolbar);
        download_recyclerview = (RecyclerView) findViewById(R.id.download_recyclerview);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        userid = getIntent().getExtras().getString("USERID");

        setSupportActionBar(download_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);

        serviceConnectionsList = new ArrayList<>();
        serviceConnectionInspectionsList = new ArrayList<>();
        serviceConnectionsAdapter = new ServiceConnectionsAdapter(serviceConnectionsList, this);
        download_recyclerview.setAdapter(serviceConnectionsAdapter);
        download_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        download_button = (FloatingActionButton) findViewById(R.id.download_button);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchSettings().execute();
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

                            }

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

    public void fetchTotalServiceConnectionInspections(String userId) {
        try {
            Call<List<ServiceConnectionInspections>> svcConnectionInspectionCall = requestPlaceHolder.getServiceConnectionInspections(userId);

            svcConnectionInspectionCall.enqueue(new Callback<List<ServiceConnectionInspections>>() {
                @Override
                public void onResponse(Call<List<ServiceConnectionInspections>> call, Response<List<ServiceConnectionInspections>> response) {
                    if (!response.isSuccessful()) {
                        Log.e("DWNLD_SVC_CON_INSP_ERR", response.message());
                    } else {
                        if (response.code() == 200) {
                            List<ServiceConnectionInspections> serviceConnectionInspections = response.body();

                            if (serviceConnectionInspections != null) {
                                new FetchDownloadableServiceConnectionInspections().execute(serviceConnectionInspections);
                            } else {

                            }

                        } else {
                            Log.e("DWNLD_SVC_CON_INSP_ERR", response.message());
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ServiceConnectionInspections>> call, Throwable t) {
                    Log.e("DWNLD_SVC_CON_ERR", t.getMessage());
                }
            });
        } catch (Exception e ){
            Log.e("DWNLD_SVC_CON_ERR", e.getMessage());
        }
    }

    class DownloadServiceConnections extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                for (int i=0; i<serviceConnectionsList.size(); i++) {
                    ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
                    LocalServiceConnections localServiceConnections = serviceConnectionsDao.getOne(serviceConnectionsList.get(i).getId());

                    if (localServiceConnections == null) {
                        LocalServiceConnections newLocalSC = new LocalServiceConnections(serviceConnectionsList.get(i).getId(),
                                serviceConnectionsList.get(i).getMemberConsumerId(),
                                serviceConnectionsList.get(i).getDateOfApplication(),
                                serviceConnectionsList.get(i).getServiceAccountName(),
                                serviceConnectionsList.get(i).getAccountCount(),
                                serviceConnectionsList.get(i).getSitio(),
                                serviceConnectionsList.get(i).getBarangay(),
                                serviceConnectionsList.get(i).getTown(),
                                serviceConnectionsList.get(i).getContactNumber(),
                                serviceConnectionsList.get(i).getEmailAddress(),
                                serviceConnectionsList.get(i).getAccountType(),
                                serviceConnectionsList.get(i).getAccountOrganization(),
                                serviceConnectionsList.get(i).getOrganizationAccountNumber(),
                                serviceConnectionsList.get(i).getIsNIHE(),
                                serviceConnectionsList.get(i).getAccountApplicationType(),
                                serviceConnectionsList.get(i).getConnectionApplicationType(),
                                serviceConnectionsList.get(i).getBuildingType(),
                                "FOR INSPECTION",
                                serviceConnectionsList.get(i).getNotes(),
                                serviceConnectionsList.get(i).getBarangayFull(),
                                serviceConnectionsList.get(i).getTownFull());
                        serviceConnectionsDao.insertAll(newLocalSC);
                    } else {
                        serviceConnectionsDao.updateServiceConnections(localServiceConnections);
                    }
                }

                return null;
            } catch (Exception e){
                Log.e("ERR", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            serviceConnectionsList.clear();
            serviceConnectionsAdapter.notifyDataSetChanged();

            Snackbar.make(download_recyclerview, "All data downloaded", Snackbar.LENGTH_LONG).show();
        }
    }

    class DownloadServiceConnectionInspections extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                for (int i=0; i<serviceConnectionInspectionsList.size(); i++) {
                    ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();
                    LocalServiceConnectionInspections localServiceConnectionInspections = serviceConnectionInspectionsDao.getOne(serviceConnectionInspectionsList.get(i).getId());

                    if (localServiceConnectionInspections == null) {
                        LocalServiceConnectionInspections newLocalSC = new LocalServiceConnectionInspections(serviceConnectionInspectionsList.get(i).getId(),
                                serviceConnectionInspectionsList.get(i).getServiceConnectionId(),
                                serviceConnectionInspectionsList.get(i).getSEMainCircuitBreakerAsPlan(),
                                serviceConnectionInspectionsList.get(i).getSEMainCircuitBreakerAsInstalled(),
                                serviceConnectionInspectionsList.get(i).getSENoOfBranchesAsPlan(),
                                serviceConnectionInspectionsList.get(i).getSENoOfBranchesAsInstalled(),
                                serviceConnectionInspectionsList.get(i).getPoleGIEstimatedDiameter(),
                                serviceConnectionInspectionsList.get(i).getPoleGIHeight(),
                                serviceConnectionInspectionsList.get(i).getPoleGINoOfLiftPoles(),
                                serviceConnectionInspectionsList.get(i).getPoleConcreteEstimatedDiameter(),
                                serviceConnectionInspectionsList.get(i).getPoleConcreteHeight(),
                                serviceConnectionInspectionsList.get(i).getPoleConcreteNoOfLiftPoles(),
                                serviceConnectionInspectionsList.get(i).getPoleHardwoodEstimatedDiameter(),
                                serviceConnectionInspectionsList.get(i).getPoleHardwoodHeight(),
                                serviceConnectionInspectionsList.get(i).getPoleHardwoodNoOfLiftPoles(),
                                serviceConnectionInspectionsList.get(i).getPoleRemarks(),
                                serviceConnectionInspectionsList.get(i).getSDWSizeAsPlan(),
                                serviceConnectionInspectionsList.get(i).getSDWSizeAsInstalled(),
                                serviceConnectionInspectionsList.get(i).getSDWLengthAsPlan(),
                                serviceConnectionInspectionsList.get(i).getSDWLengthAsInstalled(),
                                serviceConnectionInspectionsList.get(i).getGeoBuilding(),
                                serviceConnectionInspectionsList.get(i).getGeoTappingPole(),
                                serviceConnectionInspectionsList.get(i).getGeoMeteringPole(),
                                serviceConnectionInspectionsList.get(i).getGeoSEPole(),
                                serviceConnectionInspectionsList.get(i).getFirstNeighborName(),
                                serviceConnectionInspectionsList.get(i).getFirstNeighborMeterSerial(),
                                serviceConnectionInspectionsList.get(i).getSecondNeighborName(),
                                serviceConnectionInspectionsList.get(i).getSecondNeighborMeterSerial(),
                                serviceConnectionInspectionsList.get(i).getEngineerInchargeName(),
                                serviceConnectionInspectionsList.get(i).getEngineerInchargeTitle(),
                                serviceConnectionInspectionsList.get(i).getEngineerInchargeLicenseNo(),
                                serviceConnectionInspectionsList.get(i).getEngineerInchargeLicenseValidity(),
                                serviceConnectionInspectionsList.get(i).getEngineerInchargeContactNo(),
                                serviceConnectionInspectionsList.get(i).getStatus(),
                                serviceConnectionInspectionsList.get(i).getInspector(),
                                serviceConnectionInspectionsList.get(i).getDateOfVerification(),
                                serviceConnectionInspectionsList.get(i).getEstimatedDateForReinspection(),
                                serviceConnectionInspectionsList.get(i).getNotes());
                        serviceConnectionInspectionsDao.insertAll(newLocalSC);
                    } else {
                        serviceConnectionInspectionsDao.updateServiceConnectionInspections(localServiceConnectionInspections);
                    }
                }

                return null;
            } catch (Exception e){
                Log.e("ERR", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("INSPCT_DWNLD_STATUS", "Downloaded " + serviceConnectionInspectionsList.size() + " inspections data");
        }
    }

    class FetchDownloadableServiceConnections extends AsyncTask<List<ServiceConnections>, Void, Void> {

        @Override
        protected Void doInBackground(List<ServiceConnections>... lists) {
            for (int i=0; i<lists[0].size(); i++) {
                ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
                LocalServiceConnections localServiceConnections = serviceConnectionsDao.getOne(lists[0].get(i).getId());

                if (localServiceConnections == null) {
                    serviceConnectionsList.add(lists[0].get(i));
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            serviceConnectionsAdapter.notifyDataSetChanged();
        }
    }

    class FetchDownloadableServiceConnectionInspections extends AsyncTask<List<ServiceConnectionInspections>, Void, Void> {

        @Override
        protected Void doInBackground(List<ServiceConnectionInspections>... lists) {
            for (int i=0; i<lists[0].size(); i++) {
                ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();
                LocalServiceConnectionInspections localServiceConnectionInspections = serviceConnectionInspectionsDao.getOne(lists[0].get(i).getId());

                if (localServiceConnectionInspections == null) {
                    serviceConnectionInspectionsList.add(lists[0].get(i));
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.e("INSPCT_STATUS", "Logged " + serviceConnectionInspectionsList.size() + " inspections to be downloaded");
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

                fetchTotalServiceConnections(userid);
                fetchTotalServiceConnectionInspections(userid);

                download_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DownloadServiceConnections().execute();
                        new DownloadServiceConnectionInspections().execute();
                    }
                });
            } else {
                startActivity(new Intent(Download.this, SettingsActivity.class));
            }
        }
    }
}