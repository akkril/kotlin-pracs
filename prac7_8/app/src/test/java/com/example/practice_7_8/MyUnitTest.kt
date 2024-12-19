package com.example.practice_7_8

import kotlinx.coroutines.*
import android.content.Context
import android.graphics.Bitmap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.DEFAULT_MANIFEST_NAME)
class MainActivityTest {

    private lateinit var mockContext: Context
    private lateinit var mainActivity: MainActivity
    private lateinit var mainActivity2: MainActivity
    private val testDispatcher = StandardTestDispatcher()
    private val editTextUrl: EditText = mock(EditText::class.java)
    private val buttonDownload: Button = mock(Button::class.java)
    private val imageView: ImageView = mock(ImageView::class.java)


    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        Dispatchers.setMain(testDispatcher)
        mainActivity = MainActivity()
        mainActivity2 = spy(MainActivity())
        mainActivity2.editTextUrl = editTextUrl
        mainActivity2.buttonDownload = buttonDownload
        mainActivity2.imageView = imageView
    }

    @Test
    fun testDownloadImageSuccess() = runBlocking {
        val imageUrl =
            "https://images.wallpaperscraft.com/image/single/lake_mountain_tree_36589_2650x1600.jpg"

        val bitmap = mainActivity.downloadImage(imageUrl)

        assertNotNull(bitmap)
    }

    @Test
    fun testDownloadImageFailure() = runBlocking {
        val invalidImageUrl = "https://example.com/invalid.jpg"

        val bitmap = mainActivity.downloadImage(invalidImageUrl)

        assertNull(bitmap)
    }
}