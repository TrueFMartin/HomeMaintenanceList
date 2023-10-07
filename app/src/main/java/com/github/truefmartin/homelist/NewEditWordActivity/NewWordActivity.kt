package com.github.truefmartin.homelist.NewEditWordActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import com.github.truefmartin.homelist.Model.Word
import com.github.truefmartin.homelist.R
import com.github.truefmartin.homelist.HomeMaintenanceList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

const val EXTRA_ID:String = "com.github.truefmartin.NewWordActivity.EXTRA_ID"
class NewWordActivity : AppCompatActivity() {

    private lateinit var editWordView: EditText

    private val newWordViewModel: NewWordViewModel by viewModels {
        NewWordViewModelFactory((application as HomeMaintenanceList).repository,-1)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_word)
        editWordView = findViewById(R.id.edit_word)

        val id = intent.getIntExtra(EXTRA_ID,-1)
        if(id != -1){
            newWordViewModel.updateId(id)
        }
        newWordViewModel.curWord.observe(this){
            word->word?.let { editWordView.setText(word.word)}
        }

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            CoroutineScope(SupervisorJob()).launch {
                if(id==-1) {
                    newWordViewModel.insert(
                        Word(null, editWordView.text.toString(),0)
                    )
                }else{
                    val updatedWord = newWordViewModel.curWord.value
                    if (updatedWord != null) {
                        updatedWord.word = editWordView.text.toString()
                        newWordViewModel.update(updatedWord)
                    }

                }
            }

            setResult(RESULT_OK)
            finish()
        }
    }
}
