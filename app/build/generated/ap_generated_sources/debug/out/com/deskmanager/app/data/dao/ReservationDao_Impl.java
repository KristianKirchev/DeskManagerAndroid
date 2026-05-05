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
import com.deskmanager.app.data.entities.ReservationEntity;
import com.deskmanager.app.domain.enums.ReservationStatus;
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
public final class ReservationDao_Impl implements ReservationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReservationEntity> __insertionAdapterOfReservationEntity;

  private final EntityDeletionOrUpdateAdapter<ReservationEntity> __deletionAdapterOfReservationEntity;

  private final EntityDeletionOrUpdateAdapter<ReservationEntity> __updateAdapterOfReservationEntity;

  public ReservationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReservationEntity = new EntityInsertionAdapter<ReservationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `reservations` (`id`,`user_id`,`desk_id`,`start_date`,`end_date`,`status`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ReservationEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindLong(3, entity.getDeskId());
        statement.bindLong(4, entity.getStartDate());
        statement.bindLong(5, entity.getEndDate());
        final String _tmp = Converters.fromReservationStatus(entity.getStatus());
        if (_tmp == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp);
        }
      }
    };
    this.__deletionAdapterOfReservationEntity = new EntityDeletionOrUpdateAdapter<ReservationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `reservations` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ReservationEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfReservationEntity = new EntityDeletionOrUpdateAdapter<ReservationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `reservations` SET `id` = ?,`user_id` = ?,`desk_id` = ?,`start_date` = ?,`end_date` = ?,`status` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ReservationEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindLong(3, entity.getDeskId());
        statement.bindLong(4, entity.getStartDate());
        statement.bindLong(5, entity.getEndDate());
        final String _tmp = Converters.fromReservationStatus(entity.getStatus());
        if (_tmp == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp);
        }
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public long insert(final ReservationEntity reservation) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfReservationEntity.insertAndReturnId(reservation);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final ReservationEntity reservation) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfReservationEntity.handle(reservation);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final ReservationEntity reservation) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfReservationEntity.handle(reservation);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<ReservationEntity>> getAllReservations() {
    final String _sql = "SELECT * FROM reservations ORDER BY start_date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"reservations"}, false, new Callable<List<ReservationEntity>>() {
      @Override
      @Nullable
      public List<ReservationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
          final int _cursorIndexOfDeskId = CursorUtil.getColumnIndexOrThrow(_cursor, "desk_id");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "start_date");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "end_date");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<ReservationEntity> _result = new ArrayList<ReservationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReservationEntity _item;
            _item = new ReservationEntity();
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            _item.setUserId(_tmpUserId);
            final int _tmpDeskId;
            _tmpDeskId = _cursor.getInt(_cursorIndexOfDeskId);
            _item.setDeskId(_tmpDeskId);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            _item.setStartDate(_tmpStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            _item.setEndDate(_tmpEndDate);
            final ReservationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = Converters.toReservationStatus(_tmp);
            _item.setStatus(_tmpStatus);
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
  public LiveData<List<ReservationEntity>> getReservationsByUser(final int userId) {
    final String _sql = "SELECT * FROM reservations WHERE user_id = ? ORDER BY start_date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"reservations"}, false, new Callable<List<ReservationEntity>>() {
      @Override
      @Nullable
      public List<ReservationEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
          final int _cursorIndexOfDeskId = CursorUtil.getColumnIndexOrThrow(_cursor, "desk_id");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "start_date");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "end_date");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<ReservationEntity> _result = new ArrayList<ReservationEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReservationEntity _item;
            _item = new ReservationEntity();
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            _item.setUserId(_tmpUserId);
            final int _tmpDeskId;
            _tmpDeskId = _cursor.getInt(_cursorIndexOfDeskId);
            _item.setDeskId(_tmpDeskId);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            _item.setStartDate(_tmpStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            _item.setEndDate(_tmpEndDate);
            final ReservationStatus _tmpStatus;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = Converters.toReservationStatus(_tmp);
            _item.setStatus(_tmpStatus);
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
  public ReservationEntity getReservationById(final int id) {
    final String _sql = "SELECT * FROM reservations WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
      final int _cursorIndexOfDeskId = CursorUtil.getColumnIndexOrThrow(_cursor, "desk_id");
      final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "start_date");
      final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "end_date");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final ReservationEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new ReservationEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final int _tmpUserId;
        _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
        _result.setUserId(_tmpUserId);
        final int _tmpDeskId;
        _tmpDeskId = _cursor.getInt(_cursorIndexOfDeskId);
        _result.setDeskId(_tmpDeskId);
        final long _tmpStartDate;
        _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
        _result.setStartDate(_tmpStartDate);
        final long _tmpEndDate;
        _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
        _result.setEndDate(_tmpEndDate);
        final ReservationStatus _tmpStatus;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfStatus);
        }
        _tmpStatus = Converters.toReservationStatus(_tmp);
        _result.setStatus(_tmpStatus);
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
  public int countOverlapping(final int deskId, final long startDate, final long endDate) {
    final String _sql = "SELECT COUNT(*) FROM reservations WHERE desk_id = ? AND status = 'ACTIVE' AND start_date < ? AND end_date > ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, deskId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    _argIndex = 3;
    _statement.bindLong(_argIndex, startDate);
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

  @Override
  public List<ReservationEntity> getActiveReservationsForDesk(final int deskId) {
    final String _sql = "SELECT * FROM reservations WHERE desk_id = ? AND status = 'ACTIVE' ORDER BY start_date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, deskId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
      final int _cursorIndexOfDeskId = CursorUtil.getColumnIndexOrThrow(_cursor, "desk_id");
      final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "start_date");
      final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "end_date");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final List<ReservationEntity> _result = new ArrayList<ReservationEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ReservationEntity _item;
        _item = new ReservationEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final int _tmpUserId;
        _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
        _item.setUserId(_tmpUserId);
        final int _tmpDeskId;
        _tmpDeskId = _cursor.getInt(_cursorIndexOfDeskId);
        _item.setDeskId(_tmpDeskId);
        final long _tmpStartDate;
        _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
        _item.setStartDate(_tmpStartDate);
        final long _tmpEndDate;
        _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
        _item.setEndDate(_tmpEndDate);
        final ReservationStatus _tmpStatus;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfStatus);
        }
        _tmpStatus = Converters.toReservationStatus(_tmp);
        _item.setStatus(_tmpStatus);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public ReservationEntity getActiveReservationForUserOnDesk(final int deskId, final int userId) {
    final String _sql = "SELECT * FROM reservations WHERE desk_id = ? AND user_id = ? AND status = 'ACTIVE' LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, deskId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, userId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
      final int _cursorIndexOfDeskId = CursorUtil.getColumnIndexOrThrow(_cursor, "desk_id");
      final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "start_date");
      final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "end_date");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final ReservationEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new ReservationEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final int _tmpUserId;
        _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
        _result.setUserId(_tmpUserId);
        final int _tmpDeskId;
        _tmpDeskId = _cursor.getInt(_cursorIndexOfDeskId);
        _result.setDeskId(_tmpDeskId);
        final long _tmpStartDate;
        _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
        _result.setStartDate(_tmpStartDate);
        final long _tmpEndDate;
        _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
        _result.setEndDate(_tmpEndDate);
        final ReservationStatus _tmpStatus;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfStatus);
        }
        _tmpStatus = Converters.toReservationStatus(_tmp);
        _result.setStatus(_tmpStatus);
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
  public int countActiveForUser(final int userId) {
    final String _sql = "SELECT COUNT(*) FROM reservations WHERE user_id = ? AND status = 'ACTIVE'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
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

  @Override
  public List<ReservationEntity> getActiveReservationsByUserSync(final int userId) {
    final String _sql = "SELECT * FROM reservations WHERE user_id = ? AND status = 'ACTIVE'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
      final int _cursorIndexOfDeskId = CursorUtil.getColumnIndexOrThrow(_cursor, "desk_id");
      final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "start_date");
      final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "end_date");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final List<ReservationEntity> _result = new ArrayList<ReservationEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ReservationEntity _item;
        _item = new ReservationEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final int _tmpUserId;
        _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
        _item.setUserId(_tmpUserId);
        final int _tmpDeskId;
        _tmpDeskId = _cursor.getInt(_cursorIndexOfDeskId);
        _item.setDeskId(_tmpDeskId);
        final long _tmpStartDate;
        _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
        _item.setStartDate(_tmpStartDate);
        final long _tmpEndDate;
        _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
        _item.setEndDate(_tmpEndDate);
        final ReservationStatus _tmpStatus;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfStatus);
        }
        _tmpStatus = Converters.toReservationStatus(_tmp);
        _item.setStatus(_tmpStatus);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ReservationEntity> getExpiredActiveReservations(final long nowMillis) {
    final String _sql = "SELECT * FROM reservations WHERE status = 'ACTIVE' AND end_date < ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, nowMillis);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
      final int _cursorIndexOfDeskId = CursorUtil.getColumnIndexOrThrow(_cursor, "desk_id");
      final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "start_date");
      final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "end_date");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final List<ReservationEntity> _result = new ArrayList<ReservationEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ReservationEntity _item;
        _item = new ReservationEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final int _tmpUserId;
        _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
        _item.setUserId(_tmpUserId);
        final int _tmpDeskId;
        _tmpDeskId = _cursor.getInt(_cursorIndexOfDeskId);
        _item.setDeskId(_tmpDeskId);
        final long _tmpStartDate;
        _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
        _item.setStartDate(_tmpStartDate);
        final long _tmpEndDate;
        _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
        _item.setEndDate(_tmpEndDate);
        final ReservationStatus _tmpStatus;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfStatus);
        }
        _tmpStatus = Converters.toReservationStatus(_tmp);
        _item.setStatus(_tmpStatus);
        _result.add(_item);
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
