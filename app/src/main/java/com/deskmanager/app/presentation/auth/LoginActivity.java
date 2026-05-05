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
import com.deskmanager.app.databinding.ActivityLoginBinding;
import com.deskmanager.app.presentation.main.MainActivity;
import com.deskmanager.app.utils.LanguageManager;
import com.deskmanager.app.utils.NotificationHelper;
import com.deskmanager.app.utils.SessionManager;
import com.deskmanager.app.viewmodel.ReservationViewModel;
import com.deskmanager.app.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;
    private ReservationViewModel reservationViewModel;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageManager.applyLanguage(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager       = new SessionManager(this);
        userViewModel        = new ViewModelProvider(this).get(UserViewModel.class);
        reservationViewModel = new ViewModelProvider(this).get(ReservationViewModel.class);

        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        setupClickListeners();
        observeViewModel();
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        binding.tvRegisterLink.setOnClickListener(v ->
            startActivity(new Intent(this, RegisterActivity.class))
        );

        binding.btnFillAdmin.setOnClickListener(v -> {
            binding.etUsername.setText("admin");
            binding.etPassword.setText("admin123");
        });
        binding.btnFillUser.setOnClickListener(v -> {
            binding.etUsername.setText("bob");
            binding.etPassword.setText("user123");
        });
        binding.btnFillGuest.setOnClickListener(v -> {
            binding.etUsername.setText("guest");
            binding.etPassword.setText("guest123");
        });
    }

    private void attemptLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            binding.etUsername.setError(getString(R.string.error_username_required));
            return;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError(getString(R.string.error_password_required));
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);
        userViewModel.login(username, password);
    }

    private void observeViewModel() {
        userViewModel.getLoggedInUser().observe(this, user -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setEnabled(true);
            if (user != null) {
                sessionManager.saveSession(
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getRole()
                );
                sessionManager.setMustChangePassword(user.isMustChangePassword());
                showWelcomeNotification(user.getId(), user.getName());
                checkPendingCancelNotification(user);
                if (user.isMustChangePassword()) {
                    startActivity(new Intent(this, ChangePasswordActivity.class));
                } else {
                    navigateToMain();
                }
                finish();
            }
        });

        userViewModel.getLoginError().observe(this, error -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setEnabled(true);
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showWelcomeNotification(int userId, String name) {
        reservationViewModel.countActiveForUser(userId, count ->
                NotificationHelper.notifyWelcome(this, name, count));
    }

    private void checkPendingCancelNotification(UserEntity user) {
        String pending = user.getPendingCancelNotif();
        if (pending == null || pending.isEmpty()) return;
        String[] rooms = pending.split("\n");
        for (int i = 0; i < rooms.length; i++) {
            String room = rooms[i].trim();
            if (!room.isEmpty()) {
                NotificationHelper.notifyAdminCancelled(this, room, i);
            }
        }
        userViewModel.clearPendingCancelNotif(user.getId());
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
