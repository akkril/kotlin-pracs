package com.example.practice_11

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : ComponentActivity() {
    private var imageUrl by mutableStateOf("")
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(
                context = this,
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
                }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    context: Context,
    imageUrl: String,
    bitmap: Bitmap?,
    onImageUrlChange: (String) -> Unit,
    onDownloadClick: () -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column {
                    Text("Название меню", modifier = Modifier.padding(16.dp))

                    TextButton(onClick = {
                        navController.navigate("DownloadImageScreen")
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    }) {
                        Text("Загрузить изображение")
                    }

                    TextButton(onClick = {
                        navController.navigate("ImageListScreen")
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
                        title = { Text("Навигация") },
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
                        Button(onClick = { navController.navigate("DownloadImageScreen") }) {
                            Text("Загрузить изображение")
                        }
                        Button(onClick = { navController.navigate("ImageListScreen") }) {
                            Text("Сохранённые изображения")
                        }
                    }
                }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "DownloadImageScreen",
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable("DownloadImageScreen") {
                        DownloadImageScreen(
                            imageUrl = imageUrl,
                            bitmap = bitmap,
                            onImageUrlChange = onImageUrlChange,
                            onDownloadClick = onDownloadClick,
                            paddingValues = paddingValues,
                            context = context
                        )
                    }
                    composable("ImageListScreen") {
                        ImageListScreen(context = context, paddingValues = paddingValues)
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
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = imageUrl,
            onValueChange = onImageUrlChange,
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
    }
}

@Composable
fun ImageListScreen(context: Context, paddingValues: PaddingValues) {
    val imageFiles = getSavedImages(context)

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
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)

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
