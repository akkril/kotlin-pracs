package com.example.work6

import android.content.Context
import com.example.work6.data.database.CatDao
import com.example.work6.data.model.Cat
import com.example.work6.data.network.CatApi
import com.example.work6.data.repository.CatRepository
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatRepositoryTest {

    private lateinit var catRepository: CatRepository
    private lateinit var mockCatDao: CatDao
    private lateinit var mockCatApi: CatApi
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        mockCatDao = mock(CatDao::class.java)
        mockCatApi = mock(CatApi::class.java)
        mockContext = mock(Context::class.java)

        catRepository = CatRepository(mockCatDao, mockCatApi, mockContext)
    }

    @Test
    fun `downloadAndSaveImage should return true on successful download`() = runBlocking {
        // Arrange
        val responseBody = ResponseBody.create("image/png".toMediaTypeOrNull(), byteArrayOf(1, 2, 3))
        val call = mock(Call::class.java) as Call<ResponseBody>
        `when`(call.execute()).thenReturn(Response.success(responseBody))
        `when`(mockCatApi.downloadImage("https://example.com/cat.png")).thenReturn(call)

        // Act
        val result = catRepository.downloadAndSaveImage("https://example.com/cat.png")

        // Assert
        assert(result)
    }

    @Test
    fun `downloadAndSaveImage should return false on failure`() = runBlocking {
        // Arrange
        val call = mock(Call::class.java) as Call<ResponseBody>
        `when`(call.execute()).thenReturn(Response.error(404, ResponseBody.create(null, "")))
        `when`(mockCatApi.downloadImage("https://example.com/cat.png")).thenReturn(call)

        // Act
        val result = catRepository.downloadAndSaveImage("https://example.com/cat.png")

        // Assert
        assert(!result)
    }

    @Test
    fun `fetchCatFromApi should call success callback on successful response`() {
        // Arrange
        val catList = listOf(
            Cat(id = "1", url = "https://example.com/cat1.png", width = 500, height = 400),
            Cat(id = "2", url = "https://example.com/cat2.png", width = 600, height = 450)
        )
        val call = mock(Call::class.java) as Call<List<Cat>>
        `when`(call.enqueue(any())).thenAnswer { invocation ->
            val callback = invocation.arguments[0] as Callback<List<Cat>>
            callback.onResponse(call, Response.success(catList))
        }
        `when`(mockCatApi.getCat()).thenReturn(call)

        // Act
        catRepository.fetchCatFromApi { result ->
            assert(result.isSuccess)
            assert(result.getOrNull() == catList)
        }
    }

    @Test
    fun `fetchCatFromApi should call failure callback on API error`() {
        // Arrange
        val call = mock(Call::class.java) as Call<List<Cat>>
        `when`(call.enqueue(any())).thenAnswer { invocation ->
            val callback = invocation.arguments[0] as Callback<List<Cat>>
            callback.onFailure(call, Throwable("API Error"))
        }
        `when`(mockCatApi.getCat()).thenReturn(call)

        // Act
        catRepository.fetchCatFromApi { result ->
            assert(result.isFailure)
        }
    }

    @Test
    fun `saveCatToDb should call insertCat`() = runBlocking {
        // Arrange
        val cat = Cat(id = "1", url = "https://example.com/cat1.png", width = 500, height = 400)

        // Act
        catRepository.saveCatToDb(cat)

        // Assert
        verify(mockCatDao).insertCat(cat)
    }

    @Test
    fun `getCatFromDb should return cat from database`() = runBlocking {
        // Arrange
        val cat = Cat(id = "1", url = "https://example.com/cat1.png", width = 500, height = 400)
        `when`(mockCatDao.getCat()).thenReturn(cat)

        // Act
        val result = catRepository.getCatFromDb()

        // Assert
        assert(result == cat)
    }
}
