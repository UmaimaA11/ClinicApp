package com.clinic.app.data.converters;

import androidx.room.TypeConverter;
import com.clinic.app.data.entities.UserRole;

public class RoleConverter {

    @TypeConverter
    public static String fromUserRole(UserRole role) {
        return role == null ? null : role.name();
    }

    @TypeConverter
    public static UserRole toUserRole(String role) {
        return role == null ? null : UserRole.valueOf(role);
    }
}
