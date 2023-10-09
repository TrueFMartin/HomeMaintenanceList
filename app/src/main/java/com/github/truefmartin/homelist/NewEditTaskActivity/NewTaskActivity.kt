package com.github.truefmartin.homelist.NewEditTaskActivity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import com.github.truefmartin.homelist.Model.Task
import com.github.truefmartin.homelist.R
import com.github.truefmartin.homelist.HomeMaintenanceList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

const val EXTRA_ID:String = "com.github.truefmartin.NewTaskActivity.EXTRA_ID"
class NewTaskActivity : AppCompatActivity() {

    private lateinit var editTaskView: EditText
    private lateinit var editBodyView: EditText
    private lateinit var tvDateView: TextView

    private lateinit var tvTimeView: TextView
    private lateinit var btnSetDate: Button
    private lateinit var btnSetTime: Button

    private var newDate: LocalDateTime = LocalDateTime.now()
    private var newTime: LocalDateTime = LocalDateTime.now()

    private lateinit var spinner: Spinner

    private var recurringVal: RecurringState = RecurringState.ONCE
    private val newTaskViewModel: NewTaskViewModel by viewModels {
        NewTaskViewModelFactory((application as HomeMaintenanceList).repository,-1)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)
        editTaskView = findViewById(R.id.edit_task_body)

        val id = intent.getIntExtra(EXTRA_ID,-1)
        if(id != -1){
            newTaskViewModel.updateId(id)
        }
        newTaskViewModel.curTask.observe(this){
            task->task?.let { editTaskView.setText(task.title)}
        }

        tvDateView = findViewById(R.id.text_view_date)
        tvTimeView = findViewById(R.id.text_view_time)

        btnSetTime = findViewById(R.id.btn_pick_time)
        btnSetTime.setOnClickListener {
            TimePickerFragment { t: LocalDateTime -> setTime(t) }.show(supportFragmentManager, "timePicker")
        }

        btnSetDate = findViewById<Button>(R.id.btn_pick_date)
        btnSetDate.setOnClickListener {
            val newFragment = DatePickerFragment{ d: LocalDateTime -> setDate(d) }
            newFragment.show(supportFragmentManager, "datePicker")
        }

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentTime = formatter.format(time)
        spinner = findViewById(R.id.spinner_recurring)
        val r = {a: String -> setRecurring(a)}
        RecurringSpinner(this, spinner, r)
        // Create an ArrayAdapter using the string array and a default spinner layout.
//        ArrayAdapter.createFromResource(
//            this,
//            R.array.recurring_options,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            // Specify the layout to use when the list of choices appears.
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            // Apply the adapter to the spinner.
//            spinner.adapter = adapter
//        }
//        spinner.onItemSelectedListener = SpinnerActivity()

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            CoroutineScope(SupervisorJob()).launch {
                if(id==-1) {
                    newTaskViewModel.insert(
                        Task(null, editTaskView.text.toString(),"body",
                            combineDateTime(),0, recurringVal))
                }else{
                    val updatedTask = newTaskViewModel.curTask.value
                    if (updatedTask != null) {
                        updatedTask.title = editTaskView.text.toString()
                        newTaskViewModel.update(updatedTask)
                    }

                }
            }

            setResult(RESULT_OK)
            finish()
        }
    }


    private fun setRecurring(s: String){
        Log.d("NewTaskActivity", "setting recurring to $s")
        recurringVal = RecurringState.valueOf(s.uppercase())
    }

    private fun setDate(time: LocalDateTime) {
        newDate = time
        // TODO set date view too
    }
    private fun setTime(time: LocalDateTime) {
        newTime = time // FIXME prints as 6:3 for 6:03
        val tempStr: String = newTime.hour.toString() + ":" + newTime.minute.toString()
        tvTimeView.text = tempStr
    }

    private fun combineDateTime(): LocalDateTime {
        return LocalDateTime.of(
            newDate.year,
            newDate.month,
            newDate.dayOfMonth,
            newTime.hour,
            newTime.minute
        )
    }
    class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            // An item is selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos).
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback.
        }
    }
}
