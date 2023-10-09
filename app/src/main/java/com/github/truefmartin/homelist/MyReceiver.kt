package com.github.truefmartin.homelist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.truefmartin.homelist.Model.Task
import com.github.truefmartin.homelist.NewEditTaskActivity.EXTRA_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(EXTRA_ID,-1)
        Log.d("MyReceiver","Broadcast Received $id")

        val repository = (context.applicationContext as HomeMaintenanceList).repository
        CoroutineScope(SupervisorJob()).launch {
            val task: Task = repository.getTaskNotLive(id)
            Log.d("MyReceiver", "Task is ${task.title} with date ${task.date}")
        }

    }
}