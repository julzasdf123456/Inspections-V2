package com.lopez.julz.inspectionv2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lopez.julz.inspectionv2.database.AppDatabase;
import com.lopez.julz.inspectionv2.database.LocalServiceConnectionInspections;
import com.lopez.julz.inspectionv2.database.LocalServiceConnections;
import com.lopez.julz.inspectionv2.database.ServiceConnectionInspectionsDao;
import com.lopez.julz.inspectionv2.database.ServiceConnectionsDao;
import com.lopez.julz.inspectionv2.helpers.AlertHelpers;
import com.lopez.julz.inspectionv2.helpers.ObjectHelpers;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

public class FormEdit extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {

    public Toolbar form_toolbar;

    public String svcId;

    public AppDatabase db;

    public MaterialButton minimize;
    public LinearLayout form_hidable_svc_details;
    public MaterialCardView form_svc_details;
    public TextView form_name, form_address, form_svc_id, form_contact;

    public EditText formBreakerRatingPlanned, formBreakerRatingInstalled, formBreakerBranchesPlanned, formBreakerBranchesInstalled;
    public TextView formSdwSizePlanned, formSdwSizeInstalled, formSdwLengthPlanned, formSdwLengthInstalled;

    public EditText formLiftPolesDiameterGI, formLiftPolesDiameterConcrete, formLiftPolesDiameterHardWood;
    public EditText formLiftPolesHeightGI, formLiftPolesHeightConcrete, formLiftPolesHeightHardWood;
    public EditText formLiftPolesQuantityGI, formLiftPolesQuantityConcrete, formLiftPolesQuantityHardWood, formLiftPolesRemarks;

    public LocalServiceConnections serviceConnections;
    public LocalServiceConnectionInspections serviceConnectionInspections;

    public EditText formGeoTappingPole, formGeoMeteringPole, formSEPole, formGeoBuilding;
    public ImageButton formGeoTappingPoleBtn, formGeoMeteringPoleBtn, formGeoSEPoleBtn, formGeoBuildingBtn;

    public EditText formReverificationRemarks;
    public RadioGroup formRecommendation;

    public FloatingActionButton saveBtn;

    // MAP
    public MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_form_edit);

        form_toolbar = (Toolbar) findViewById(R.id.form_toolbar);
        svcId = getIntent().getExtras().getString("SVCID");

        setSupportActionBar(form_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);

        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        mapView = (MapView) findViewById(R.id.mapViewForm);

        // mapview
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        minimize = (MaterialButton) findViewById(R.id.minimize);
        form_hidable_svc_details = (LinearLayout) findViewById(R.id.form_hidable_svc_details);
        form_svc_details = (MaterialCardView) findViewById(R.id.form_svc_details);
        form_name = (TextView) findViewById(R.id.form_name);
        form_address = (TextView) findViewById(R.id.form_address);
        form_svc_id = (TextView) findViewById(R.id.form_svc_id);
        form_contact = (TextView) findViewById(R.id.form_contact);

        formBreakerRatingPlanned = (EditText) findViewById(R.id.formBreakerRatingPlanned);
        formBreakerRatingInstalled = (EditText) findViewById(R.id.formBreakerRatingInstalled);
        formBreakerBranchesPlanned = (EditText) findViewById(R.id.formBreakerBranchesPlanned);
        formBreakerBranchesInstalled = (EditText) findViewById(R.id.formBreakerBranchesInstalled);

        formSdwSizePlanned = (TextView) findViewById(R.id.formSdwSizePlanned);
        formSdwSizeInstalled = (TextView) findViewById(R.id.formSdwSizeInstalled);
        formSdwLengthPlanned = (TextView) findViewById(R.id.formSdwLengthPlanned);
        formSdwLengthInstalled = (TextView) findViewById(R.id.formSdwLengthInstalled);

        formGeoTappingPole = (EditText) findViewById(R.id.formGeoTappingPole);
        formGeoMeteringPole = (EditText) findViewById(R.id.formGeoMeteringPole);
        formSEPole = (EditText) findViewById(R.id.formSEPole);
        formGeoBuilding = (EditText) findViewById(R.id.formGeoBuilding);

        //SL Poles Inits
        formLiftPolesDiameterGI = (EditText) findViewById(R.id.formLiftPolesDiameterGI);
        formLiftPolesDiameterConcrete = (EditText) findViewById(R.id.formLiftPolesDiameterConcrete);
        formLiftPolesDiameterHardWood = (EditText) findViewById(R.id.formLiftPolesDiameterHardWood);
        formLiftPolesHeightGI = (EditText) findViewById(R.id.formLiftPolesHeightGI);
        formLiftPolesHeightConcrete = (EditText) findViewById(R.id.formLiftPolesHeightConcrete);
        formLiftPolesHeightHardWood = (EditText) findViewById(R.id.formLiftPolesHeightHardWood);
        formLiftPolesQuantityGI = (EditText) findViewById(R.id.formLiftPolesQuantityGI);
        formLiftPolesQuantityConcrete = (EditText) findViewById(R.id.formLiftPolesQuantityConcrete);
        formLiftPolesQuantityHardWood = (EditText) findViewById(R.id.formLiftPolesQuantityHardWood);
        formLiftPolesRemarks = (EditText) findViewById(R.id.formLiftPolesRemarks);

        formRecommendation = (RadioGroup) findViewById(R.id.formRecommendation);
        formReverificationRemarks = (EditText) findViewById(R.id.formReverificationRemarks);

        // BUTTONS FOR THE GEO LOCS
        formGeoTappingPoleBtn = (ImageButton) findViewById(R.id.formGeoTappingPoleBtn);
        formGeoBuildingBtn = (ImageButton) findViewById(R.id.formGeoBuildingBtn);
        formGeoMeteringPoleBtn = (ImageButton) findViewById(R.id.formGeoMeteringPoleBtn);
        formGeoSEPoleBtn = (ImageButton) findViewById(R.id.formGeoSEPoleBtn);

        saveBtn = (FloatingActionButton) findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateInspectionData().execute();
            }
        });

        new FetchServiceConnectionData().execute(svcId);
        new FetchServiceConnectionInspectionData().execute(svcId);

        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (form_hidable_svc_details.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(form_svc_details,
                            new AutoTransition());
                    form_hidable_svc_details.setVisibility(View.GONE);
                    minimize.setIcon(getDrawable(R.drawable.ic_round_add_24));
                } else {
                    TransitionManager.beginDelayedTransition(form_svc_details,
                            new AutoTransition());
                    form_hidable_svc_details.setVisibility(View.VISIBLE);
                    minimize.setIcon(getDrawable(R.drawable.ic_round_minimize_24));
                }
            }
        });

        formGeoTappingPoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCoordinates(formGeoTappingPole);
            }
        });

        formGeoMeteringPoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCoordinates(formGeoMeteringPole);
            }
        });

        formGeoSEPoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCoordinates(formSEPole);
            }
        });

        formGeoBuildingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCoordinates(formGeoBuilding);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        try {
            this.mapboxMap = mapboxMap;
            mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } catch (Exception e) {
            Log.e("ERR_INIT_MAPBOX", e.getMessage());
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        try {
            // Check if permissions are enabled and if not request
            if (PermissionsManager.areLocationPermissionsGranted(this)) {

                // Get an instance of the component
                locationComponent = mapboxMap.getLocationComponent();

                // Activate with options
                locationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

                // Enable to make component visible
                locationComponent.setLocationComponentEnabled(true);

                // Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.TRACKING);

                // Set the component's render mode
                locationComponent.setRenderMode(RenderMode.COMPASS);

            } else {
                permissionsManager = new PermissionsManager(this);
                permissionsManager.requestLocationPermissions(this);
            }
        } catch (Exception e) {
            Log.e("ERR_LOAD_MAP", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void fetchCoordinates(EditText destination) {
        if (locationComponent == null) {
            Toast.makeText(FormEdit.this, "Location service unavailable. Switch to manual mode.", Toast.LENGTH_LONG).show();
        } else {
            if (null != locationComponent.getLastKnownLocation()) {
                destination.setText(locationComponent.getLastKnownLocation().getLatitude() + "," + locationComponent.getLastKnownLocation().getLongitude());
            }
        }
    }

    class FetchServiceConnectionData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
                serviceConnections = serviceConnectionsDao.getOne(strings[0]);
            } catch (Exception e) {
                Log.e("ERR_GET_SVC_FORM", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            try {
                if (serviceConnections == null) {
                    AlertHelpers.infoDialog(FormEdit.this, "Not Found (404)", "Service connection data not found!");
                } else {
                    form_name.setText(serviceConnections.getServiceAccountName());
                    form_svc_id.setText(serviceConnections.getId());
                    form_address.setText(serviceConnections.getBarangay() + ", " + serviceConnections.getTown());
                    form_contact.setText(serviceConnections.getContactNumber());
                }
            } catch (Exception e) {
                Log.e("ERR_GET_SVC", e.getMessage());
            }
        }
    }

    class FetchServiceConnectionInspectionData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();
                serviceConnectionInspections = serviceConnectionInspectionsDao.getOneBySvcId(strings[0]);
            } catch (Exception e) {
                Log.e("ERR_GET_SVC_INS_FORM", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            try {
                if (serviceConnectionInspections == null) {
                    AlertHelpers.infoDialog(FormEdit.this, "Not Found (404)", "Service connection inspection data not found!");
                } else {
                    formBreakerRatingPlanned.setText(serviceConnectionInspections.getSEMainCircuitBreakerAsPlan());
                    formBreakerRatingInstalled.setText(serviceConnectionInspections.getSEMainCircuitBreakerAsInstalled());
                    formBreakerBranchesPlanned.setText(serviceConnectionInspections.getSENoOfBranchesAsPlan());
                    formBreakerBranchesInstalled.setText(serviceConnectionInspections.getSENoOfBranchesAsInstalled());

                    formSdwSizePlanned.setText(serviceConnectionInspections.getSDWSizeAsPlan());
                    formSdwSizeInstalled.setText(serviceConnectionInspections.getSDWSizeAsInstalled());
                    formSdwLengthPlanned.setText(serviceConnectionInspections.getSDWLengthAsPlan());
                    formSdwLengthInstalled.setText(serviceConnectionInspections.getSDWLengthAsInstalled());

                    formLiftPolesDiameterGI.setText(serviceConnectionInspections.getPoleGIEstimatedDiameter());
                    formLiftPolesDiameterConcrete.setText(serviceConnectionInspections.getPoleConcreteEstimatedDiameter());
                    formLiftPolesDiameterHardWood.setText(serviceConnectionInspections.getPoleHardwoodEstimatedDiameter());
                    formLiftPolesHeightGI.setText(serviceConnectionInspections.getPoleGIEstimatedDiameter());
                    formLiftPolesHeightConcrete.setText(serviceConnectionInspections.getPoleConcreteHeight());
                    formLiftPolesHeightHardWood.setText(serviceConnectionInspections.getPoleHardwoodHeight());
                    formLiftPolesQuantityGI.setText(serviceConnectionInspections.getPoleGINoOfLiftPoles());
                    formLiftPolesQuantityConcrete.setText(serviceConnectionInspections.getPoleConcreteNoOfLiftPoles());
                    formLiftPolesQuantityHardWood.setText(serviceConnectionInspections.getPoleHardwoodNoOfLiftPoles());
                    formLiftPolesRemarks.setText(serviceConnectionInspections.getPoleRemarks());

                    formGeoTappingPole.setText(serviceConnectionInspections.getGeoTappingPole());
                    formGeoMeteringPole.setText(serviceConnectionInspections.getGeoMeteringPole());
                    formSEPole.setText(serviceConnectionInspections.getGeoSEPole());
                    formGeoBuilding.setText(serviceConnectionInspections.getGeoBuilding());

                    Log.e("TEST", serviceConnectionInspections.getStatus());

                    if (null==serviceConnectionInspections.getStatus() | serviceConnectionInspections.getStatus().equals("") | serviceConnectionInspections.getStatus().equals("FOR INSPECTION")) {

                    } else {
                        formRecommendation.check(getRecommendationSelected(serviceConnectionInspections.getStatus()));
                    }

                    formReverificationRemarks.setText(serviceConnectionInspections.getNotes());
                }
            } catch (Exception e) {
                Log.e("ERR_GET_INSP", e.getMessage());
            }

        }
    }

    class UpdateInspectionData extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            serviceConnectionInspections.setSEMainCircuitBreakerAsInstalled(formBreakerRatingPlanned.getText().toString());
            serviceConnectionInspections.setSENoOfBranchesAsInstalled(formBreakerBranchesInstalled.getText().toString());
            serviceConnectionInspections.setSDWLengthAsInstalled(formSdwLengthInstalled.getText().toString());
            serviceConnectionInspections.setSDWSizeAsInstalled(formSdwSizeInstalled.getText().toString());

            serviceConnectionInspections.setPoleGIEstimatedDiameter(formLiftPolesDiameterGI.getText().toString());
            serviceConnectionInspections.setPoleGIHeight(formLiftPolesHeightGI.getText().toString());
            serviceConnectionInspections.setPoleGINoOfLiftPoles(formLiftPolesQuantityGI.getText().toString());

            serviceConnectionInspections.setPoleConcreteEstimatedDiameter(formLiftPolesDiameterConcrete.getText().toString());
            serviceConnectionInspections.setPoleConcreteHeight(formLiftPolesHeightConcrete.getText().toString());
            serviceConnectionInspections.setPoleConcreteNoOfLiftPoles(formLiftPolesQuantityConcrete.getText().toString());

            serviceConnectionInspections.setPoleHardwoodEstimatedDiameter(formLiftPolesDiameterHardWood.getText().toString());
            serviceConnectionInspections.setPoleHardwoodHeight(formLiftPolesHeightHardWood.getText().toString());
            serviceConnectionInspections.setPoleHardwoodNoOfLiftPoles(formLiftPolesQuantityHardWood.getText().toString());

            serviceConnectionInspections.setPoleRemarks(formLiftPolesRemarks.getText().toString());

            serviceConnectionInspections.setGeoTappingPole(formGeoTappingPole.getText().toString());
            serviceConnectionInspections.setGeoMeteringPole(formGeoMeteringPole.getText().toString());
            serviceConnectionInspections.setGeoSEPole(formSEPole.getText().toString());
            serviceConnectionInspections.setGeoBuilding(formGeoBuilding.getText().toString());

            serviceConnectionInspections.setStatus(ObjectHelpers.getSelectedTextFromRadioGroup(formRecommendation, getWindow().getDecorView()));
            serviceConnectionInspections.setNotes(formReverificationRemarks.getText().toString());
            serviceConnectionInspections.setDateOfVerification(ObjectHelpers.getDateTime());

            // UPDATE STATUS OF SERVICE CONNECTION
            serviceConnections.setStatus(ObjectHelpers.getSelectedTextFromRadioGroup(formRecommendation, getWindow().getDecorView()));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();
            serviceConnectionInspectionsDao.updateServiceConnectionInspections(serviceConnectionInspections);

            ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
            serviceConnectionsDao.updateServiceConnections(serviceConnections);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Toast.makeText(FormEdit.this, "Inspection data saved!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public int getRecommendationSelected(String recommendation) {
        if (recommendation != null) {
            if (recommendation.equals("Approved")) {
                return R.id.opsApproved;
            } else {
                return R.id.opsReInspection;
            }
        } else {
            return 0;
        }
    }
}