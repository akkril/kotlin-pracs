package com.example.prac2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Класс MainActivity представляет основную активность приложения
class MainActivity : AppCompatActivity() {
    // Метод вызывается при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Включаем поддержку "Edge-to-Edge" для отображения контента под системными элементами, такими как статус-бар и навигационная панель
        enableEdgeToEdge()
        // Устанавливаем макет активности
        setContentView(R.layout.activity_main)

        // Устанавливаем слушатель для обработки изменения оконных вставок (WindowInsets), чтобы контент не был перекрыт системными элементами
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // Получаем размеры системных панелей (статус-бар, навигационная панель)
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Устанавливаем отступы у корневого представления в соответствии с размерами системных панелей
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            // Возвращаем исходные вставки
            insets
        }
    }
}
