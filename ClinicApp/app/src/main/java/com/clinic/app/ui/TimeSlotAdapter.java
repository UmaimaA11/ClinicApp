package com.clinic.app.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.clinic.app.R;
import com.clinic.app.data.entities.TimeSlot;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.SlotViewHolder> {

    private final List<TimeSlot> slots;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public TimeSlotAdapter(List<TimeSlot> slots) {
        this.slots = slots;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        TimeSlot slot = slots.get(position);

        // Formate l'affichage : "10:00 - 10:30"
        String timeText = timeFormat.format(slot.getStartTime()) + " - " + timeFormat.format(slot.getEndTime());
        holder.tvTime.setText(timeText);

        // État dynamique du RadioButton
        holder.rbSelect.setChecked(slot.isBooked());
        holder.rbSelect.setEnabled(!slot.isBooked()); // Désactivé si déjà réservé
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        RadioButton rbSelect;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTimeSlot);
            rbSelect = itemView.findViewById(R.id.rbSelectTime);
        }
    }
}