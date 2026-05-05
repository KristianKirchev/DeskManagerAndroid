package com.deskmanager.app.presentation.admin;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deskmanager.app.R;
import com.deskmanager.app.data.entities.UserEntity;
import com.deskmanager.app.data.repository.UserRepository;
import com.deskmanager.app.databinding.FragmentAdminUsersBinding;
import com.deskmanager.app.utils.DisplayUtils;
import com.deskmanager.app.utils.PasswordUtils;
import com.deskmanager.app.utils.SessionManager;
import com.deskmanager.app.viewmodel.UserViewModel;

public class AdminUsersFragment extends Fragment {

    private FragmentAdminUsersBinding binding;
    private UserViewModel userViewModel;
    private AdminUserAdapter userAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        SessionManager sessionManager = new SessionManager(requireContext());

        userAdapter = new AdminUserAdapter(
                sessionManager.getUserId(),
                (user, newRole) -> {
                    UserEntity updated = new UserEntity(user.getName(), user.getUsername(), user.getPassword(), newRole);
                    updated.setId(user.getId());
                    updated.setMustChangePassword(user.isMustChangePassword());
                    userViewModel.updateUser(updated);
                    Toast.makeText(requireContext(),
                            getString(R.string.msg_role_updated, user.getName(),
                                    DisplayUtils.userRole(requireContext(), newRole)),
                            Toast.LENGTH_SHORT).show();
                },
                user -> userViewModel.deleteUser(user, new UserRepository.DeleteCallback() {
                    @Override
                    public void onSuccess() {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(),
                                        getString(R.string.msg_user_deleted, user.getName()),
                                        Toast.LENGTH_SHORT).show());
                    }
                    @Override
                    public void onError(String message) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
                    }
                }),
                user -> {
                    String tempPassword = PasswordUtils.generateTempPassword();
                    userViewModel.resetPassword(user, tempPassword, new UserRepository.ResetPasswordCallback() {
                        @Override
                        public void onSuccess(String password) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() ->
                                    showTempPasswordDialog(user.getName(), password));
                        }
                        @Override
                        public void onError(String message) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
                        }
                    });
                }
        );

        binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUsers.setAdapter(userAdapter);

        userViewModel.getAllUsers().observe(getViewLifecycleOwner(),
                users -> userAdapter.submitList(users));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showTempPasswordDialog(String userName, String tempPassword) {
        android.view.View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_temp_password, null);
        android.widget.TextView tvPassword = dialogView.findViewById(R.id.tv_temp_password);
        android.widget.Button btnCopy = dialogView.findViewById(R.id.btn_copy_password);

        tvPassword.setText(tempPassword);

        btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager)
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("temp_password", tempPassword);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), getString(R.string.msg_password_copied), Toast.LENGTH_SHORT).show();
        });

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_temp_password_title, userName))
                .setView(dialogView)
                .setMessage(R.string.dialog_temp_password_message)
                .setPositiveButton(R.string.btn_close, null)
                .setCancelable(false)
                .show();
    }
}
