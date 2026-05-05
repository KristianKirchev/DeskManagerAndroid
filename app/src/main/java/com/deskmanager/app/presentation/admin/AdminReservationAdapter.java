package com.deskmanager.app.presentation.admin;

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
import com.deskmanager.app.data.entities.UserEntity;
import com.deskmanager.app.databinding.ItemReservationAdminBinding;
import com.deskmanager.app.domain.enums.ReservationStatus;
import com.deskmanager.app.utils.DateUtils;
import com.deskmanager.app.utils.DisplayUtils;

import java.util.Collections;
import java.util.List;

public class AdminReservationAdapter
        extends ListAdapter<ReservationEntity, AdminReservationAdapter.ViewHolder> {

    public interface OnCancelListener {
        void onCancel(ReservationEntity reservation);
    }

    private final OnCancelListener cancelListener;
    private List<UserEntity> users = Collections.emptyList();
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

    public AdminReservationAdapter(OnCancelListener cancelListener) {
        super(DIFF);
        this.cancelListener = cancelListener;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users != null ? users : Collections.emptyList();
        notifyItemRangeChanged(0, getItemCount());
    }

    public void setDesks(List<DeskEntity> desks) {
        this.desks = desks != null ? desks : Collections.emptyList();
        notifyItemRangeChanged(0, getItemCount());
    }

    private String resolveUsername(int userId) {
        for (UserEntity u : users) {
            if (u.getId() == userId) return u.getUsername();
        }
        return "#" + userId;
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
        ItemReservationAdminBinding binding = ItemReservationAdminBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemReservationAdminBinding binding;

        ViewHolder(ItemReservationAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ReservationEntity r) {
            binding.tvAdminResId.setText(itemView.getContext().getString(R.string.label_reservation_id, r.getId()));
            binding.tvAdminUser.setText(itemView.getContext().getString(
                    R.string.label_reservation_username, resolveUsername(r.getUserId())));
            binding.tvAdminDesk.setText(itemView.getContext().getString(
                    R.string.label_reservation_room, resolveDeskName(r.getDeskId())));
            binding.tvAdminDates.setText(itemView.getContext().getString(
                    R.string.label_date_range,
                    DateUtils.format(r.getStartDate()),
                    DateUtils.format(r.getEndDate())));

            ReservationStatus status = r.getStatus();
            binding.tvAdminStatus.setText(DisplayUtils.reservationStatus(itemView.getContext(), status));

            int color;
            switch (status) {
                case ACTIVE:    color = R.color.green_500; break;
                case CANCELLED: color = R.color.red_500;   break;
                default:        color = R.color.grey_500;  break;
            }
            binding.tvAdminStatus.setTextColor(itemView.getContext().getColor(color));

            boolean canCancel = status == ReservationStatus.ACTIVE;
            binding.btnAdminCancel.setVisibility(canCancel ? View.VISIBLE : View.GONE);
            binding.btnAdminCancel.setOnClickListener(v -> showCancelConfirmDialog(r));
        }

        private void showCancelConfirmDialog(ReservationEntity r) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(R.string.dialog_force_cancel_title)
                    .setMessage(itemView.getContext().getString(
                            R.string.dialog_force_cancel_message, r.getId(), r.getDeskId()))
                    .setPositiveButton(R.string.btn_force_cancel, (dialog, which) -> {
                        binding.btnAdminCancel.setVisibility(View.GONE);
                        if (cancelListener != null) cancelListener.onCancel(r);
                    })
                    .setNegativeButton(R.string.btn_cancel_dialog, null)
                    .show();
        }
    }
}
