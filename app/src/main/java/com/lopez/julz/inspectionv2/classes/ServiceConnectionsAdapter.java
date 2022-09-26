package com.lopez.julz.inspectionv2.classes;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lopez.julz.inspectionv2.R;
import com.lopez.julz.inspectionv2.api.ServiceConnections;
import com.lopez.julz.inspectionv2.database.LocalServiceConnections;

import java.util.List;

public class ServiceConnectionsAdapter extends RecyclerView.Adapter<ServiceConnectionsAdapter.ViewHolder> {

    public List<ServiceConnections> localServiceConnectionsList;
    public Context context;

    public ServiceConnectionsAdapter(List<ServiceConnections> localServiceConnectionsList, Context context) {
        this.localServiceConnectionsList = localServiceConnectionsList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView download_recyclerview_name, download_recyclerview_id;
        public FloatingActionButton delete_btn;

        public ViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);

            download_recyclerview_name = (TextView) itemView.findViewById(R.id.download_recyclerview_name);
            download_recyclerview_id = (TextView) itemView.findViewById(R.id.download_recyclerview_id);
            delete_btn = (FloatingActionButton) itemView.findViewById(R.id.delete_btn);
        }
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ServiceConnectionsAdapter.ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.download_recyclerview_layout, parent, false);

        // Return a new holder instance
        ServiceConnectionsAdapter.ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull ServiceConnectionsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ServiceConnections localServiceConnections = localServiceConnectionsList.get(position);

        holder.download_recyclerview_name.setText(localServiceConnections.getServiceAccountName());
        holder.download_recyclerview_id.setText(ObjectHelpers.getAddress(localServiceConnections));

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localServiceConnectionsList.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return localServiceConnectionsList.size();
    }
}
