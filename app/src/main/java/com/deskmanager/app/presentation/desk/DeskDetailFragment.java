package com.deskmanager.app.presentation.desk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.deskmanager.app.R;
import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.data.entities.ReservationEntity;
import com.deskmanager.app.databinding.FragmentDeskDetailBinding;
import com.deskmanager.app.domain.enums.DeskStatus;
import com.deskmanager.app.domain.enums.ReservationStatus;
import com.deskmanager.app.domain.enums.UserRole;
import com.deskmanager.app.utils.DateUtils;
import com.deskmanager.app.utils.DisplayUtils;
import com.deskmanager.app.utils.NotificationHelper;
import com.deskmanager.app.utils.ReminderScheduler;
import com.deskmanager.app.utils.SessionManager;
import com.deskmanager.app.viewmodel.DeskViewModel;
import com.deskmanager.app.viewmodel.ReservationViewModel;

public class DeskDetailFragment extends Fragment {

    private FragmentDeskDetailBinding binding;
    private DeskViewModel deskViewModel;
    private ReservationViewModel reservationViewModel;
    private SessionManager sessionManager;

    private DeskEntity currentDesk;
    private long selectedStartDate = -1L;
    private long selectedEndDate   = -1L;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDeskDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deskViewModel        = new ViewModelProvider(requireActivity()).get(DeskViewModel.class);
        reservationViewModel = new ViewModelProvider(requireActivity()).get(ReservationViewModel.class);
        sessionManager       = new SessionManager(requireContext());

        int deskId = getArguments() != null ? getArguments().getInt("deskId", -1) : -1;
        if (deskId == -1) {
            Toast.makeText(requireContext(), getString(R.string.msg_desk_not_found), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
            return;
        }

        deskViewModel.selectDesk(deskId);

        observeDesk();
        observeOperationResult();
        setupButtons();
    }

    private void observeDesk() {
        deskViewModel.getSelectedDesk().observe(getViewLifecycleOwner(), desk -> {
            if (desk == null) return;
            currentDesk = desk;
            populateUI(desk);
        });
    }

    private void observeOperationResult() {
        reservationViewModel.getOperationResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            reservationViewModel.clearOperationResult();
            if (result.startsWith("ERROR:")) {
                Toast.makeText(requireContext(), result.substring(6), Toast.LENGTH_LONG).show();
            } else {
                if (currentDesk != null && selectedStartDate != -1L) {
                    NotificationHelper.notifyReservationConfirmed(
                            requireContext(),
                            currentDesk.getRoomNumber(),
                            DateUtils.format(selectedStartDate));
                    ReservationEntity dummy = new ReservationEntity(
                            sessionManager.getUserId(), currentDesk.getId(),
                            selectedStartDate, selectedEndDate, ReservationStatus.ACTIVE);
                    ReminderScheduler.schedule(requireContext(), dummy, currentDesk.getRoomNumber());
                }
                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
    }

    private void populateUI(DeskEntity desk) {
        binding.tvRoomNumber.setText(getString(R.string.label_desk_room, desk.getRoomNumber()));
        binding.tvFloor.setText(getString(R.string.label_desk_floor, desk.getFloor()));

        boolean available = desk.getStatus() == DeskStatus.AVAILABLE;
        binding.tvStatus.setText(DisplayUtils.deskStatus(requireContext(), desk.getStatus()));
        binding.tvStatus.setTextColor(
                requireContext().getColor(available ? R.color.green_500 : R.color.red_500));

        applyRolePermissions(desk);
    }

    private void applyRolePermissions(DeskEntity desk) {
        UserRole role = sessionManager.getUserRole();

        if (role == UserRole.GUEST) {
            binding.reservationForm.setVisibility(View.GONE);
            binding.btnFreeDesk.setVisibility(View.GONE);
            binding.tvUnavailableMsg.setVisibility(View.GONE);
            binding.tvGuestNote.setVisibility(View.VISIBLE);
            return;
        }

        binding.tvGuestNote.setVisibility(View.GONE);

        boolean available = desk.getStatus() == DeskStatus.AVAILABLE;

        if (available) {
            binding.reservationForm.setVisibility(View.VISIBLE);
            binding.btnFreeDesk.setVisibility(View.GONE);
            binding.tvUnavailableMsg.setVisibility(View.GONE);
        } else {
            binding.reservationForm.setVisibility(View.GONE);
            checkOwnership(desk.getId());
        }
    }

    private void checkOwnership(int deskId) {
        int userId = sessionManager.getUserId();
        reservationViewModel.hasActiveReservation(userId, deskId, hasReservation -> {
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                if (hasReservation) {
                    binding.btnFreeDesk.setVisibility(View.VISIBLE);
                    binding.tvUnavailableMsg.setVisibility(View.GONE);
                } else {
                    binding.btnFreeDesk.setVisibility(View.GONE);
                    binding.tvUnavailableMsg.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void setupButtons() {
        binding.btnPickStartDate.setOnClickListener(v -> showDatePicker(true));
        binding.btnPickEndDate.setOnClickListener(v -> showDatePicker(false));
        binding.btnReserve.setOnClickListener(v -> submitReservation());
        binding.btnFreeDesk.setOnClickListener(v -> freeDesk());
    }

    private void showDatePicker(boolean isStart) {
        android.app.DatePickerDialog dialog = new android.app.DatePickerDialog(
                requireContext(),
                (picker, year, month, day) -> {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.set(year, month, day, 0, 0, 0);
                    cal.set(java.util.Calendar.MILLISECOND, 0);
                    long millis = cal.getTimeInMillis();
                    if (isStart) {
                        selectedStartDate = millis;
                        binding.btnPickStartDate.setText(DateUtils.format(millis));
                    } else {
                        selectedEndDate = millis;
                        binding.btnPickEndDate.setText(DateUtils.format(millis));
                    }
                },
                java.util.Calendar.getInstance().get(java.util.Calendar.YEAR),
                java.util.Calendar.getInstance().get(java.util.Calendar.MONTH),
                java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void submitReservation() {
        if (currentDesk == null) return;
        if (selectedStartDate == -1L) {
            Toast.makeText(requireContext(), getString(R.string.msg_select_start_date), Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedEndDate == -1L) {
            Toast.makeText(requireContext(), getString(R.string.msg_select_end_date), Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedEndDate < selectedStartDate) {
            Toast.makeText(requireContext(), getString(R.string.msg_end_before_start), Toast.LENGTH_SHORT).show();
            return;
        }

        ReservationEntity reservation = new ReservationEntity(
                sessionManager.getUserId(),
                currentDesk.getId(),
                selectedStartDate,
                selectedEndDate,
                ReservationStatus.ACTIVE
        );
        reservationViewModel.createReservation(reservation);
    }

    private void freeDesk() {
        if (currentDesk == null) return;
        reservationViewModel.freeDesk(sessionManager.getUserId(), currentDesk.getId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
