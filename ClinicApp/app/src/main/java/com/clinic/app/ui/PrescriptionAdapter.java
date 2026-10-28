package com.clinic.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.clinic.app.R;
import com.clinic.app.data.entities.User;
import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.ViewHolder> {

    private final List<User> patients;

    public PrescriptionAdapter(List<User> patients) {
        this.patients = patients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Vérifie que le nom du fichier est bien item_prescription.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prescription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User patient = patients.get(position);

        if (holder.tvName != null) holder.tvName.setText(patient.getName());
        if (holder.tvDetails != null) holder.tvDetails.setText("ID: #" + patient.getId());
        if (holder.tvDate != null) holder.tvDate.setText("Aujourd'hui");
    }

    @Override
    public int getItemCount() {
        return (patients != null) ? patients.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // VÉRIFIE BIEN CES IDs dans ton item_prescription.xml
            tvName = itemView.findViewById(R.id.tv_item_patient_name);
            tvDetails = itemView.findViewById(R.id.tv_item_details);
            tvDate = itemView.findViewById(R.id.tv_item_date);
        }
    }
}