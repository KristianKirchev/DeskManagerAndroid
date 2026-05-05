package com.deskmanager.app.presentation.desk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.deskmanager.app.R;
import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.databinding.ItemDeskBinding;
import com.deskmanager.app.domain.enums.DeskStatus;
import com.deskmanager.app.utils.DisplayUtils;

public class DeskAdapter extends ListAdapter<DeskEntity, DeskAdapter.DeskViewHolder> {

    private final OnDeskClickListener clickListener;

    public interface OnDeskClickListener {
        void onDeskClick(DeskEntity desk);
    }

    private static final DiffUtil.ItemCallback<DeskEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<DeskEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull DeskEntity o, @NonNull DeskEntity n) {
                    return o.getId() == n.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull DeskEntity o, @NonNull DeskEntity n) {
                    return o.getRoomNumber().equals(n.getRoomNumber())
                            && o.getFloor() == n.getFloor()
                            && o.isOccupied() == n.isOccupied()
                            && o.getStatus() == n.getStatus();
                }
            };

    public DeskAdapter(OnDeskClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public DeskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeskBinding binding = ItemDeskBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DeskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeskViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class DeskViewHolder extends RecyclerView.ViewHolder {
        private final ItemDeskBinding binding;

        DeskViewHolder(ItemDeskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DeskEntity desk) {
            binding.tvRoomNumber.setText(itemView.getContext().getString(R.string.label_desk_room, desk.getRoomNumber()));
            binding.tvFloor.setText(itemView.getContext().getString(R.string.label_desk_floor, desk.getFloor()));

            boolean available = desk.getStatus() == DeskStatus.AVAILABLE;
            binding.tvStatus.setText(DisplayUtils.deskStatus(itemView.getContext(), desk.getStatus()));
            binding.tvStatus.setTextColor(
                    itemView.getContext().getColor(available ? R.color.green_500 : R.color.red_500));
            binding.statusIndicator.setBackgroundColor(
                    itemView.getContext().getColor(available ? R.color.green_500 : R.color.red_500));

            itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onDeskClick(desk);
            });
        }
    }
}
