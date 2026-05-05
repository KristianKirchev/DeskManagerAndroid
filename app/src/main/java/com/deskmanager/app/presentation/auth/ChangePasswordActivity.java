package com.deskmanager.app.presentation.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.deskmanager.app.R;
import com.deskmanager.app.data.entities.UserEntity;
import com.deskmanager.app.data.repository.UserRepository;
import com.deskmanager.app.databinding.ActivityChangePasswordBinding;
import com.deskmanager.app.presentation.main.MainActivity;
import com.deskmanager.app.utils.LanguageManager;
import com.deskmanager.app.utils.SessionManager;
import com.deskmanager.app.viewmodel.UserViewModel;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageManager.applyLanguage(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        userViewModel  = new ViewModelProvider(this).get(UserViewModel.class);

        getOnBackPressedDispatcher().addCallback(this,
                new androidx.activity.OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Toast.makeText(ChangePasswordActivity.this,
                                getString(R.string.msg_must_change_password),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        binding.btnSetPassword.setOnClickListener(v -> attemptChangePassword());
    }

    private void attemptChangePassword() {
        String newPass     = binding.etNewPassword.getText().toString().trim();
        String confirmPass = binding.etConfirmNewPassword.getText().toString().trim();

        if (newPass.isEmpty()) {
            binding.etNewPassword.setError(getString(R.string.error_password_required));
            return;
        }
        if (newPass.length() < 6) {
            binding.etNewPassword.setError(getString(R.string.error_password_min));
            return;
        }
        if (!newPass.equals(confirmPass)) {
            binding.etConfirmNewPassword.setError(getString(R.string.error_passwords_no_match));
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSetPassword.setEnabled(false);

        int userId = sessionManager.getUserId();

        userViewModel.getUserById(userId, user -> {
            if (isFinishing() || isDestroyed()) return;
            if (user == null) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSetPassword.setEnabled(true);
                    Toast.makeText(this, getString(R.string.error_user_not_found), Toast.LENGTH_SHORT).show();
                });
                return;
            }

            UserEntity updated = new UserEntity(
                    user.getName(), user.getUsername(), newPass, user.getRole());
            updated.setId(user.getId());
            updated.setMustChangePassword(false);

            userViewModel.updateUser(updated);
            sessionManager.setMustChangePassword(false);

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this, getString(R.string.msg_password_changed), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            });
        });
    }
}
