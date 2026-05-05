package com.deskmanager.app.presentation.admin;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deskmanager.app.R;
import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.databinding.FragmentAdminDesksBinding;
import com.deskmanager.app.domain.enums.DeskStatus;
import com.deskmanager.app.utils.NotificationHelper;
import com.deskmanager.app.viewmodel.DeskViewModel;
import com.deskmanager.app.presentation.desk.DeskAdapter;

public class AdminDesksFragment extends Fragment {

    private FragmentAdminDesksBinding binding;
    private DeskViewModel deskViewModel;
    private DeskAdapter deskAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDesksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deskViewModel = new ViewModelProvider(requireActivity()).get(DeskViewModel.class);

        deskAdapter = new DeskAdapter(this::showDeskOptionsDialog);
        binding.rvAdminDesks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAdminDesks.setAdapter(deskAdapter);

        deskViewModel.getAllDesks().observe(getViewLifecycleOwner(), desks -> deskAdapter.submitList(desks));
        deskViewModel.getOperationResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            deskViewModel.clearOperationResult();
            Toast.makeText(requireContext(),
                    result.startsWith("ERROR:") ? result.substring(6) : result,
                    Toast.LENGTH_SHORT).show();
        });
    }

    void showAddDeskDialog() {
        showAddDeskDialogInternal(null);
    }

    private void showDeskOptionsDialog(DeskEntity desk) {
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_manage_desk_title, desk.getRoomNumber()))
                .setItems(new CharSequence[]{
                        getString(R.string.btn_edit),
                        getString(R.string.btn_delete)
                }, (dialog, which) -> {
                    if (which == 0) showAddDeskDialogInternal(desk);
                    else            confirmDelete(desk);
                })
                .show();
    }

    private void confirmDelete(DeskEntity desk) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message, desk.getRoomNumber()))
                .setPositiveButton(R.string.btn_delete, (d, w) -> deskViewModel.deleteDesk(desk))
                .setNegativeButton(R.string.btn_cancel_dialog, null)
                .show();
    }

    private void showAddDeskDialogInternal(@Nullable DeskEntity existingDesk) {
        boolean isEdit = existingDesk != null;
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_desk, null);

        android.widget.EditText etRoom     = dialogView.findViewById(R.id.et_room_number);
        android.widget.EditText etFloor    = dialogView.findViewById(R.id.et_floor);
        android.widget.CheckBox cbOccupied = dialogView.findViewById(R.id.cb_occupied);

        if (isEdit) {
            etRoom.setText(existingDesk.getRoomNumber());
            etFloor.setText(String.valueOf(existingDesk.getFloor()));
            cbOccupied.setChecked(existingDesk.isOccupied());
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(isEdit ? R.string.dialog_edit_desk_title : R.string.dialog_add_desk_title)
                .setView(dialogView)
                .setPositiveButton(R.string.btn_save, (dialog, which) -> {
                    String room  = etRoom.getText().toString().trim();
                    String floor = etFloor.getText().toString().trim();

                    if (room.isEmpty() || floor.isEmpty()) {
                        Toast.makeText(requireContext(), getString(R.string.msg_fields_required), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int floorNum;
                    try {
                        floorNum = Integer.parseInt(floor);
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), getString(R.string.error_floor_invalid), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean occupied = cbOccupied.isChecked();
                    DeskStatus status = occupied ? DeskStatus.OCCUPIED : DeskStatus.AVAILABLE;

                    if (isEdit) {
                        existingDesk.setRoomNumber(room);
                        existingDesk.setFloor(floorNum);
                        existingDesk.setOccupied(occupied);
                        existingDesk.setStatus(status);
                        deskViewModel.updateDesk(existingDesk);
                    } else {
                        DeskEntity newDesk = new DeskEntity(room, floorNum, occupied, status);
                        deskViewModel.addDesk(newDesk);
                        if (!occupied) {
                            NotificationHelper.notifyNewDesk(requireContext(), room, floorNum);
                        }
                    }
                })
                .setNegativeButton(R.string.btn_cancel_dialog, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
