package com.clinic.app.malak.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import androidx.core.content.ContextCompat;

import com.clinic.app.R;
import com.clinic.app.malak.models.Doctor;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private Context context;
    private List<Doctor> doctorList;
    private int selectedPosition = -1; // sélection unique
    private OnDoctorClickListener listener;

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    public DoctorAdapter(Context context, List<Doctor> doctorList, OnDoctorClickListener listener) {
        this.context = context;
        this.doctorList = doctorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);

        holder.txtName.setText(doctor.getName());
        holder.txtSpecialty.setText(doctor.getSpeciality());
        // Placeholder si image manquante
        holder.imgDoctor.setImageResource(doctor.getImage() != 0 ? doctor.getImage() : R.drawable.ic_baseline_person_24);

        // Changement de couleur selon sélection
        if (position == selectedPosition) {
            holder.cardDoctor.setCardBackgroundColor(ContextCompat.getColor(context, R.color.selected_card_color));
        } else {
            holder.cardDoctor.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        }

        holder.cardDoctor.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            int previousSelected = selectedPosition;
            selectedPosition = currentPosition;

            if (previousSelected != -1) notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) listener.onDoctorClick(doctorList.get(selectedPosition));
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {

        CardView cardDoctor;
        ImageView imgDoctor;
        TextView txtName, txtSpecialty;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDoctor = itemView.findViewById(R.id.cardDoctor);
            imgDoctor = itemView.findViewById(R.id.imgDoctor);
            txtName = itemView.findViewById(R.id.txtName);
            txtSpecialty = itemView.findViewById(R.id.txtSpecialty);
        }
    }
}
