package com.example.work6.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.work6.data.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatViewModel @Inject constructor(
    private val repository: CatRepository
): ViewModel() {
    val catImageUrl = MutableLiveData<String>()
    val error = MutableLiveData<String>()
    val saveResult = MutableLiveData<Boolean>()

    fun fetchCat() {
        repository.fetchCatFromApi { result ->
            result.onSuccess { cats ->
                if (cats.isNotEmpty()) {
                    val cat = cats[0]
                    viewModelScope.launch {
                        repository.saveCatToDb(cat)
                    }
                    catImageUrl.postValue(cat.url)
                } else {
                    error.postValue("No cat data found")
                }
            }.onFailure { throwable ->
                error.postValue(throwable.message)
            }
        }
    }

    fun loadCatFromDb() {
        viewModelScope.launch {
            val cat = repository.getCatFromDb()
            if (cat != null) {
                catImageUrl.postValue(cat.url)
            } else {
                error.postValue("No cat in database")
            }
        }
    }

    fun downloadAndSaveImage(url: String) {
        viewModelScope.launch {
            val success = repository.downloadAndSaveImage(url)
            saveResult.postValue(success)
        }
    }
}