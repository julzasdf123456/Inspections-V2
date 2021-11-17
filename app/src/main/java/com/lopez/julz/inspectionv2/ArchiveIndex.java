package com.lopez.julz.inspectionv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

import com.lopez.julz.inspectionv2.api.ServiceConnections;
import com.lopez.julz.inspectionv2.classes.ArchiveAdapter;
import com.lopez.julz.inspectionv2.classes.ServiceConnectionsAdapter;
import com.lopez.julz.inspectionv2.database.AppDatabase;
import com.lopez.julz.inspectionv2.database.LocalServiceConnections;
import com.lopez.julz.inspectionv2.database.ServiceConnectionsDao;
import com.lopez.julz.inspectionv2.helpers.ObjectHelpers;

import java.util.ArrayList;
import java.util.List;

public class ArchiveIndex extends AppCompatActivity {

    public Toolbar archive_toolbar;

    public RecyclerView archive_recyclerview;
    public ArchiveAdapter archiveAdapter;
    public List<LocalServiceConnections> localServiceConnectionsList;

    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_index);

        archive_toolbar = (Toolbar) findViewById(R.id.archive_toolbar);
        setSupportActionBar(archive_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);

        archive_recyclerview = (RecyclerView) findViewById(R.id.archive_recyclerview);
        db = Room.databaseBuilder(this,
                AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        localServiceConnectionsList = new ArrayList<>();
        archiveAdapter = new ArchiveAdapter(localServiceConnectionsList, this);
        archive_recyclerview.setAdapter(archiveAdapter);
        archive_recyclerview.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class FetchArchive extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            localServiceConnectionsList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
            List<LocalServiceConnections> localServiceConnections = serviceConnectionsDao.getAll();

            for (int i=0; i<localServiceConnections.size(); i++) {
                localServiceConnectionsList.add(localServiceConnections.get(i));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            archiveAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchArchive().execute();
    }
}