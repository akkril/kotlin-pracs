package com.example.prac4

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import com.example.prac4.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Запрашиваем разрешения для работы с камерой
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        // Устанавливаем обработчики для кнопок
        viewBinding.btnCaptureImage.setOnClickListener { takePhoto() }
        viewBinding.btnGallery.setOnClickListener { openGallery() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        // Сохраняем текущую дату и время в файл
        val dateFormat = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault())
        val date = dateFormat.format(System.currentTimeMillis())

        try {
            val photosDir = File(baseContext.filesDir, "photos")
            if (!photosDir.exists()) {
                photosDir.mkdir() // Создаем папку, если она не существует
            }
            val dateFile = File(photosDir, "date.txt")
            if (!dateFile.exists()) {
                dateFile.createNewFile() // Создаем файл, если его нет
            }
            // Записываем дату в файл
            dateFile.appendText("$date\n")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun openGallery() {
        // Открываем активность с галереей
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll() // Отвязываем предыдущие случаи использования камеры
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture) // Привязываем к жизненному циклу
            } catch (exc: Exception) {
                Log.e(TAG, "Не удалось привязать use case", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown() // Останавливаем поток выполнения
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS" // Формат для сохранения даты и времени
        private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.CAMERA).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray() // Массив необходимых разрешений
    }

    // Запрос разрешений через ActivityResultLauncher
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && it.value == false) permissionGranted = false
        }
        if (!permissionGranted) {
            Toast.makeText(baseContext, "Запрос на разрешение отклонен", Toast.LENGTH_SHORT).show()
        } else {
            startCamera() // Если разрешения выданы, запускаем камеру
        }
    }
}
