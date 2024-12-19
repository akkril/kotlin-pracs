package com.example.practice_10_12


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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : ComponentActivity() {
    private var currentScreen by mutableStateOf("DownloadImageScreen")
    private var imageUrl: String = ""
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(
                context = this,  // Передаем context сюда
                currentScreen = currentScreen,
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
                onScreenChange = { screen -> currentScreen = screen },
                onDeleteImagesClick = { startDeleteImagesWorker() } // Обработчик для кнопки удаления изображений
            )
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

    private fun startDeleteImagesWorker() {
        val deleteWorkRequest = OneTimeWorkRequestBuilder<DeleteImagesWorker>()
            .build()

        WorkManager.getInstance(this).enqueue(deleteWorkRequest)
    }
}

class DeleteImagesWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        return try {
            deleteImages()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun deleteImages() {
        val directory = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFiles = directory?.listFiles { _, name -> name.endsWith(".jpg") }

        imageFiles?.forEach { file ->
            file.delete()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    context: Context,
    currentScreen: String,
    imageUrl: String,
    bitmap: Bitmap?,
    onImageUrlChange: (String) -> Unit,
    onDownloadClick: () -> Unit,
    onScreenChange: (String) -> Unit,
    onDeleteImagesClick: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column {
                    Text("Drawer title", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

                    TextButton(onClick = {
                        onScreenChange("DownloadImageScreen")
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    }) {
                        Text("Загрузить изображение")
                    }

                    TextButton(onClick = {
                        onScreenChange("ImageListScreen")
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    }) {
                        Text("Сохранённые изображения")
                    }
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(currentScreen) },
                        navigationIcon = {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    if (drawerState.isOpen) drawerState.close() else drawerState.open()
                                }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomAppBar {
                        Button(onClick = { onScreenChange("DownloadImageScreen") }) {
                            Text("Загрузить изображение")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = { onScreenChange("ImageListScreen") }) {
                            Text("Сохранённые изображения")
                        }
                    }
                }
            ) { paddingValues ->
                when (currentScreen) {
                    "DownloadImageScreen" -> {
                        DownloadImageScreen(
                            imageUrl = imageUrl,
                            bitmap = bitmap,
                            onImageUrlChange = onImageUrlChange,
                            onDownloadClick = onDownloadClick,
                            paddingValues = paddingValues,
                            context = context,
                            onDeleteImagesClick = onDeleteImagesClick // Передаём функцию удаления
                        )
                    }
                    "ImageListScreen" -> {
                        ImageListScreen(context, paddingValues)
                    }
                }
            }
        }
    )
}

@Composable
fun DownloadImageScreen(
    imageUrl: String,
    bitmap: Bitmap?,
    onImageUrlChange: (String) -> Unit,
    onDownloadClick: () -> Unit,
    paddingValues: PaddingValues,
    context: Context,
    onDeleteImagesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
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

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Downloaded Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        Button(
            onClick = onDeleteImagesClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Удалить все изображения")
        }
    }
}

@Composable
fun ImageListScreen(context: Context, paddingValues: PaddingValues) {
    val imageFiles = remember { getSavedImages(context) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(imageFiles) { file ->
            ImageItem(file)
        }
    }
}

@Composable
fun ImageItem(file: File) {
    val bitmap = remember { BitmapFactory.decodeFile(file.absolutePath) }

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
