package com.example.prac3.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prac3.data.repository.TimerRepository

// Фабрика для создания экземпляров TimerViewModel
class TimerViewModelFactory(private val repository: TimerRepository) : ViewModelProvider.Factory {
    // Метод для создания ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Проверяем, является ли запрашиваемый класс TimerViewModel
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // Подавляем предупреждение о привидении типов
            return TimerViewModel(repository) as T // Возвращаем новый экземпляр TimerViewModel
        }
        // Вызываем исключение, если запрашиваемый класс не является TimerViewModel
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
