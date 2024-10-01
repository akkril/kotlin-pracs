package com.example.prac3.ui.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.prac3.data.repository.TimerRepository

// ViewModel для управления состоянием таймера
class TimerViewModel(private val repository: TimerRepository) : ViewModel() {

    // Хранит оставшееся время в миллисекундах
    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> get() = _timeLeft // Объявляем LiveData для наблюдения

    // Хранит состояние таймера (запущен или нет)
    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> get() = _isRunning // Объявляем LiveData для наблюдения

    // Инициализация значений при создании ViewModel
    init {
        _timeLeft.value = repository.getTime() // Получаем текущее оставшееся время из репозитория
        _isRunning.value = repository.isRunning() // Получаем текущее состояние таймера из репозитория
    }

    // Метод для запуска таймера
    fun startTimer(timeInMillis: Long) {
        repository.startTimer(timeInMillis) // Инициализируем таймер в репозитории
        _isRunning.value = true // Устанавливаем состояние таймера как запущенный
        startCountdown() // Начинаем обратный отсчет
    }

    // Метод для остановки таймера
    fun stopTimer() {
        repository.stopTimer() // Останавливаем таймер в репозитории
        _isRunning.value = false // Устанавливаем состояние таймера как не запущенный
    }

    // Метод для обратного отсчета времени
    private fun startCountdown() {
        viewModelScope.launch { // Запускаем корутину в рамках ViewModel
            while (repository.isRunning()) { // Продолжаем, пока таймер запущен
                delay(1000L) // Интервал 1 секунда
                repository.decreaseTime(1000L) // Уменьшаем время в репозитории на 1 секунду
                _timeLeft.postValue(repository.getTime()) // Обновляем оставшееся время для наблюдателей
            }
        }
    }
}
