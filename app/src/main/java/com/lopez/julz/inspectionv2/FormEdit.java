package com.lopez.julz.inspectionv2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lopez.julz.inspectionv2.classes.MastPoleAdapters;
import com.lopez.julz.inspectionv2.database.AppDatabase;
import com.lopez.julz.inspectionv2.database.LocalServiceConnectionInspections;
import com.lopez.julz.inspectionv2.database.LocalServiceConnections;
import com.lopez.julz.inspectionv2.database.MastPoles;
import com.lopez.julz.inspectionv2.database.PayTransactions;
import com.lopez.julz.inspectionv2.database.PayTransactionsDao;
import com.lopez.julz.inspectionv2.database.Photos;
import com.lopez.julz.inspectionv2.database.ServiceConnectionInspectionsDao;
import com.lopez.julz.inspectionv2.database.ServiceConnectionsDao;
import com.lopez.julz.inspectionv2.database.TotalPayments;
import com.lopez.julz.inspectionv2.helpers.AlertHelpers;
import com.lopez.julz.inspectionv2.helpers.ObjectHelpers;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FormEdit extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {

    public Toolbar form_toolbar;

    public String svcId;

    public AppDatabase db;

    public MaterialButton minimize;
    public LinearLayout form_hidable_svc_details;
    public MaterialCardView form_svc_details;
    public TextView form_name, form_address, form_svc_id, form_contact, form_title, svcDropLength;

    public EditText formBreakerRatingPlanned, formBreakerRatingInstalled, formBreakerBranchesPlanned, formBreakerBranchesInstalled;
    public TextView formSdwSizePlanned, formSdwSizeInstalled, formSdwLengthPlanned, formSdwLengthInstalled;

    public EditText billDeposit, looping, mop, lengthSubTotal;

    public EditText formLiftPolesDiameterGI, formLiftPolesDiameterConcrete, formLiftPolesDiameterHardWood;
    public EditText formLiftPolesHeightGI, formLiftPolesHeightConcrete, formLiftPolesHeightHardWood;
    public EditText formLiftPolesQuantityGI, formLiftPolesQuantityConcrete, formLiftPolesQuantityHardWood, formLiftPolesRemarks;

    public LocalServiceConnections serviceConnections;
    public LocalServiceConnectionInspections serviceConnectionInspections;

    public EditText formGeoTappingPole, formGeoMeteringPole, formSEPole, formGeoBuilding;
    public ImageButton formGeoTappingPoleBtn, formGeoMeteringPoleBtn, formGeoSEPoleBtn, formGeoBuildingBtn;

    public EditText formReverificationRemarks;
    public RadioGroup formRecommendation;

    public FloatingActionButton saveBtn, cameraBtn;

    // MAP
    public MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private LocationComponent locationComponent;
    public Style style;

    static final int REQUEST_PICTURE_CAPTURE = 1;
    public FlexboxLayout imageFields;
    public String currentPhotoPath;

    // MAST POLES
    public MaterialButton addMastPole;
    public List<MastPoles> mastPoles;
    public RecyclerView mastPolesRecyclerview;
    public MastPoleAdapters mastPoleAdapters;
    public String LINE_SOURCE_ID = "linesource";
    public String LINE_LAYER_ID = "linelayer";
    public Double distance = 0.0, geoPointsDistance = 0.0;
    public boolean firstInit = true;

    // DEPOSITS
    public PayTransactions payTransactions;
    public TotalPayments totalPayments;

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
        form_title = findViewById(R.id.form_title);

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

        // mast poles
        addMastPole = findViewById(R.id.addMastPole);
        svcDropLength = findViewById(R.id.svcDropLength);
        mastPoles = new ArrayList<>();
        mastPolesRecyclerview = findViewById(R.id.mastPolesRecyclerview);
        mastPoleAdapters = new MastPoleAdapters(mastPoles, this, db);
        mastPolesRecyclerview.setAdapter(mastPoleAdapters);
        mastPolesRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // added
        billDeposit = findViewById(R.id.billDeposit);
        looping = findViewById(R.id.looping);
        mop = findViewById(R.id.mop);
        lengthSubTotal = findViewById(R.id.lengthSubTotal);

        cameraBtn = findViewById(R.id.cameraBtn);
        imageFields = findViewById(R.id.imageFields);

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
                fetchCoordinates(formGeoTappingPole, "tw-provincial-2");
                new FetchMastPoles().execute(svcId);
            }
        });

        formGeoMeteringPoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCoordinates(formGeoMeteringPole, "hu-main-2");
                new FetchMastPoles().execute(svcId);
            }
        });

        formGeoSEPoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCoordinates(formSEPole, "tw-provincial-expy-2");
                new FetchMastPoles().execute(svcId);
            }
        });

        formGeoBuildingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCoordinates(formGeoBuilding, "in-national-3");
                new FetchMastPoles().execute(svcId);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        lengthSubTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    looping.setText(ObjectHelpers.roundTwoNoComma(getLooping()));
                    formSdwLengthInstalled.setText(ObjectHelpers.roundTwoNoComma(getTotalServiceDrop()));
                } catch (Exception e) {
                    Log.e("ERR_UPD_SVC_DROP", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addMastPole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FormEdit.this);

                    EditText mastPoleRemarks = new EditText(FormEdit.this);
                    mastPoleRemarks.setHint("Pole Name or Pole Description");
                    mastPoleRemarks.setText("Mast Pole # " + (mastPoles.size()+1));

                    builder.setTitle("Add Mast Pole Details")
                            .setView(mastPoleRemarks)
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AddMastPole().execute(mastPoleRemarks.getText().toString(), svcId);
                                    firstInit = false;
                                }
                            });

                    AlertDialog dialog = builder.create();

                    dialog.show();
                } catch (Exception e) {
                    Log.e("ERR_ADD_MST_PL", e.getMessage());
                }
            }
        });

        new GetPhotos().execute();
    }

    public void setStyle(Style style) {
        this.style = style;
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
            mapboxMap.setStyle(new Style.Builder()
                    .fromUri("mapbox://styles/julzlopez/ckavvidmh61bt1iqp2n1ilgec"), new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    setStyle(style);

                    enableLocationComponent(style);

                    new FetchMastPoles().execute(svcId);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new  File(currentPhotoPath);
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath());
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/8, bitmap.getHeight()/8, true);

            ImageView imageView = new ImageView(FormEdit.this);
            Constraints.LayoutParams layoutParams = new Constraints.LayoutParams(scaledBmp.getWidth(), scaledBmp.getHeight());
            imageView.setLayoutParams(layoutParams);
            imageView.setPadding(0, 5, 5, 0);
            if (imgFile.exists()) {
                imageView.setImageBitmap(scaledBmp);
            }
            imageFields.addView(imageView);

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popup = new PopupMenu(FormEdit.this,imageView);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.image_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_img:
                                    if (imgFile.exists()) {
                                        imgFile.delete();
                                        new GetPhotos().execute();
                                    }
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                    return false;
                }
            });
        }
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

    public void fetchCoordinates(EditText destination, String iconImage) {
        if (locationComponent == null) {
            Toast.makeText(FormEdit.this, "Location service unavailable. Switch to manual mode.", Toast.LENGTH_LONG).show();
        } else {
            if (null != locationComponent.getLastKnownLocation()) {
                destination.setText(locationComponent.getLastKnownLocation().getLatitude() + "," + locationComponent.getLastKnownLocation().getLongitude());
                try {
                    addMarkers(style, new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude()), iconImage);
                } catch (Exception e) {
                    Log.e("ERR_PLOT_MARKER", e.getMessage());
                    Toast.makeText(FormEdit.this, "Unable to plot marker\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    class FetchServiceConnectionData extends AsyncTask<String, Void, Void> {

        private String barangay, town;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
                serviceConnections = serviceConnectionsDao.getOne(strings[0]);

                barangay = db.barangaysDao().getOne(serviceConnections.getBarangay()).getBarangay();
                town = db.townsDao().getOne(serviceConnections.getTown()).getTown();

                // DEPOSITS
                totalPayments = db.totalPaymentsDao().getOneByServiceConnectionId(strings[0]);
                payTransactions = db.payTransactionsDao().getOneByServiceConnectionId(strings[0]);
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
                    form_address.setText(serviceConnections.getSitio() + ", " + barangay + ", " + town);
                    form_contact.setText(serviceConnections.getContactNumber());
                    form_title.setText("Form (" + serviceConnections.getConnectionApplicationType() + ")");
                }

                if (payTransactions != null) {
                    billDeposit.setText(payTransactions.getAmount());
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

    class FetchMastPoles extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mastPoles.clear();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (strings != null) {
                    mastPoles.addAll(db.mastPolesDao().getAllMastPoles(strings[0]));
                }
            } catch (Exception e) {
                Log.e("ERR_GET_MST_PLS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            mastPoleAdapters.notifyDataSetChanged();

            List<Point> latLngList = new ArrayList<>();

            // DISPLAY POINTS
            if (serviceConnectionInspections != null) {
                if (formGeoMeteringPole.getText() != null) {
                    String splitted[] = formGeoMeteringPole.getText().toString().split(",");
                    if (splitted.length > 1) {
                        String lat = formGeoMeteringPole.getText().toString().split(",")[0];
                        String longi = formGeoMeteringPole.getText().toString().split(",")[1];
                        addMarkers(style, new LatLng(Double.valueOf(lat), Double.valueOf(longi)), "hu-main-2");
                        latLngList.add(Point.fromLngLat(Double.valueOf(longi), Double.valueOf(lat)));
                    }
                }

                if (formGeoTappingPole.getText() != null) {
                    String splitted[] = formGeoTappingPole.getText().toString().split(",");
                    if (splitted.length > 1) {
                        String lat = formGeoTappingPole.getText().toString().split(",")[0];
                        String longi = formGeoTappingPole.getText().toString().split(",")[1];
                        addMarkers(style, new LatLng(Double.valueOf(lat), Double.valueOf(longi)), "tw-provincial-2");
                        latLngList.add(Point.fromLngLat(Double.valueOf(longi), Double.valueOf(lat)));
                    }
                }

                if (formSEPole.getText() != null) {
                    String splitted[] = formSEPole.getText().toString().split(",");
                    if (splitted.length > 1) {
                        String lat = formSEPole.getText().toString().split(",")[0];
                        String longi = formSEPole.getText().toString().split(",")[1];
                        addMarkers(style, new LatLng(Double.valueOf(lat), Double.valueOf(longi)), "tw-provincial-expy-2");
                        latLngList.add(Point.fromLngLat(Double.valueOf(longi), Double.valueOf(lat)));
                    }
                }

                if (formGeoBuilding.getText() != null) {
                    String splitted[] = formGeoBuilding.getText().toString().split(",");
                    if (splitted.length > 1) {
                        String lat = formGeoBuilding.getText().toString().split(",")[0];
                        String longi = formGeoBuilding.getText().toString().split(",")[1];
                        addMarkers(style, new LatLng(Double.valueOf(lat), Double.valueOf(longi)), "in-national-3");
                        latLngList.add(Point.fromLngLat(Double.valueOf(longi), Double.valueOf(lat)));
                    }
                }
            }

            // ADD LINE TO MAP
            try {
                List<Point> routes = new ArrayList<>();
                distance = 0.0;

                /**
                 *
                 * SERVICE CONNECTION LONG LATS
                 */
                LatLng prevPoint;
                LatLng presPoint;
                for (int x=0; x<latLngList.size(); x++) {
                    routes.add(latLngList.get(x));

                    Log.e("TEST_SC", Double.valueOf(latLngList.get(x).longitude()) + "");
                }

                /**
                 * MAST POLES
                 */
                for (int i=0; i<mastPoles.size(); i++) {
                    routes.add(Point.fromLngLat(Double.valueOf(mastPoles.get(i).getLongitude()), Double.valueOf(mastPoles.get(i).getLatitude())));

                    addMarkers(style, new LatLng(Double.valueOf(mastPoles.get(i).getLatitude()), Double.valueOf(mastPoles.get(i).getLongitude())), "za-provincial-2");
                }

                Log.e("TST", routes.size() + "");

                /**
                 * GET DISTANCES
                 */
                for(int j=0; j<routes.size(); j++) {
                    if (routes.size() > 0) {
                        if (j > 0) {
                            prevPoint = new LatLng(Double.valueOf(routes.get(j-1).latitude()), Double.valueOf(routes.get(j-1).longitude()));
                            presPoint = new LatLng(Double.valueOf(routes.get(j).latitude()), Double.valueOf(routes.get(j).longitude()));

                            distance += presPoint.distanceTo(prevPoint);
                        }
                    } else {
                        distance = 0.0;
                    }
                }

                svcDropLength.setText("Len: " + ObjectHelpers.roundTwo(distance)  + " mtrs");

                if (!firstInit) {
                    lengthSubTotal.setText(ObjectHelpers.roundTwoNoComma(distance));

                    looping.setText(ObjectHelpers.roundTwoNoComma(getLooping()));
                    formSdwLengthInstalled.setText(ObjectHelpers.roundTwo(getTotalServiceDrop()));
                }

            } catch (Exception e) {
                Log.e("ERR_ADD_LN_MST_PL", e.getMessage());
            }
        }
    }

    public double getTotalServiceDrop() {
        try {
            double subTotal = lengthSubTotal.getText() != null ? Double.valueOf(lengthSubTotal.getText().toString()) : 0;
            double mOp = mop.getText() != null ? Double.valueOf(mop.getText().toString()) : 0;
            double loop = (subTotal + mOp) * .05;
            return  subTotal + mOp + loop;
        } catch (Exception e) {
            Log.e("ERR_GET_SVC_TTL", e.getMessage());
            return 0;
        }
    }

    public double getLooping() {
        try {
            double subTotal = lengthSubTotal.getText() != null ? Double.valueOf(lengthSubTotal.getText().toString()) : 0;
            double mOp = mop.getText() != null ? Double.valueOf(mop.getText().toString()) : 0;
            return (subTotal + mOp) * .05;
        } catch (Exception e) {
            Log.e("ERR_GET_SVC_TTL", e.getMessage());
            return 0;
        }
    }

    class AddMastPole extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (strings != null) {
                    MastPoles mastPoles = new MastPoles();
                    mastPoles.setId(ObjectHelpers.generateIDandRandString());
                    mastPoles.setPoleRemarks(strings[0]);
                    mastPoles.setServiceConnectionId(strings[1]);

                    if (locationComponent != null) {
                        mastPoles.setLatitude(locationComponent.getLastKnownLocation() != null ? (locationComponent.getLastKnownLocation().getLatitude() + "") : "");
                        mastPoles.setLongitude(locationComponent.getLastKnownLocation() != null ? (locationComponent.getLastKnownLocation().getLongitude() + "") : "");
                    }

                    mastPoles.setDateTimeTaken(ObjectHelpers.getDateTime());
                    mastPoles.setIsUploaded("No");

                    db.mastPolesDao().insertAll(mastPoles);
                }
            } catch (Exception e) {
                Log.e("ERR_ADD_MST_POLE", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Toast.makeText(FormEdit.this, "Mast Pole Added", Toast.LENGTH_SHORT).show();
            new FetchMastPoles().execute(svcId);
        }
    }

    public void addMarkers(Style style, LatLng latLng, String iconImage) {
        try {
            // ADD MARKERS
            SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);

            symbolManager.setIconAllowOverlap(true);
            symbolManager.setTextAllowOverlap(true);

            if (latLng != null) {
                SymbolOptions symbolOptions = new SymbolOptions()
                        .withLatLng(latLng)
                        .withIconImage(iconImage)
                        .withIconSize(.3f);

                Symbol symbol = symbolManager.create(symbolOptions);

                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(14)
                        .build());
            }
        } catch (Exception e) {
            Log.e("ERR_PLOT_MRKERS", e.getMessage());
        }
    }

    public void refreshMap() {
        new FetchMastPoles().execute(svcId);
        firstInit = false;
    }

    class UpdateInspectionData extends AsyncTask<String, Void, Void> {

        Object billDepFill;

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
            try {
                ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();
                serviceConnectionInspectionsDao.updateServiceConnectionInspections(serviceConnectionInspections);

                ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
                serviceConnectionsDao.updateServiceConnections(serviceConnections);

                // ADD BILL DEPOSIT
                billDepFill = billDeposit.getText();
                if (billDepFill != null) {
                    if (payTransactions != null) {
                        payTransactions.setAmount(billDepFill.toString());
                        payTransactions.setTotal(billDepFill.toString());

                        db.payTransactionsDao().updateAll(payTransactions);
                    } else {
                        payTransactions = new PayTransactions();
                        payTransactions.setId(ObjectHelpers.generateIDandRandString());
                        payTransactions.setAmount(billDepFill.toString());
                        payTransactions.setTotal(billDepFill.toString());
                        payTransactions.setParticular(com.lopez.julz.inspectionv2.classes.ObjectHelpers.getBillDepositId());
                        payTransactions.setServiceConnectionId(serviceConnections.getId());

                        db.payTransactionsDao().insertAll(payTransactions);
                    }
                }
            } catch (Exception e) {
                Log.e("ERR_SV_INSP", e.getMessage());
            }

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

    /**
     * TAKE PHOTOS
     */
    private void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            File pictureFile = null;
            try {
                pictureFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.lopez.julz.inspectionv2",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "CRM_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        new SavePhotoToDatabase().execute(svcId, currentPhotoPath);
        return image;
    }

    public class SavePhotoToDatabase extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (strings != null) {
                    String scId = strings[0];
                    String photo = strings[1];

                    Photos photoObject = new Photos(photo, scId);
                    db.photosDao().insertAll(photoObject);
                }
            } catch (Exception e) {
                Log.e("ERR_SAVE_PHOTO_DB", e.getMessage());
            }

            return null;
        }
    }

    public class GetPhotos extends AsyncTask<Void, Void, Void> {

        List<Photos> photosList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageFields.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                photosList.addAll(db.photosDao().getAllPhotos(svcId));
            } catch (Exception e) {
                Log.e("ERR_GET_IMGS", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if (photosList != null) {
                for(int i=0; i<photosList.size(); i++) {
                    File file = new File(photosList.get(i).getPath());
                    if (file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                        Bitmap scaledBmp = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/8, bitmap.getHeight()/8, true);
                        ImageView imageView = new ImageView(FormEdit.this);
                        Constraints.LayoutParams layoutParams = new Constraints.LayoutParams(scaledBmp.getWidth(), scaledBmp.getHeight());
                        imageView.setLayoutParams(layoutParams);
                        imageView.setPadding(0, 5, 5, 0);
                        imageView.setImageBitmap(scaledBmp);
                        imageFields.addView(imageView);

                        imageView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                PopupMenu popup = new PopupMenu(FormEdit.this,imageView);
                                //inflating menu from xml resource
                                popup.inflate(R.menu.image_menu);
                                //adding click listener
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.delete_img:
                                                file.delete();
                                                new GetPhotos().execute();
                                                return true;
                                            default:
                                                return false;
                                        }
                                    }
                                });
                                //displaying the popup
                                popup.show();
                                return false;
                            }
                        });
                    } else {
                        Log.e("ERR_RETRV_FILE", "Error retriveing file");
                    }
                }
            }
        }
    }
}