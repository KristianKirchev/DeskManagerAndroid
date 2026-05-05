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
import com.deskmanager.app.data.entities.DeskEntity;
import com.deskmanager.app.domain.enums.DeskStatus;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class DeskDao_Impl implements DeskDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DeskEntity> __insertionAdapterOfDeskEntity;

  private final EntityDeletionOrUpdateAdapter<DeskEntity> __deletionAdapterOfDeskEntity;

  private final EntityDeletionOrUpdateAdapter<DeskEntity> __updateAdapterOfDeskEntity;

  public DeskDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDeskEntity = new EntityInsertionAdapter<DeskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `desks` (`id`,`room_number`,`floor`,`is_occupied`,`status`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final DeskEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getRoomNumber() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getRoomNumber());
        }
        statement.bindLong(3, entity.getFloor());
        final int _tmp = entity.isOccupied() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final String _tmp_1 = Converters.fromDeskStatus(entity.getStatus());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
      }
    };
    this.__deletionAdapterOfDeskEntity = new EntityDeletionOrUpdateAdapter<DeskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `desks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final DeskEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfDeskEntity = new EntityDeletionOrUpdateAdapter<DeskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `desks` SET `id` = ?,`room_number` = ?,`floor` = ?,`is_occupied` = ?,`status` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final DeskEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getRoomNumber() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getRoomNumber());
        }
        statement.bindLong(3, entity.getFloor());
        final int _tmp = entity.isOccupied() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final String _tmp_1 = Converters.fromDeskStatus(entity.getStatus());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
        statement.bindLong(6, entity.getId());
      }
    };
  }

  @Override
  public long insert(final DeskEntity desk) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfDeskEntity.insertAndReturnId(desk);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final DeskEntity desk) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfDeskEntity.handle(desk);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final DeskEntity desk) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfDeskEntity.handle(desk);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<DeskEntity>> getAllDesks() {
    final String _sql = "SELECT * FROM desks ORDER BY floor ASC, room_number ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"desks"}, false, new Callable<List<DeskEntity>>() {
      @Override
      @Nullable
      public List<DeskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoomNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "room_number");
          final int _cursorIndexOfFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "floor");
          final int _cursorIndexOfIsOccupied = CursorUtil.getColumnIndexOrThrow(_cursor, "is_occupied");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<DeskEntity> _result = new ArrayList<DeskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeskEntity _item;
            _item = new DeskEntity();
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpRoomNumber;
            if (_cursor.isNull(_cursorIndexOfRoomNumber)) {
              _tmpRoomNumber = null;
            } else {
              _tmpRoomNumber = _cursor.getString(_cursorIndexOfRoomNumber);
            }
            _item.setRoomNumber(_tmpRoomNumber);
            final int _tmpFloor;
            _tmpFloor = _cursor.getInt(_cursorIndexOfFloor);
            _item.setFloor(_tmpFloor);
            final boolean _tmpIsOccupied;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOccupied);
            _tmpIsOccupied = _tmp != 0;
            _item.setOccupied(_tmpIsOccupied);
            final DeskStatus _tmpStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = Converters.toDeskStatus(_tmp_1);
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
  public DeskEntity getDeskById(final int id) {
    final String _sql = "SELECT * FROM desks WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfRoomNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "room_number");
      final int _cursorIndexOfFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "floor");
      final int _cursorIndexOfIsOccupied = CursorUtil.getColumnIndexOrThrow(_cursor, "is_occupied");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final DeskEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new DeskEntity();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final String _tmpRoomNumber;
        if (_cursor.isNull(_cursorIndexOfRoomNumber)) {
          _tmpRoomNumber = null;
        } else {
          _tmpRoomNumber = _cursor.getString(_cursorIndexOfRoomNumber);
        }
        _result.setRoomNumber(_tmpRoomNumber);
        final int _tmpFloor;
        _tmpFloor = _cursor.getInt(_cursorIndexOfFloor);
        _result.setFloor(_tmpFloor);
        final boolean _tmpIsOccupied;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsOccupied);
        _tmpIsOccupied = _tmp != 0;
        _result.setOccupied(_tmpIsOccupied);
        final DeskStatus _tmpStatus;
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
        }
        _tmpStatus = Converters.toDeskStatus(_tmp_1);
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
  public LiveData<List<DeskEntity>> getDesksByStatus(final String status) {
    final String _sql = "SELECT * FROM desks WHERE status = ? ORDER BY floor ASC, room_number ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"desks"}, false, new Callable<List<DeskEntity>>() {
      @Override
      @Nullable
      public List<DeskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoomNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "room_number");
          final int _cursorIndexOfFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "floor");
          final int _cursorIndexOfIsOccupied = CursorUtil.getColumnIndexOrThrow(_cursor, "is_occupied");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<DeskEntity> _result = new ArrayList<DeskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeskEntity _item;
            _item = new DeskEntity();
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpRoomNumber;
            if (_cursor.isNull(_cursorIndexOfRoomNumber)) {
              _tmpRoomNumber = null;
            } else {
              _tmpRoomNumber = _cursor.getString(_cursorIndexOfRoomNumber);
            }
            _item.setRoomNumber(_tmpRoomNumber);
            final int _tmpFloor;
            _tmpFloor = _cursor.getInt(_cursorIndexOfFloor);
            _item.setFloor(_tmpFloor);
            final boolean _tmpIsOccupied;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOccupied);
            _tmpIsOccupied = _tmp != 0;
            _item.setOccupied(_tmpIsOccupied);
            final DeskStatus _tmpStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = Converters.toDeskStatus(_tmp_1);
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
  public LiveData<List<DeskEntity>> searchDesks(final String query) {
    final String _sql = "SELECT * FROM desks WHERE room_number LIKE '%' || ? || '%' OR CAST(floor AS TEXT) LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    _argIndex = 2;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"desks"}, false, new Callable<List<DeskEntity>>() {
      @Override
      @Nullable
      public List<DeskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoomNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "room_number");
          final int _cursorIndexOfFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "floor");
          final int _cursorIndexOfIsOccupied = CursorUtil.getColumnIndexOrThrow(_cursor, "is_occupied");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<DeskEntity> _result = new ArrayList<DeskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeskEntity _item;
            _item = new DeskEntity();
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpRoomNumber;
            if (_cursor.isNull(_cursorIndexOfRoomNumber)) {
              _tmpRoomNumber = null;
            } else {
              _tmpRoomNumber = _cursor.getString(_cursorIndexOfRoomNumber);
            }
            _item.setRoomNumber(_tmpRoomNumber);
            final int _tmpFloor;
            _tmpFloor = _cursor.getInt(_cursorIndexOfFloor);
            _item.setFloor(_tmpFloor);
            final boolean _tmpIsOccupied;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOccupied);
            _tmpIsOccupied = _tmp != 0;
            _item.setOccupied(_tmpIsOccupied);
            final DeskStatus _tmpStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = Converters.toDeskStatus(_tmp_1);
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
  public LiveData<List<DeskEntity>> getDesksForFloor(final int floor) {
    final String _sql = "SELECT * FROM desks WHERE floor = ? ORDER BY room_number ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, floor);
    return __db.getInvalidationTracker().createLiveData(new String[] {"desks"}, false, new Callable<List<DeskEntity>>() {
      @Override
      @Nullable
      public List<DeskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRoomNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "room_number");
          final int _cursorIndexOfFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "floor");
          final int _cursorIndexOfIsOccupied = CursorUtil.getColumnIndexOrThrow(_cursor, "is_occupied");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<DeskEntity> _result = new ArrayList<DeskEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeskEntity _item;
            _item = new DeskEntity();
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final String _tmpRoomNumber;
            if (_cursor.isNull(_cursorIndexOfRoomNumber)) {
              _tmpRoomNumber = null;
            } else {
              _tmpRoomNumber = _cursor.getString(_cursorIndexOfRoomNumber);
            }
            _item.setRoomNumber(_tmpRoomNumber);
            final int _tmpFloor;
            _tmpFloor = _cursor.getInt(_cursorIndexOfFloor);
            _item.setFloor(_tmpFloor);
            final boolean _tmpIsOccupied;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOccupied);
            _tmpIsOccupied = _tmp != 0;
            _item.setOccupied(_tmpIsOccupied);
            final DeskStatus _tmpStatus;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            }
            _tmpStatus = Converters.toDeskStatus(_tmp_1);
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
  public List<Integer> getDistinctFloorsSync() {
    final String _sql = "SELECT DISTINCT floor FROM desks ORDER BY floor ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final List<Integer> _result = new ArrayList<Integer>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Integer _item;
        if (_cursor.isNull(0)) {
          _item = null;
        } else {
          _item = _cursor.getInt(0);
        }
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
