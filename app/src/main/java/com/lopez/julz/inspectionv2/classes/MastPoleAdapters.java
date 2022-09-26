package com.lopez.julz.inspectionv2.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lopez.julz.inspectionv2.FormEdit;
import com.lopez.julz.inspectionv2.R;
import com.lopez.julz.inspectionv2.database.AppDatabase;
import com.lopez.julz.inspectionv2.database.MastPoles;

import java.util.List;

public class MastPoleAdapters extends RecyclerView.Adapter<MastPoleAdapters.ViewHolder> {

    public List<MastPoles> mastPolesList;
    public Context context;
    public AppDatabase db;

    public MastPoleAdapters(List<MastPoles> mastPolesList, Context context, AppDatabase db) {
        this.mastPolesList = mastPolesList;
        this.context = context;
        this.db = db;
    }

    @NonNull
    @Override
    public MastPoleAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.mastpoles_recyclerview_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MastPoleAdapters.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MastPoles mastPole = mastPolesList.get(position);

        holder.mastPoleDetails.setText(mastPole.getPoleRemarks() != null ? mastPole.getPoleRemarks() : "Pole");
        holder.deleteMastPole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteMastPole deleteMastPole = new DeleteMastPole(position);
                deleteMastPole.execute(mastPole);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mastPolesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mastPoleDetails;
        public ImageButton deleteMastPole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mastPoleDetails = itemView.findViewById(R.id.mastPoleDetails);
            deleteMastPole = itemView.findViewById(R.id.deleteMastPole);
        }
    }

    public class DeleteMastPole extends AsyncTask<MastPoles, Void, Void> {

        int pos;

        public DeleteMastPole(int position) {
            pos = position;
        }

        @Override
        protected Void doInBackground(MastPoles... mastPoles) {
            try {
                 if (mastPoles != null) {
                     db.mastPolesDao().deleteOne(mastPoles[0].getId());
                 }
            } catch (Exception e) {
                Log.e("ERR_DEL_MST_PL", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            mastPolesList.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, mastPolesList.size());
            ((FormEdit) context).refreshMap();
        }
    }
}
