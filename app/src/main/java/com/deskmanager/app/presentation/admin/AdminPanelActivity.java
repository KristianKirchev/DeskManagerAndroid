package com.deskmanager.app.presentation.admin;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.deskmanager.app.R;
import com.deskmanager.app.databinding.ActivityAdminPanelBinding;
import com.deskmanager.app.domain.enums.UserRole;
import com.deskmanager.app.utils.LanguageManager;
import com.deskmanager.app.utils.SessionManager;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminPanelActivity extends AppCompatActivity {

    private ActivityAdminPanelBinding binding;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageManager.applyLanguage(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminPanelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_admin_panel);
        }

        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getUserRole() != UserRole.ADMIN) {
            Toast.makeText(this, getString(R.string.msg_unauthorized), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.viewPager.setAdapter(new AdminTabAdapter(this));

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText(R.string.tab_admin_desks); break;
                case 1: tab.setText(R.string.tab_admin_reservations); break;
                case 2: tab.setText(R.string.tab_admin_users); break;
            }
        }).attach();

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.fabAddDesk.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            }
        });

        binding.fabAddDesk.setOnClickListener(v -> {
            Fragment f = getSupportFragmentManager().findFragmentByTag("f0");
            if (f instanceof AdminDesksFragment) {
                ((AdminDesksFragment) f).showAddDeskDialog();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private static class AdminTabAdapter extends FragmentStateAdapter {

        AdminTabAdapter(FragmentActivity fa) { super(fa); }

        @Override
        public int getItemCount() { return 3; }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new AdminDesksFragment();
                case 1: return new AdminReservationsFragment();
                case 2: return new AdminUsersFragment();
                default: throw new IllegalArgumentException();
            }
        }
    }
}
