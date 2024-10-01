package com.example.prac3.data.repository

import com.example.prac3.data.model.TimerModel

// Класс TimerRepository отвечает за управление данными таймера
class TimerRepository {

    // Переменная для хранения состояния таймера
    private var timerModel = TimerModel()

    // Метод для получения текущего времени таймера в миллисекундах
    fun getTime(): Long {
        return timerModel.timeInMillis
    }

    // Метод для проверки, работает ли таймер в данный момент
    fun isRunning(): Boolean {
        return timerModel.isRunning
    }

    // Метод для запуска таймера с заданным временем
    fun startTimer(timeInMillis: Long) {
        timerModel.timeInMillis = timeInMillis
        timerModel.isRunning = true  // Устанавливаем флаг, что таймер работает
    }

    // Метод для остановки таймера
    fun stopTimer() {
        timerModel.isRunning = false  // Устанавливаем флаг, что таймер остановлен
    }

    // Метод для уменьшения времени таймера на определенный интервал
    fun decreaseTime(interval: Long) {
        // Уменьшаем время, только если таймер работает
        if (timerModel.isRunning) {
            timerModel.timeInMillis -= interval
            // Если время таймера меньше или равно 0, останавливаем таймер
            if (timerModel.timeInMillis <= 0L) {
                timerModel.isRunning = false
                timerModel.timeInMillis = 0L  // Сбрасываем таймер на 0
            }
        }
    }
}
