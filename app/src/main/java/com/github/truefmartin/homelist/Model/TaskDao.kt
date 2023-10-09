package com.github.truefmartin.homelist.Model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    //Get all words alphabetized
    @Query("SELECT * FROM task_table ORDER BY date DESC, title DESC")
    fun getTimeSortedTasks(): Flow<List<Task>>

    //Get a single word with a given id
    @Query("SELECT * FROM task_table WHERE id=:id")
    fun getTask(id:Int): Flow<Task>

    //Get a single word with a given id
    @Query("SELECT * FROM task_table WHERE id=:id")
    fun getTaskNotLive(id:Int): Task

    //Insert a single task
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    //Delete all words
    @Query("DELETE FROM task_table")
    suspend fun deleteAll()

    //Update a single task
    @Update
    suspend fun update(task: Task):Int
}
