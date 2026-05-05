package com.deskmanager.app.presentation.desk;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deskmanager.app.R;
import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.databinding.FragmentDeskListBinding;
import com.deskmanager.app.domain.enums.DeskStatus;
import com.deskmanager.app.viewmodel.DeskViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class DeskListFragment extends Fragment {

    private FragmentDeskListBinding binding;
    private DeskViewModel deskViewModel;
    private DeskAdapter adapter;

    private LiveData<List<DeskEntity>> currentDesksLiveData;
    private final Observer<List<DeskEntity>> desksObserver = desks -> {
        if (binding == null) return;
        adapter.submitList(desks);
        updateEmptyState(desks == null || desks.isEmpty());
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDeskListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deskViewModel = new ViewModelProvider(requireActivity()).get(DeskViewModel.class);

        setupRecyclerView();
        setupTabs();
        setupSearch();

        observeAllDesks();
    }

    private void setupRecyclerView() {
        adapter = new DeskAdapter(desk -> {
            Bundle args = new Bundle();
            args.putInt("deskId", desk.getId());
            Navigation.findNavController(requireView())
                      .navigate(R.id.action_deskListFragment_to_deskDetailFragment, args);
        });
        binding.rvDesks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDesks.setAdapter(adapter);
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String query = binding.etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    observeDesks(deskViewModel.searchDesks(query));
                } else {
                    switch (tab.getPosition()) {
                        case 1: observeByStatus(DeskStatus.AVAILABLE); break;
                        case 2: observeByStatus(DeskStatus.OCCUPIED);  break;
                        default: observeAllDesks();
                    }
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString().trim();
                if (q.isEmpty()) {
                    int pos = binding.tabLayout.getSelectedTabPosition();
                    if (pos == 1) observeDesks(deskViewModel.getDesksByStatus(DeskStatus.AVAILABLE));
                    else if (pos == 2) observeDesks(deskViewModel.getDesksByStatus(DeskStatus.OCCUPIED));
                    else observeDesks(deskViewModel.getAllDesks());
                } else {
                    observeDesks(deskViewModel.searchDesks(q));
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void observeDesks(LiveData<List<DeskEntity>> liveData) {
        if (currentDesksLiveData != null) {
            currentDesksLiveData.removeObserver(desksObserver);
        }
        currentDesksLiveData = liveData;
        currentDesksLiveData.observe(getViewLifecycleOwner(), desksObserver);
    }

    private void observeAllDesks() {
        observeDesks(deskViewModel.getAllDesks());
    }

    private void observeByStatus(DeskStatus status) {
        observeDesks(deskViewModel.getDesksByStatus(status));
    }

    private void updateEmptyState(boolean isEmpty) {
        binding.tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.rvDesks.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
