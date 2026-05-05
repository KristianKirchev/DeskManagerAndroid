package com.deskmanager.app.presentation.map;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.deskmanager.app.R;
import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.data.entities.ReservationEntity;
import com.deskmanager.app.databinding.FragmentOfficeMapBinding;
import com.deskmanager.app.domain.enums.DeskStatus;
import com.deskmanager.app.domain.enums.ReservationStatus;
import com.deskmanager.app.domain.enums.UserRole;
import com.deskmanager.app.utils.DateUtils;
import com.deskmanager.app.utils.SessionManager;
import com.deskmanager.app.viewmodel.DeskViewModel;
import com.deskmanager.app.viewmodel.ReservationViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OfficeMapFragment extends Fragment {

    private FragmentOfficeMapBinding binding;
    private DeskViewModel deskViewModel;
    private ReservationViewModel reservationViewModel;
    private SessionManager sessionManager;

    private final List<Integer> floors = new ArrayList<>();
    private int currentFloorIndex = 0;
    private LiveData<List<DeskEntity>> currentFloorLiveData;
    private final Observer<List<DeskEntity>> floorObserver = desks -> {
        if (binding == null || !isAdded()) return;
        binding.officeMapView.setDesks(desks);
        int shown = Math.min(desks != null ? desks.size() : 0, OfficeMapView.MAX_DESKS);
        int total = desks != null ? desks.size() : 0;
        binding.tvDeskCount.setText(getString(R.string.msg_desk_count, shown, total));
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOfficeMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deskViewModel        = new ViewModelProvider(requireActivity()).get(DeskViewModel.class);
        reservationViewModel = new ViewModelProvider(requireActivity()).get(ReservationViewModel.class);
        sessionManager       = new SessionManager(requireContext());

        setupFloorNavigation();
        loadFloors();
        observeReservationResult();

        if (sessionManager.getUserRole() == UserRole.GUEST) {
            binding.tvGuestMapNote.setVisibility(View.VISIBLE);
        }
    }

    private void setupFloorNavigation() {
        binding.btnPrevFloor.setOnClickListener(v -> {
            if (currentFloorIndex > 0) {
                currentFloorIndex--;
                showCurrentFloor();
            }
        });

        binding.btnNextFloor.setOnClickListener(v -> {
            if (currentFloorIndex < floors.size() - 1) {
                currentFloorIndex++;
                showCurrentFloor();
            }
        });
    }

    private void loadFloors() {
        deskViewModel.getDistinctFloors(result -> {
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                floors.clear();
                if (result != null) floors.addAll(result);
                currentFloorIndex = 0;
                showCurrentFloor();
            });
        });
    }

    private void showCurrentFloor() {
        if (floors.isEmpty()) {
            binding.tvFloorLabel.setText(R.string.label_no_floors);
            binding.btnPrevFloor.setEnabled(false);
            binding.btnNextFloor.setEnabled(false);
            binding.officeMapView.setDesks(new ArrayList<>());
            return;
        }

        int floor = floors.get(currentFloorIndex);
        binding.tvFloorLabel.setText(getString(R.string.label_floor, floor));
        binding.btnPrevFloor.setEnabled(currentFloorIndex > 0);
        binding.btnNextFloor.setEnabled(currentFloorIndex < floors.size() - 1);

        if (currentFloorLiveData != null) {
            currentFloorLiveData.removeObserver(floorObserver);
        }
        currentFloorLiveData = deskViewModel.getDesksForFloor(floor);
        currentFloorLiveData.observe(getViewLifecycleOwner(), floorObserver);

        binding.officeMapView.setOnDeskClickListener(this::onDeskTapped);
    }

    private void onDeskTapped(DeskEntity desk) {
        UserRole role = sessionManager.getUserRole();

        if (role == UserRole.GUEST) {
            String status = desk.getStatus() == DeskStatus.AVAILABLE
                    ? getString(R.string.legend_available)
                    : getString(R.string.legend_occupied);
            Toast.makeText(requireContext(),
                    getString(R.string.msg_guest_tap, desk.getRoomNumber(), status),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (desk.getStatus() == DeskStatus.OCCUPIED) {
            int userId = sessionManager.getUserId();
            reservationViewModel.hasActiveReservation(userId, desk.getId(), hasIt -> {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (hasIt) {
                        Toast.makeText(requireContext(),
                                getString(R.string.msg_your_reservation, desk.getRoomNumber()),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(),
                                getString(R.string.msg_occupied_other, desk.getRoomNumber()),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            });
            return;
        }

        showReservationDialog(desk);
    }

    private void showReservationDialog(DeskEntity desk) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_map_reserve, null);

        final long[] startDate = {-1L};
        final long[] endDate   = {-1L};

        android.widget.Button btnStart = dialogView.findViewById(R.id.btn_map_start_date);
        android.widget.Button btnEnd   = dialogView.findViewById(R.id.btn_map_end_date);
        android.widget.TextView tvDeskInfo = dialogView.findViewById(R.id.tv_map_desk_info);

        tvDeskInfo.setText(getString(R.string.label_desk_room_floor, desk.getRoomNumber(), desk.getFloor()));

        btnStart.setOnClickListener(v -> pickDate(true, startDate, endDate, btnStart, btnEnd));
        btnEnd.setOnClickListener(v -> pickDate(false, startDate, endDate, btnStart, btnEnd));

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_reserve_title)
                .setView(dialogView)
                .setPositiveButton(R.string.btn_confirm, (dialog, which) -> {
                    if (startDate[0] == -1L) {
                        Toast.makeText(requireContext(), getString(R.string.msg_select_start_date), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (endDate[0] == -1L) {
                        Toast.makeText(requireContext(), getString(R.string.msg_select_end_date), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (endDate[0] < startDate[0]) {
                        Toast.makeText(requireContext(), getString(R.string.msg_end_before_start), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ReservationEntity reservation = new ReservationEntity(
                            sessionManager.getUserId(),
                            desk.getId(),
                            startDate[0],
                            endDate[0],
                            ReservationStatus.ACTIVE
                    );
                    reservationViewModel.createReservation(reservation);
                })
                .setNegativeButton(R.string.btn_cancel_dialog, null)
                .show();
    }

    private void pickDate(boolean isStart, long[] startDate, long[] endDate,
                          android.widget.Button btnStart, android.widget.Button btnEnd) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (picker, year, month, day) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long millis = cal.getTimeInMillis();
            if (isStart) {
                startDate[0] = millis;
                btnStart.setText(getString(R.string.label_date_start_prefix, DateUtils.format(millis)));
            } else {
                endDate[0] = millis;
                btnEnd.setText(getString(R.string.label_date_end_prefix, DateUtils.format(millis)));
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void observeReservationResult() {
        reservationViewModel.getOperationResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            reservationViewModel.clearOperationResult();
            if (result.startsWith("ERROR:")) {
                Toast.makeText(requireContext(), result.substring(6), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
                showCurrentFloor();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
