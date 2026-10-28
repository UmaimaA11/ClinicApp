package com.clinic.app.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.clinic.app.data.entities.User;
import com.clinic.app.data.entities.UserRole;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM user WHERE id = :id")
    User getUser(int id);

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    User findByEmail(String email);

    @Query("SELECT * FROM user ORDER BY name ASC")
    List<User> getAllUsers();

    @Query("SELECT COUNT(*) FROM user WHERE role = :role")
    int countUsersByRole(UserRole role);
}
