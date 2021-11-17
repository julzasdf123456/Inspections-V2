package com.lopez.julz.inspectionv2.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lopez.julz.inspectionv2.R;
import com.lopez.julz.inspectionv2.database.LocalServiceConnections;

import java.util.List;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder> {

    public List<LocalServiceConnections> localServiceConnectionsList;
    public Context context;

    public UploadAdapter(List<LocalServiceConnections> localServiceConnectionsList, Context context) {
        this.localServiceConnectionsList = localServiceConnectionsList;
        this.context = context;
    }

    @NonNull
    @Override
    public UploadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.upload_recyclerview_layout, parent, false);

        UploadAdapter.ViewHolder viewHolder = new UploadAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UploadAdapter.ViewHolder holder, int position) {
        LocalServiceConnections localServiceConnections = localServiceConnectionsList.get(position);

        holder.download_recyclerview_name.setText(localServiceConnections.getServiceAccountName());
        holder.download_recyclerview_id.setText(localServiceConnections.getId());
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
}
