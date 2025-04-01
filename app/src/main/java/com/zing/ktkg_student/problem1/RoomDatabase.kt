package com.zing.ktkg_student.problem1
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
@Database(entities = [Customer::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CustomerDb : RoomDatabase() {
    abstract fun customerDao(): CustomerDao

    companion object {
        @Volatile
        private var INSTANCE: CustomerDb? = null

        fun getDatabase(context: Context): CustomerDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CustomerDb::class.java,
                    "CustomerDb"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

