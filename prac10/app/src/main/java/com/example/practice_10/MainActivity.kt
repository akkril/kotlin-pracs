package com.example.practice_10

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : ComponentActivity() {
    private var imageUrl: String by mutableStateOf("")
    private var bitmap: Bitmap? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(
                imageUrl = imageUrl,
                bitmap = bitmap,
                onImageUrlChange = { newUrl -> imageUrl = newUrl },
                onDownloadClick = {
                    lifecycleScope.launch {
                        val downloadedBitmap = downloadAndSaveImage(imageUrl)
                        if (downloadedBitmap != null) {
                            bitmap = downloadedBitmap
                        } else {
                            showToast("Ошибка загрузки изображения")
                        }
                    }
                },
                onShowImagesClick = { goToImageListScreen() }
            )
        }
    }

    private fun goToImageListScreen() {
        setContent {
            ImageListScreen(context = this)
        }
    }

    private suspend fun downloadAndSaveImage(imageUrl: String): Bitmap? {
        val bitmap = withContext(Dispatchers.IO) {
            downloadImage(imageUrl)
        }

        if (bitmap != null) {
            withContext(Dispatchers.IO) {
                saveImageToDisk(bitmap)
            }
        }

        return bitmap
    }

    private fun downloadImage(imageUrl: String): Bitmap? = runCatching {
        val connection = URL(imageUrl).openConnection()
        connection.doInput = true
        val input = connection.getInputStream()
        BitmapFactory.decodeStream(input)
    }.getOrNull()

    private fun saveImageToDisk(bitmap: Bitmap) {
        runCatching {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "${imageUrl.first()} + ${imageUrl.length}.jpg")
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
            }
        }.onFailure {
            showToast("Ошибка сохранения изображения")
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun MainScreen(
    imageUrl: String,
    bitmap: Bitmap?,
    onImageUrlChange: (String) -> Unit,
    onDownloadClick: () -> Unit,
    onShowImagesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BasicTextField(
            value = TextFieldValue(imageUrl),
            onValueChange = { onImageUrlChange(it.text) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Button(
            onClick = onDownloadClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Скачать изображение")
        }

        Button(
            onClick = onShowImagesClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Посмотреть сохраненные изображения")
        }

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Downloaded Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}

@Composable
fun ImageListScreen(context: Context) {
    val imageFiles = getSavedImages(context)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(imageFiles) { file ->
            ImageItem(file)
        }
    }
}

@Composable
fun ImageItem(file: File) {
    val bitmap =  BitmapFactory.decodeFile(file.absolutePath)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Saved Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        } ?: Text("Ошибка загрузки изображения")
    }
}

private fun getSavedImages(context: Context): List<File> {
    val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return directory?.listFiles { _, name -> name.endsWith(".jpg") }?.toList() ?: emptyList()
}

