package com.lopez.julz.inspectionv2.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lopez.julz.inspectionv2.ArchiveIndex;
import com.lopez.julz.inspectionv2.FormEdit;
import com.lopez.julz.inspectionv2.R;
import com.lopez.julz.inspectionv2.api.ServiceConnections;
import com.lopez.julz.inspectionv2.database.AppDatabase;
import com.lopez.julz.inspectionv2.database.LocalServiceConnectionInspections;
import com.lopez.julz.inspectionv2.database.LocalServiceConnections;
import com.lopez.julz.inspectionv2.database.ServiceConnectionInspectionsDao;
import com.lopez.julz.inspectionv2.database.ServiceConnectionsDao;
import com.lopez.julz.inspectionv2.helpers.ObjectHelpers;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ViewHolder> {

    public List<LocalServiceConnections> localServiceConnectionsList;
    public Context context;

    public ArchiveAdapter(List<LocalServiceConnections> localServiceConnectionsList, Context context) {
        this.localServiceConnectionsList = localServiceConnectionsList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ArchiveAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.archive_recyclerview_layout, parent, false);

        // Return a new holder instance
        ArchiveAdapter.ViewHolder viewHolder = new ArchiveAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ArchiveAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        LocalServiceConnections localServiceConnections = localServiceConnectionsList.get(position);

        holder.download_recyclerview_name.setText(localServiceConnections.getServiceAccountName());
        holder.download_recyclerview_id.setText(com.lopez.julz.inspectionv2.classes.ObjectHelpers.getAddress(localServiceConnections));

        if (localServiceConnections.getStatus() != null) {
            if (localServiceConnections.getStatus().equals("Approved")) {
                holder.download_recyclerview_name.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_round_check_circle_24), null, null, null);
                holder.download_recyclerview_name.setCompoundDrawablePadding(5);
            } else if (localServiceConnections.getStatus().equals("Re-Inspection")) {
                holder.download_recyclerview_name.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_baseline_error_24), null, null, null);
                holder.download_recyclerview_name.setCompoundDrawablePadding(5);
            } else {
                holder.download_recyclerview_name.setCompoundDrawables(null, null, null, null);
            }
        }

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(context);

                builder.setTitle("Confirmation");

                builder.setMessage("Are you sure you want to delete this data?");

                builder.setCancelable(false);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteArchive().execute(localServiceConnections.getId());

                        localServiceConnectionsList.remove(position);
                        notifyItemRemoved(position);
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
        });

        holder.archiveLayoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FormEdit.class);
                intent.putExtra("SVCID", localServiceConnections.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return localServiceConnectionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView download_recyclerview_name, download_recyclerview_id;
        public FloatingActionButton delete_btn;
        public CoordinatorLayout archiveLayoutParent;

        public ViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);

            download_recyclerview_name = (TextView) itemView.findViewById(R.id.download_recyclerview_name);
            download_recyclerview_id = (TextView) itemView.findViewById(R.id.download_recyclerview_id);
            delete_btn = (FloatingActionButton) itemView.findViewById(R.id.delete_btn);
            archiveLayoutParent = (CoordinatorLayout) itemView.findViewById(R.id.archiveLayoutParent);
        }
    }

    class DeleteArchive extends AsyncTask<String, Void, Void> {

        public AppDatabase db;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            db = Room.databaseBuilder(context,
                    AppDatabase.class, ObjectHelpers.databaseName()).fallbackToDestructiveMigration().build();

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                ServiceConnectionsDao serviceConnectionsDao = db.serviceConnectionsDao();
                ServiceConnectionInspectionsDao serviceConnectionInspectionsDao = db.serviceConnectionInspectionsDao();

                serviceConnectionsDao.delete(strings[0]);
                serviceConnectionInspectionsDao.deleteBySvcId(strings[0]);
            } catch (Exception e) {
                Log.e("ERR_DELETE_ARCHV", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }

}
