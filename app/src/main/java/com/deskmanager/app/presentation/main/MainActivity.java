package com.deskmanager.app.presentation.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.deskmanager.app.R;
import com.deskmanager.app.databinding.ActivityMainBinding;
import com.deskmanager.app.domain.enums.UserRole;
import com.deskmanager.app.presentation.admin.AdminPanelActivity;
import com.deskmanager.app.presentation.auth.LoginActivity;
import com.deskmanager.app.utils.DisplayUtils;
import com.deskmanager.app.utils.LanguageManager;
import com.deskmanager.app.utils.NotificationHelper;
import com.deskmanager.app.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;
    private NavController navController;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageManager.applyLanguage(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        requestNotificationPermission();

        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            finish();
            return;
        }
        navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(
                R.id.deskListFragment, R.id.officeMapFragment, R.id.reservationHistoryFragment)
                .setOpenableLayout(binding.drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
        NavigationUI.setupWithNavController(binding.navView, navController);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.nav_open, R.string.nav_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        View headerView = binding.navView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tv_nav_name);
        TextView tvRole = headerView.findViewById(R.id.tv_nav_role);
        tvName.setText(sessionManager.getUserName());
        tvRole.setText(DisplayUtils.userRole(this, sessionManager.getUserRole()));

        boolean isAdmin = sessionManager.getUserRole() == UserRole.ADMIN;
        boolean isGuest = sessionManager.getUserRole() == UserRole.GUEST;

        binding.navView.getMenu()
                .findItem(R.id.nav_admin_panel)
                .setVisible(isAdmin);

        binding.navView.getMenu()
                .findItem(R.id.nav_my_reservations)
                .setVisible(!isGuest);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_desk_list) {
            navController.navigate(R.id.deskListFragment);
        } else if (id == R.id.nav_office_map) {
            navController.navigate(R.id.officeMapFragment);
        } else if (id == R.id.nav_my_reservations) {
            navController.navigate(R.id.reservationHistoryFragment);
        } else if (id == R.id.nav_admin_panel) {
            startActivity(new Intent(this, AdminPanelActivity.class));
        } else if (id == R.id.nav_language) {
            showLanguageDialog();
        } else if (id == R.id.nav_test_notification) {
            NotificationHelper.notifyTest(this);
        } else if (id == R.id.nav_logout) {
            sessionManager.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }

    private void showLanguageDialog() {
        String current = LanguageManager.getLanguage(this);
        int selected = current.equals(LanguageManager.LANG_BULGARIAN) ? 1 : 0;

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_language_title)
                .setSingleChoiceItems(
                        new CharSequence[]{
                                getString(R.string.lang_english),
                                getString(R.string.lang_bulgarian)
                        },
                        selected,
                        (dialog, which) -> {
                            String lang = which == 1
                                    ? LanguageManager.LANG_BULGARIAN
                                    : LanguageManager.LANG_ENGLISH;
                            if (!lang.equals(current)) {
                                LanguageManager.setLanguage(this, lang);
                                dialog.dismiss();
                                recreate();
                            } else {
                                dialog.dismiss();
                            }
                        })
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
