package com.example.prac3.data.model

// Класс TimerModel представляет модель данных для таймера
data class TimerModel(
    // Время в миллисекундах, которое хранится для таймера
    var timeInMillis: Long = 0L,
    // Флаг, указывающий, запущен ли таймер в данный момент
    var isRunning: Boolean = false
)
