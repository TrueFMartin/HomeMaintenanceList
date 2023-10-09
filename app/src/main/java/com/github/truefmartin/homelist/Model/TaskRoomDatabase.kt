package com.github.truefmartin.homelist.Model

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.truefmartin.homelist.NewEditTaskActivity.RecurringState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.TemporalAccessor

@Database(entities = arrayOf(Task::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TaskRoomDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    private class TaskDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("Database","Here1")

            INSTANCE?.let { database ->
                scope.launch {
                    val taskDao = database.taskDao()

                    // Delete all content here.
                    taskDao.deleteAll()
                    // Add sample words.
                    var task = Task(null,"Clean","Clean bathroom", LocalDateTime.now().plusDays(1), 0, RecurringState.MONTHLY)
                    taskDao.insert(task)
                    task = Task(null,"Fix Sink","Need hammer", LocalDateTime.now(), 0, RecurringState.ONCE)
                    taskDao.insert(task)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TaskRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): TaskRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskRoomDatabase::class.java,
                    "task_database"
                )
                    .addCallback(TaskDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

