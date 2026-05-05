package com.deskmanager.app.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.deskmanager.app.data.database.Converters;
import com.deskmanager.app.data.entities.UserEntity;
import com.deskmanager.app.domain.enums.UserRole;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserEntity> __insertionAdapterOfUserEntity;

  private final EntityDeletionOrUpdateAdapter<UserEntity> __deletionAdapterOfUserEntity;

  private final EntityDeletionOrUpdateAdapter<UserEntity> __updateAdapterOfUserEntity;

  public UserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserEntity = new EntityInsertionAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `users` (`id`,`name`,`username`,`password`,`role`,`must_change_password`,`pending_cancel_notif`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final UserEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getUsername() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getUsername());
        }
        if (entity.getPassword() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPassword());
        }
        final String _tmp = Converters.fromUserRole(entity.getRole());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        final int _tmp_1 = entity.isMustChangePassword() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        if (entity.getPendingCancelNotif() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPendingCancelNotif());
        }
      }
    };
    this.__deletionAdapterOfUserEntity = new EntityDeletionOrUpdateAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `users` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final UserEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfUserEntity = new EntityDeletionOrUpdateAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `users` SET `id` = ?,`name` = ?,`username` = ?,`password` = ?,`role` = ?,`must_change_password` = ?,`pending_cancel_notif` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final UserEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getUsername() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getUsername());
        }
        if (entity.getPassword() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPassword());
        }
        final String _tmp = Converters.fromUserRole(entity.getRole());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp);
        }
        final int _tmp_1 = entity.isMustChangePassword() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        if (entity.getPendingCancelNotif() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPendingCancelNotif());
        }
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public long insert(final UserEntity user) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfUserEntity.insertAndReturnId(user);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final UserEntity user) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfUserEntity.handle(user);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final UserEntity user) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfUserEntity.handle(user);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<UserEntity>> getAllUsers() {
    final String _sql = "SELECT * FROM users ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"users"}, false, new Callable<List<UserEntity>>() {
      @Override
      @Nullable
      public List<UserEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfMustChangePassword = CursorUtil.getColumnIndexOrThrow(_cursor, "must_change_password");
          final int _cursorIndexOfPendingCancelNotif = CursorUtil.getColumnIndexOrThrow(_cursor, "pending_cancel_notif");
          final List<UserEntity> _result = new ArrayList<UserEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UserEntity _item;
            _item = new UserEntity();
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            _item.setName(_tmpName);
            final String _tmpUsername;
            if (_cursor.isNull(_cursorIndexOfUsername)) {
              _tmpUsername = null;
            } else {
              _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            }
            _item.setUsername(_tmpUsername);
            final String _tmpPassword;
            if (_cursor.isNull(_cursorIndexOfPassword)) {
              _tmpPassword = null;
            } else {
              _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
            }
            _item.setPassword(_tmpPassword);
            final UserRole _tmpRole;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfRole)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfRole);
            }
            _tmpRole = Converters.toUserRole(_tmp);
            _item.setRole(_tmpRole);
            final boolean _tmpMustChangePassword;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfMustChangePassword);
            _tmpMustChangePassword = _tmp_1 != 0;
            _item.setMustChangePassword(_tmpMustChangePassword);
            final String _tmpPendingCancelNotif;
            if (_cursor.isNull(_cursorIndexOfPendingCancelNotif)) {
              _tmpPendingCancelNotif = null;
            } else {
              _tmpPendingCancelNotif = _cursor.getString(_cursorIndexOfPendingCancelNotif);
            }
            _item.setPendingCancelNotif(_tmpPendingCancelNotif);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public UserEntity getUserById(final int id) {
    final String _sql = "SELECT * FROM users WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
      final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
      final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
      final int _cursorIndexOfMustChangePassword = CursorUtil.getColumnIndexOrThrow(_cursor, "must_change_password");
      final int _cursorIndexOfPendingCancelNotif = CursorUtil.getColumnIndexOrThrow(_cursor, "pending_cancel_notif");
      final UserEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new UserEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        _result.setName(_tmpName);
        final String _tmpUsername;
        if (_cursor.isNull(_cursorIndexOfUsername)) {
          _tmpUsername = null;
        } else {
          _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
        }
        _result.setUsername(_tmpUsername);
        final String _tmpPassword;
        if (_cursor.isNull(_cursorIndexOfPassword)) {
          _tmpPassword = null;
        } else {
          _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
        }
        _result.setPassword(_tmpPassword);
        final UserRole _tmpRole;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfRole)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfRole);
        }
        _tmpRole = Converters.toUserRole(_tmp);
        _result.setRole(_tmpRole);
        final boolean _tmpMustChangePassword;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfMustChangePassword);
        _tmpMustChangePassword = _tmp_1 != 0;
        _result.setMustChangePassword(_tmpMustChangePassword);
        final String _tmpPendingCancelNotif;
        if (_cursor.isNull(_cursorIndexOfPendingCancelNotif)) {
          _tmpPendingCancelNotif = null;
        } else {
          _tmpPendingCancelNotif = _cursor.getString(_cursorIndexOfPendingCancelNotif);
        }
        _result.setPendingCancelNotif(_tmpPendingCancelNotif);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public UserEntity login(final String username, final String password) {
    final String _sql = "SELECT * FROM users WHERE username = ? AND password = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (username == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, username);
    }
    _argIndex = 2;
    if (password == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, password);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
      final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
      final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
      final int _cursorIndexOfMustChangePassword = CursorUtil.getColumnIndexOrThrow(_cursor, "must_change_password");
      final int _cursorIndexOfPendingCancelNotif = CursorUtil.getColumnIndexOrThrow(_cursor, "pending_cancel_notif");
      final UserEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new UserEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        _result.setName(_tmpName);
        final String _tmpUsername;
        if (_cursor.isNull(_cursorIndexOfUsername)) {
          _tmpUsername = null;
        } else {
          _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
        }
        _result.setUsername(_tmpUsername);
        final String _tmpPassword;
        if (_cursor.isNull(_cursorIndexOfPassword)) {
          _tmpPassword = null;
        } else {
          _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
        }
        _result.setPassword(_tmpPassword);
        final UserRole _tmpRole;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfRole)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfRole);
        }
        _tmpRole = Converters.toUserRole(_tmp);
        _result.setRole(_tmpRole);
        final boolean _tmpMustChangePassword;
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfMustChangePassword);
        _tmpMustChangePassword = _tmp_1 != 0;
        _result.setMustChangePassword(_tmpMustChangePassword);
        final String _tmpPendingCancelNotif;
        if (_cursor.isNull(_cursorIndexOfPendingCancelNotif)) {
          _tmpPendingCancelNotif = null;
        } else {
          _tmpPendingCancelNotif = _cursor.getString(_cursorIndexOfPendingCancelNotif);
        }
        _result.setPendingCancelNotif(_tmpPendingCancelNotif);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int countByUsername(final String username) {
    final String _sql = "SELECT COUNT(*) FROM users WHERE username = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (username == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, username);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
