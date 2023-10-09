package com.github.truefmartin.homelist

import android.app.Application
import com.github.truefmartin.homelist.Model.TaskRepository
import com.github.truefmartin.homelist.Model.TaskRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class HomeMaintenanceList : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { TaskRoomDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { TaskRepository(database.taskDao()) }
}
