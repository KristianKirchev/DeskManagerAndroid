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
import com.deskmanager.app.databinding.ActivityRegisterBinding;
import com.deskmanager.app.domain.enums.UserRole;
import com.deskmanager.app.utils.LanguageManager;
import com.deskmanager.app.utils.SessionManager;
import com.deskmanager.app.viewmodel.UserViewModel;
import com.deskmanager.app.presentation.main.MainActivity;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageManager.applyLanguage(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel  = new ViewModelProvider(this).get(UserViewModel.class);
        sessionManager = new SessionManager(this);

        binding.btnRegister.setOnClickListener(v -> attemptRegister());
        binding.tvLoginLink.setOnClickListener(v -> finish());

        observeViewModel();
    }

    private void attemptRegister() {
        String name     = binding.etName.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirm  = binding.etConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etName.setError(getString(R.string.error_name_required)); return;
        }
        if (username.isEmpty()) {
            binding.etUsername.setError(getString(R.string.error_username_required)); return;
        }
        if (username.length() < 3) {
            binding.etUsername.setError(getString(R.string.error_username_min)); return;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError(getString(R.string.error_password_required)); return;
        }
        if (password.length() < 6) {
            binding.etPassword.setError(getString(R.string.error_password_min)); return;
        }
        if (!password.equals(confirm)) {
            binding.etConfirmPassword.setError(getString(R.string.error_passwords_no_match)); return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnRegister.setEnabled(false);

        UserEntity newUser = new UserEntity(name, username, password, UserRole.USER);
        userViewModel.register(newUser);
    }

    private void observeViewModel() {
        userViewModel.getRegisterResult().observe(this, result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnRegister.setEnabled(true);

            if (result == null) return;

            if (result.equals("SUCCESS")) {
                Toast.makeText(this, getString(R.string.msg_account_created), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finishAffinity();
            } else if (result.startsWith("ERROR:")) {
                Toast.makeText(this, result.substring(6), Toast.LENGTH_LONG).show();
            }
        });
    }
}
