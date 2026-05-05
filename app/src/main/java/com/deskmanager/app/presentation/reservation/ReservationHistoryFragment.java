package com.deskmanager.app.presentation.reservation;

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

import com.deskmanager.app.databinding.FragmentReservationHistoryBinding;
import com.deskmanager.app.utils.SessionManager;
import com.deskmanager.app.viewmodel.DeskViewModel;
import com.deskmanager.app.viewmodel.ReservationViewModel;

public class ReservationHistoryFragment extends Fragment {

    private FragmentReservationHistoryBinding binding;
    private ReservationViewModel reservationViewModel;
    private DeskViewModel deskViewModel;
    private ReservationAdapter adapter;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReservationHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reservationViewModel = new ViewModelProvider(requireActivity()).get(ReservationViewModel.class);
        deskViewModel        = new ViewModelProvider(requireActivity()).get(DeskViewModel.class);
        sessionManager       = new SessionManager(requireContext());

        setupRecyclerView();
        loadReservations();
        observeDesks();
        observeResult();
    }

    private void setupRecyclerView() {
        adapter = new ReservationAdapter(reservation -> {
            reservationViewModel.cancelReservation(reservation);
        });
        binding.rvReservations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReservations.setAdapter(adapter);
    }

    private void loadReservations() {
        int userId = sessionManager.getUserId();
        reservationViewModel.getReservationsByUser(userId)
                .observe(getViewLifecycleOwner(), reservations -> {
                    adapter.submitList(reservations);
                    boolean empty = reservations == null || reservations.isEmpty();
                    binding.tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                    binding.rvReservations.setVisibility(empty ? View.GONE : View.VISIBLE);
                });
    }

    private void observeDesks() {
        deskViewModel.getAllDesks().observe(getViewLifecycleOwner(),
                desks -> adapter.setDesks(desks));
    }

    private void observeResult() {
        reservationViewModel.getOperationResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            reservationViewModel.clearOperationResult();
            Toast.makeText(requireContext(),
                    result.startsWith("ERROR:") ? result.substring(6) : result,
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
