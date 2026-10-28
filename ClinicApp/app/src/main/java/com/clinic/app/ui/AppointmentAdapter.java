package com.clinic.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.clinic.app.R;
import com.clinic.app.data.entities.TimeSlot;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<TimeSlot> appointments;
    private OnAppointmentActionListener listener;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public interface OnAppointmentActionListener {
        void onCancel(TimeSlot slot);
        void onConfirm(TimeSlot slot);
    }

    public AppointmentAdapter(List<TimeSlot> appointments, OnAppointmentActionListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Assurez-vous que item_appointment.xml existe
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeSlot slot = appointments.get(position);

        String timeStr = timeFormat.format(slot.getStartTime()) + " - " + timeFormat.format(slot.getEndTime());
        holder.tvTime.setText(timeStr);

        // Change le texte ou la couleur si le créneau est déjà réservé
        if (slot.isBooked()) {
            holder.btnConfirm.setText("Déjà Confirmé");
            holder.btnConfirm.setEnabled(false);
        } else {
            holder.btnConfirm.setText("Confirmer");
            holder.btnConfirm.setEnabled(true);
        }

        holder.btnCancel.setOnClickListener(v -> listener.onCancel(slot));
        holder.btnConfirm.setOnClickListener(v -> listener.onConfirm(slot));
    }

    @Override
    public int getItemCount() { return appointments.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        Button btnCancel, btnConfirm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_appointment_time);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
        }
    }
}