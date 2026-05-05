package com.deskmanager.app.presentation.desk;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import com.deskmanager.app.R;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;

public class DeskListFragmentDirections {
  private DeskListFragmentDirections() {
  }

  @NonNull
  public static ActionDeskListFragmentToDeskDetailFragment actionDeskListFragmentToDeskDetailFragment(
      ) {
    return new ActionDeskListFragmentToDeskDetailFragment();
  }

  public static class ActionDeskListFragmentToDeskDetailFragment implements NavDirections {
    private final HashMap arguments = new HashMap();

    private ActionDeskListFragmentToDeskDetailFragment() {
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public ActionDeskListFragmentToDeskDetailFragment setDeskId(int deskId) {
      this.arguments.put("deskId", deskId);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NonNull
    public Bundle getArguments() {
      Bundle __result = new Bundle();
      if (arguments.containsKey("deskId")) {
        int deskId = (int) arguments.get("deskId");
        __result.putInt("deskId", deskId);
      } else {
        __result.putInt("deskId", -1);
      }
      return __result;
    }

    @Override
    public int getActionId() {
      return R.id.action_deskListFragment_to_deskDetailFragment;
    }

    @SuppressWarnings("unchecked")
    public int getDeskId() {
      return (int) arguments.get("deskId");
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) {
          return true;
      }
      if (object == null || getClass() != object.getClass()) {
          return false;
      }
      ActionDeskListFragmentToDeskDetailFragment that = (ActionDeskListFragmentToDeskDetailFragment) object;
      if (arguments.containsKey("deskId") != that.arguments.containsKey("deskId")) {
        return false;
      }
      if (getDeskId() != that.getDeskId()) {
        return false;
      }
      if (getActionId() != that.getActionId()) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int result = 1;
      result = 31 * result + getDeskId();
      result = 31 * result + getActionId();
      return result;
    }

    @Override
    public String toString() {
      return "ActionDeskListFragmentToDeskDetailFragment(actionId=" + getActionId() + "){"
          + "deskId=" + getDeskId()
          + "}";
    }
  }
}
