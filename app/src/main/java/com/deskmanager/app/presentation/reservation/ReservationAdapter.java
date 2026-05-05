package com.deskmanager.app.presentation.reservation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.deskmanager.app.R;
import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.data.entities.ReservationEntity;
import com.deskmanager.app.databinding.ItemReservationBinding;
import com.deskmanager.app.domain.enums.ReservationStatus;
import com.deskmanager.app.utils.DateUtils;
import com.deskmanager.app.utils.DisplayUtils;

import java.util.Collections;
import java.util.List;

public class ReservationAdapter extends ListAdapter<ReservationEntity, ReservationAdapter.ViewHolder> {

    public interface OnCancelClickListener {
        void onCancel(ReservationEntity reservation);
    }

    private final OnCancelClickListener cancelListener;
    private List<DeskEntity> desks = Collections.emptyList();

    private static final DiffUtil.ItemCallback<ReservationEntity> DIFF =
            new DiffUtil.ItemCallback<ReservationEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull ReservationEntity o, @NonNull ReservationEntity n) {
                    return o.getId() == n.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ReservationEntity o, @NonNull ReservationEntity n) {
                    return o.getStatus()    == n.getStatus()
                        && o.getStartDate() == n.getStartDate()
                        && o.getEndDate()   == n.getEndDate()
                        && o.getUserId()    == n.getUserId()
                        && o.getDeskId()    == n.getDeskId();
                }
            };

    public ReservationAdapter(OnCancelClickListener cancelListener) {
        super(DIFF);
        this.cancelListener = cancelListener;
    }

    public void setDesks(List<DeskEntity> desks) {
        this.desks = desks != null ? desks : Collections.emptyList();
        notifyItemRangeChanged(0, getItemCount());
    }

    private String resolveDeskName(int deskId) {
        for (DeskEntity d : desks) {
            if (d.getId() == deskId) return d.getRoomNumber();
        }
        return "#" + deskId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReservationBinding binding = ItemReservationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemReservationBinding binding;

        ViewHolder(ItemReservationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ReservationEntity reservation) {
            binding.tvDeskId.setText(itemView.getContext().getString(
                    R.string.label_reservation_room, resolveDeskName(reservation.getDeskId())));
            binding.tvDateRange.setText(itemView.getContext().getString(
                    R.string.label_date_range,
                    DateUtils.format(reservation.getStartDate()),
                    DateUtils.format(reservation.getEndDate())));

            ReservationStatus status = reservation.getStatus();
            binding.tvStatus.setText(DisplayUtils.reservationStatus(itemView.getContext(), status));

            int color;
            switch (status) {
                case ACTIVE:    color = R.color.green_500; break;
                case CANCELLED: color = R.color.red_500;   break;
                default:        color = R.color.grey_500;  break;
            }
            binding.tvStatus.setTextColor(itemView.getContext().getColor(color));

            boolean canCancel = status == ReservationStatus.ACTIVE;
            binding.btnCancel.setVisibility(canCancel ? View.VISIBLE : View.GONE);

            binding.btnCancel.setOnClickListener(v -> showConfirmDialog(reservation));
        }

        private void showConfirmDialog(ReservationEntity reservation) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(R.string.dialog_cancel_title)
                    .setMessage(itemView.getContext().getString(
                            R.string.dialog_cancel_message, reservation.getDeskId()))
                    .setPositiveButton(R.string.btn_cancel_reservation, (dialog, which) -> {
                        binding.btnCancel.setVisibility(View.GONE);
                        binding.tvStatus.setText(DisplayUtils.reservationStatus(
                                itemView.getContext(), ReservationStatus.CANCELLED));
                        binding.tvStatus.setTextColor(
                                itemView.getContext().getColor(R.color.red_500));

                        ReservationEntity copy = makeCancelledCopy(reservation);
                        if (cancelListener != null) cancelListener.onCancel(copy);
                    })
                    .setNegativeButton(R.string.btn_close, null)
                    .show();
        }

        private ReservationEntity makeCancelledCopy(ReservationEntity src) {
            ReservationEntity copy = new ReservationEntity();
            copy.setId(src.getId());
            copy.setUserId(src.getUserId());
            copy.setDeskId(src.getDeskId());
            copy.setStartDate(src.getStartDate());
            copy.setEndDate(src.getEndDate());
            copy.setStatus(ReservationStatus.CANCELLED);
            return copy;
        }
    }
}
