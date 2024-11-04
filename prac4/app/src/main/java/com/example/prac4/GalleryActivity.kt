package com.example.prac4

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prac4.databinding.ActivityGalleryBinding
import java.io.File

// Активность, отображающая галерею с использованием RecyclerView
class GalleryActivity : AppCompatActivity() {

    // Переменная для хранения привязки к разметке ActivityGalleryBinding
    private lateinit var viewBinding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация привязки к разметке и установка корневого представления
        viewBinding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        try {
            // Определяем папку для хранения фотографий
            val photosDir = File(baseContext.filesDir, "photos")
            // Если папка не существует, создаем её
            if (!photosDir.exists()) {
                photosDir.mkdir()
            }

            // Определяем файл с датами фотографий
            val dateFile = File(photosDir, "date.txt")
            // Если файл не существует, создаем его и выводим сообщение
            if (!dateFile.exists()) {
                dateFile.createNewFile()
                Toast.makeText(this, "Файла с данными не было. Файл создан", Toast.LENGTH_SHORT).show()
                return
            } else {
                // Читаем строки из файла в список
                val dataList = dateFile.readLines().toMutableList()

                // Если данные отсутствуют, выводим сообщение
                if (dataList.isEmpty()) {
                    Toast.makeText(this, "Данных нет", Toast.LENGTH_SHORT).show()
                    return
                }

                // Инициализируем RecyclerView и задаем для него адаптер
                val recyclerView = viewBinding.recyclerView
                val adapter = DataAdapter(dataList)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this)
            }

        } catch (e: Exception) {
            // Обрабатываем возможные исключения и выводим информацию об ошибке
            e.printStackTrace()
        }
    }
}
