package com.zing.ktkg_student.problem1
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val email: String,
    val phone: String,
    val birthDate: Date
)
