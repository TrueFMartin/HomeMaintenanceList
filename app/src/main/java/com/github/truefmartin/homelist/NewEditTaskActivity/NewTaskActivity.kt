package com.github.truefmartin.homelist.NewEditTaskActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import com.github.truefmartin.homelist.Model.Task
import com.github.truefmartin.homelist.R
import com.github.truefmartin.homelist.HomeMaintenanceList
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

const val EXTRA_ID:String = "com.github.truefmartin.NewTaskActivity.EXTRA_ID"
class NewTaskActivity : AppCompatActivity() {

    private lateinit var spinnerPopupWindow: Spinner
    private lateinit var etTaskTitle: EditText
    private lateinit var etTaskBody: EditText
    private lateinit var tvDateView: TextView
    private lateinit var tvTimeView: TextView

    private lateinit var btnSetDate: Button
    private lateinit var btnSetTime: Button
    private var newDate: LocalDateTime = LocalDateTime.now()

    private var newTime: LocalDateTime = LocalDateTime.now()

    private val formatterDate = DateTimeFormatter.ofPattern("M dd, yyyy", Locale.getDefault())
    private val formatterTime = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

    private var recurringVal: RecurringState = RecurringState.NONE

    private lateinit var spinner: Spinner

    private var isComplete = false;
    private val newTaskViewModel: NewTaskViewModel by viewModels {
        NewTaskViewModelFactory((application as HomeMaintenanceList).repository,-1)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)
        etTaskTitle = findViewById(R.id.edit_task_title)
        etTaskBody = findViewById(R.id.edit_task_body)

        val id = intent.getIntExtra(EXTRA_ID,-1)
        if(id != -1){
            newTaskViewModel.updateId(id)
        }

        newTaskViewModel.curTask.observe(this){
            task->task?.let { etTaskTitle.setText(task.title)}
        }

        tvDateView = findViewById(R.id.text_view_date)
        tvTimeView = findViewById(R.id.text_view_time)

        val time = LocalDateTime.now()
        tvDateView.text = time.format(formatterDate)
        tvTimeView.text = time.format(formatterTime)

        btnSetTime = findViewById(R.id.btn_pick_time)
        btnSetTime.setOnClickListener {
            TimePickerFragment { t: LocalDateTime -> setTime(t) }.show(supportFragmentManager, "timePicker")
        }

        btnSetDate = findViewById<Button>(R.id.btn_pick_date)
        btnSetDate.setOnClickListener {
            val newFragment = DatePickerFragment{ d: LocalDateTime -> setDate(d) }
            newFragment.show(supportFragmentManager, "datePicker")
        }

        // Pass call back function for spinner that selects if the task is recurring
        spinner = findViewById(R.id.spinner_recurring)
        RecurringSpinner(this, spinner) { a: String -> setRecurring(a) }

        // Set the listener for toggling of the 'is task complete' switch
        findViewById<SwitchMaterial>(R.id.switch_is_completed).setOnCheckedChangeListener {
                _, isToggled -> setComplete(isToggled) }
        // Set the on click function when user hits 'save'. Add or Update task in DB
        findViewById<Button>(R.id.button_save).setOnClickListener { onSaveClick(id) }

        findViewById<Button>(R.id.button_delete).setOnClickListener { ConfirmDelete{ _ -> deleteTask(id) } }

    }


    private fun setDate(date: LocalDateTime) {
        newDate = date
        val selectedDateTime = newDate.toLocalDate().format(formatterDate)
        tvDateView.text = selectedDateTime
    }

    private fun setTime(time: LocalDateTime) {
        newTime = time
        val selectedDateTime = newTime.toLocalTime().format(formatterTime)
        tvTimeView.text = selectedDateTime
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

    private fun setRecurring(s: String){
        Log.d("NewTaskActivity", "setting recurring to $s")
        recurringVal = RecurringState.valueOf(s.uppercase())
    }

    private fun setComplete(isToggled: Boolean) {
        isComplete = isToggled
    }

    private fun onSaveClick(id: Int) {
        CoroutineScope(SupervisorJob()).launch {
            if(id==-1) {
                newTaskViewModel.insert(
                    Task(null, etTaskTitle.text.toString(),etTaskBody.text.toString(),
                        combineDateTime(), isComplete, recurringVal))
            }else{
                val updatedTask = newTaskViewModel.curTask.value
                if (updatedTask != null) {
                    updatedTask.title = etTaskTitle.text.toString()
                    updatedTask.body = etTaskBody.text.toString()
                    updatedTask.date = combineDateTime()
                    updatedTask.repeated = recurringVal
                    updatedTask.completed = isComplete
                    newTaskViewModel.update(updatedTask)
                }

            }
        }

        setResult(RESULT_OK)
        finish()
    }

    private fun deleteTask(id: Int) {
        if (id == -1) {
            setResult(RESULT_CANCELED)
            finish()
        } else {
            CoroutineScope(SupervisorJob()).launch {
                newTaskViewModel.curTask.value?.let { newTaskViewModel.deleteTask(it) }
            }
            setResult(RESULT_OK)
            finish()
        }

    }
}
