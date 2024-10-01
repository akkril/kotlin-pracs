package com.example.prac3

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Главная активность приложения
class MainActivity : AppCompatActivity() {
    // Метод, вызываемый при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Вызываем метод родительского класса

        // Включаем режим "от края до края" для отображения контента под системными панелями
        enableEdgeToEdge()

        // Устанавливаем контент для активности из XML-файла
        setContentView(R.layout.activity_main)

        // Устанавливаем слушателя для обработки изменения отступов окна
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // Получаем отступы системных панелей
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Устанавливаем отступы для представления, чтобы контент не перекрывался панелями
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets // Возвращаем insets для дальнейшей обработки
        }
    }
}
