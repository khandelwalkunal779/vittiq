package com.vittiq.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val firstName: String,
    val lastName: String,
    val handle: String,
    val avatarInitial: String
)

