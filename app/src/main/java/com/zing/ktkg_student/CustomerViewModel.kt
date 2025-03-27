package com.zing.ktkg_student

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CustomerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CustomerDb.getDatabase(application)
    private val repository = CustomerRepository(db.customerDao())
    var customerList by mutableStateOf<List<Customer>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    init {
        viewModelScope.launch {
            loadCustomers()
        }
    }

    private suspend fun loadCustomers() {
        customerList = repository.getAllCustomers()
    }
    fun addCustomer(name: String, email: String, phone: String, onSuccess: () -> Unit) {
        // Xác thực dữ liệu
        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
            errorMessage = "Vui lòng điền đầy đủ thông tin."
            return
        }
        if (!email.contains("@")) {
            errorMessage = "Email không hợp lệ."
            return
        }
        if (!phone.all { it.isDigit() } || phone.length < 7) {
            errorMessage = "Số điện thoại không hợp lệ."
            return
        }
        errorMessage = null
        viewModelScope.launch {
            repository.insertCustomer(Customer(name = name, email = email, phone = phone))
            loadCustomers()
            onSuccess()
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            loadCustomers()
        }
    }
}
