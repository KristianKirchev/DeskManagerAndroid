package com.deskmanager.app.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.deskmanager.app.domain.enums.UserRole;

@Entity(
    tableName = "users",
    indices = {@Index(value = {"username"}, unique = true)}
)
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "role")
    private UserRole role;

    @ColumnInfo(name = "must_change_password")
    private boolean mustChangePassword;

    @ColumnInfo(name = "pending_cancel_notif")
    private String pendingCancelNotif;

    public UserEntity() {}

    @Ignore
    public UserEntity(String name, String username, String password, UserRole role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.mustChangePassword = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }

    public String getPendingCancelNotif() { return pendingCancelNotif; }
    public void setPendingCancelNotif(String pendingCancelNotif) { this.pendingCancelNotif = pendingCancelNotif; }
}
