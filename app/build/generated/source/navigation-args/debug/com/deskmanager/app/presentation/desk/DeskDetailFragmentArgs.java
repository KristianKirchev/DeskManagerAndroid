package com.deskmanager.app.presentation.desk;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavArgs;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;

public class DeskDetailFragmentArgs implements NavArgs {
  private final HashMap arguments = new HashMap();

  private DeskDetailFragmentArgs() {
  }

  @SuppressWarnings("unchecked")
  private DeskDetailFragmentArgs(HashMap argumentsMap) {
    this.arguments.putAll(argumentsMap);
  }

  @NonNull
  @SuppressWarnings("unchecked")
  public static DeskDetailFragmentArgs fromBundle(@NonNull Bundle bundle) {
    DeskDetailFragmentArgs __result = new DeskDetailFragmentArgs();
    bundle.setClassLoader(DeskDetailFragmentArgs.class.getClassLoader());
    if (bundle.containsKey("deskId")) {
      int deskId;
      deskId = bundle.getInt("deskId");
      __result.arguments.put("deskId", deskId);
    } else {
      __result.arguments.put("deskId", -1);
    }
    return __result;
  }

  @NonNull
  @SuppressWarnings("unchecked")
  public static DeskDetailFragmentArgs fromSavedStateHandle(
      @NonNull SavedStateHandle savedStateHandle) {
    DeskDetailFragmentArgs __result = new DeskDetailFragmentArgs();
    if (savedStateHandle.contains("deskId")) {
      int deskId;
      deskId = savedStateHandle.get("deskId");
      __result.arguments.put("deskId", deskId);
    } else {
      __result.arguments.put("deskId", -1);
    }
    return __result;
  }

  @SuppressWarnings("unchecked")
  public int getDeskId() {
    return (int) arguments.get("deskId");
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public Bundle toBundle() {
    Bundle __result = new Bundle();
    if (arguments.containsKey("deskId")) {
      int deskId = (int) arguments.get("deskId");
      __result.putInt("deskId", deskId);
    } else {
      __result.putInt("deskId", -1);
    }
    return __result;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public SavedStateHandle toSavedStateHandle() {
    SavedStateHandle __result = new SavedStateHandle();
    if (arguments.containsKey("deskId")) {
      int deskId = (int) arguments.get("deskId");
      __result.set("deskId", deskId);
    } else {
      __result.set("deskId", -1);
    }
    return __result;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
        return true;
    }
    if (object == null || getClass() != object.getClass()) {
        return false;
    }
    DeskDetailFragmentArgs that = (DeskDetailFragmentArgs) object;
    if (arguments.containsKey("deskId") != that.arguments.containsKey("deskId")) {
      return false;
    }
    if (getDeskId() != that.getDeskId()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + getDeskId();
    return result;
  }

  @Override
  public String toString() {
    return "DeskDetailFragmentArgs{"
        + "deskId=" + getDeskId()
        + "}";
  }

  public static final class Builder {
    private final HashMap arguments = new HashMap();

    @SuppressWarnings("unchecked")
    public Builder(@NonNull DeskDetailFragmentArgs original) {
      this.arguments.putAll(original.arguments);
    }

    public Builder() {
    }

    @NonNull
    public DeskDetailFragmentArgs build() {
      DeskDetailFragmentArgs result = new DeskDetailFragmentArgs(arguments);
      return result;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Builder setDeskId(int deskId) {
      this.arguments.put("deskId", deskId);
      return this;
    }

    @SuppressWarnings({"unchecked","GetterOnBuilder"})
    public int getDeskId() {
      return (int) arguments.get("deskId");
    }
  }
}
