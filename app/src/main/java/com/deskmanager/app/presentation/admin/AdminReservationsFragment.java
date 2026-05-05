package com.deskmanager.app.presentation.admin;

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

import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.databinding.FragmentAdminReservationsBinding;
import com.deskmanager.app.data.repository.DeskRepository;
import com.deskmanager.app.utils.NotificationHelper;
import com.deskmanager.app.utils.SessionManager;
import com.deskmanager.app.viewmodel.DeskViewModel;
import com.deskmanager.app.viewmodel.ReservationViewModel;
import com.deskmanager.app.viewmodel.UserViewModel;

public class AdminReservationsFragment extends Fragment {

    private FragmentAdminReservationsBinding binding;
    private ReservationViewModel reservationViewModel;
    private DeskViewModel deskViewModel;
    private UserViewModel userViewModel;
    private AdminReservationAdapter reservationAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminReservationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reservationViewModel = new ViewModelProvider(requireActivity()).get(ReservationViewModel.class);
        deskViewModel        = new ViewModelProvider(requireActivity()).get(DeskViewModel.class);
        userViewModel        = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        reservationAdapter = new AdminReservationAdapter(reservation -> {
            reservationViewModel.cancelReservation(reservation);
            deskViewModel.getDeskById(reservation.getDeskId(), new DeskRepository.GetDeskCallback() {
                @Override
                public void onSuccess(DeskEntity desk) {
                    routeCancelNotification(reservation.getUserId(), desk.getRoomNumber());
                }
                @Override
                public void onError(String message) {
                    routeCancelNotification(reservation.getUserId(), "#" + reservation.getDeskId());
                }
            });
        });

        binding.rvAllReservations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAllReservations.setAdapter(reservationAdapter);

        reservationViewModel.getAllReservations().observe(getViewLifecycleOwner(),
                reservations -> reservationAdapter.submitList(reservations));

        userViewModel.getAllUsers().observe(getViewLifecycleOwner(),
                users -> reservationAdapter.setUsers(users));

        deskViewModel.getAllDesks().observe(getViewLifecycleOwner(),
                desks -> reservationAdapter.setDesks(desks));

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

    private void routeCancelNotification(int targetUserId, String roomName) {
        int loggedInUserId = new SessionManager(requireContext()).getUserId();
        if (loggedInUserId == targetUserId) {
            NotificationHelper.notifyAdminCancelled(requireContext(), roomName, targetUserId);
        } else {
            userViewModel.appendPendingCancelNotif(targetUserId, roomName);
        }
    }
}
