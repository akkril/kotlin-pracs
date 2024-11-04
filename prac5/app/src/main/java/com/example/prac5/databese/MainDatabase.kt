package com.example.prac5.databese

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.prac5.dao.TodoDao
import com.example.prac5.entity.TodosEntity

@Database(
    entities = [TodosEntity::class],
    version = 1
)
abstract class MainDatabase : RoomDatabase()
{
    abstract fun todoDao(): TodoDao
}