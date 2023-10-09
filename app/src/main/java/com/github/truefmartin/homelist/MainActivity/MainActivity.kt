package com.github.truefmartin.homelist.MainActivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.truefmartin.MainActivity.TaskListViewModel
import com.github.truefmartin.MainActivity.TaskListViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.github.truefmartin.homelist.NewEditTaskActivity.EXTRA_ID
import com.github.truefmartin.homelist.NewEditTaskActivity.NewTaskActivity
import com.github.truefmartin.homelist.R
import com.github.truefmartin.homelist.HomeMaintenanceList
import com.github.truefmartin.homelist.MyReceiver
import com.github.truefmartin.homelist.NotificationUtil
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainActivity : AppCompatActivity() {

    private var notificationPermissionGranted = false
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                NotificationUtil().createNotificationChannel(this)
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }
    //This is our viewModel instance for the MainActivity class
    private val taskListViewModel: TaskListViewModel by viewModels {
        TaskListViewModelFactory((application as HomeMaintenanceList).repository)
    }
    //This is our ActivityResultContracts value that defines
    //the behavior of our application when the activity has finished.
    val startNewTaskActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        if(result.resultCode== Activity.RESULT_OK){
            //Note that all we are doing is logging that we completed
            //This means that the other activity is handling updates to the data
            Log.d("MainActivity","Completed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionGranted = true
            NotificationUtil().createNotificationChannel(this)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = TaskListAdapter {
            //This is the callback function to be executed
            //when a view in the TaskListAdapter is clicked

            //First we log the word
            Log.d("MainActivity",it.title)
            //Then create a new intent with the ID of the word
            val intent = Intent(this@MainActivity, NewTaskActivity::class.java)
            intent.putExtra(EXTRA_ID,it.id)
            //And start the activity through the results contract
            startNewTaskActivity.launch(intent)

            val receiverIntent = Intent(this@MainActivity, MyReceiver::class.java)
            receiverIntent.putExtra(EXTRA_ID,it.id)
            sendBroadcast(receiverIntent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        taskListViewModel.allTasks.observe( this) { words ->
            // Update the cached copy of the words in the adapter.
            words.let {
                adapter.submitList(it)
                if(it.isNotEmpty()) {
                    it[0].id?.let { it1 ->
                        NotificationUtil().createClickableNotification(
                            this,
                            it[0].title,
                            it[0].date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)),
                            Intent(this@MainActivity, NewTaskActivity::class.java),
                            it1
                        )
                    }
                }

            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewTaskActivity::class.java)
            startNewTaskActivity.launch(intent)
        }
    }
}
