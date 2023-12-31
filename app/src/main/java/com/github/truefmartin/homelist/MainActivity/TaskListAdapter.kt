package com.github.truefmartin.homelist.MainActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.truefmartin.homelist.Model.Task
import com.github.truefmartin.homelist.R
import java.time.LocalDateTime

class TaskListAdapter(val taskClicked:(task:Task)->Unit): ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.title,current.date)
        holder.itemView.tag= current
        holder.itemView.setOnClickListener{
            taskClicked(holder.itemView.tag as Task)
        }
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskItemView: TextView = itemView.findViewById(R.id.title_text)
        private val dateTextView: TextView = itemView.findViewById(R.id.date_text)

        fun bind(text: String?, date:LocalDateTime?) {
            taskItemView.text = text
            dateTextView.text = date.toString()
        }
        companion object {
            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return TaskViewHolder(view)
            }
        }
    }

    class TasksComparator : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return (oldItem.title == newItem.title
                    && oldItem.date == newItem.date)
        }
    }
}
