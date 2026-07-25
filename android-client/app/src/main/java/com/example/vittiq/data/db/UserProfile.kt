package com.example.vittiq.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val fullName: String,
    val photoUri: String? = null
)
