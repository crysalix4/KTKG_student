package com.zing.ktkg_student.problem2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ResourceViewModel : ViewModel() {
    var resourceResponse: ResourceResponse? by mutableStateOf(null)
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set

    var errorMessage: String by mutableStateOf("")
        private set

    init {
        loadResources()
    }

    private fun loadResources() {
        viewModelScope.launch {
            isLoading = true
            try {
                resourceResponse = RetrofitClient.apiService.getResources()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }
}
