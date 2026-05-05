package com.deskmanager.app.presentation.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.deskmanager.app.R;
import com.deskmanager.app.data.entities.UserEntity;
import com.deskmanager.app.databinding.ItemUserAdminBinding;
import com.deskmanager.app.domain.enums.UserRole;
import com.deskmanager.app.utils.DisplayUtils;

public class AdminUserAdapter
        extends ListAdapter<UserEntity, AdminUserAdapter.ViewHolder> {

    public interface OnRoleChangeListener {
        void onRoleChange(UserEntity user, UserRole newRole);
    }

    public interface OnDeleteListener {
        void onDelete(UserEntity user);
    }

    public interface OnResetPasswordListener {
        void onResetPassword(UserEntity user);
    }

    private final OnRoleChangeListener roleChangeListener;
    private final OnDeleteListener deleteListener;
    private final OnResetPasswordListener resetPasswordListener;
    private final int currentUserId;

    private static final DiffUtil.ItemCallback<UserEntity> DIFF =
            new DiffUtil.ItemCallback<UserEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull UserEntity o, @NonNull UserEntity n) {
                    return o.getId() == n.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull UserEntity o, @NonNull UserEntity n) {
                    return o.getRole() == n.getRole()
                            && o.getName().equals(n.getName())
                            && o.getUsername().equals(n.getUsername());
                }
            };

    public AdminUserAdapter(int currentUserId, OnRoleChangeListener roleChangeListener,
                            OnDeleteListener deleteListener,
                            OnResetPasswordListener resetPasswordListener) {
        super(DIFF);
        this.currentUserId = currentUserId;
        this.roleChangeListener = roleChangeListener;
        this.deleteListener = deleteListener;
        this.resetPasswordListener = resetPasswordListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserAdminBinding binding = ItemUserAdminBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserAdminBinding binding;

        ViewHolder(ItemUserAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(UserEntity user) {
            binding.tvUserName.setText(user.getName());
            binding.tvUserEmail.setText(user.getUsername());
            binding.tvUserRole.setText(DisplayUtils.userRole(itemView.getContext(), user.getRole()));

            boolean isSelf = user.getId() == currentUserId;

            itemView.setOnClickListener(v -> {
                if (isSelf) return;
                showRoleOptionsDialog(user);
            });

            binding.btnDeleteUser.setVisibility(isSelf ? android.view.View.GONE : android.view.View.VISIBLE);
            binding.btnDeleteUser.setOnClickListener(v -> showDeleteConfirmDialog(user));

            binding.btnResetPassword.setVisibility(isSelf ? android.view.View.GONE : android.view.View.VISIBLE);
            binding.btnResetPassword.setOnClickListener(v -> showResetPasswordConfirmDialog(user));
        }

        private void showRoleOptionsDialog(UserEntity user) {
            boolean isAdmin = user.getRole() == UserRole.ADMIN;
            String actionLabel = isAdmin
                    ? itemView.getContext().getString(R.string.btn_make_user)
                    : itemView.getContext().getString(R.string.btn_make_admin);
            UserRole newRole = isAdmin ? UserRole.USER : UserRole.ADMIN;

            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(user.getName())
                    .setMessage(itemView.getContext().getString(
                            R.string.dialog_change_role_message,
                            user.getName(),
                            DisplayUtils.userRole(itemView.getContext(), user.getRole()),
                            DisplayUtils.userRole(itemView.getContext(), newRole)))
                    .setPositiveButton(actionLabel, (dialog, which) ->
                            showConfirmRoleDialog(user, newRole))
                    .setNegativeButton(R.string.btn_cancel_dialog, null)
                    .show();
        }

        private void showConfirmRoleDialog(UserEntity user, UserRole newRole) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(R.string.dialog_confirm_role_title)
                    .setMessage(itemView.getContext().getString(
                            R.string.dialog_confirm_role_message,
                            user.getName(),
                            DisplayUtils.userRole(itemView.getContext(), newRole)))
                    .setPositiveButton(R.string.btn_confirm, (dialog, which) -> {
                        if (roleChangeListener != null) roleChangeListener.onRoleChange(user, newRole);
                    })
                    .setNegativeButton(R.string.btn_cancel_dialog, null)
                    .show();
        }

        private void showDeleteConfirmDialog(UserEntity user) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(R.string.dialog_delete_user_title)
                    .setMessage(itemView.getContext().getString(
                            R.string.dialog_delete_user_message, user.getName(), user.getUsername()))
                    .setPositiveButton(R.string.btn_delete_user, (dialog, which) -> {
                        if (deleteListener != null) deleteListener.onDelete(user);
                    })
                    .setNegativeButton(R.string.btn_cancel_dialog, null)
                    .show();
        }

        private void showResetPasswordConfirmDialog(UserEntity user) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle(R.string.dialog_reset_password_title)
                    .setMessage(itemView.getContext().getString(
                            R.string.dialog_reset_password_message, user.getName()))
                    .setPositiveButton(R.string.btn_reset_password, (dialog, which) -> {
                        if (resetPasswordListener != null) resetPasswordListener.onResetPassword(user);
                    })
                    .setNegativeButton(R.string.btn_cancel_dialog, null)
                    .show();
        }
    }
}
