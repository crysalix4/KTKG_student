package com.zing.ktkg_student.problem1
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Date

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

    fun addCustomer(
        name: String,
        email: String,
        phone: String,
        birthDate: Date?,
        onSuccess: () -> Unit
    ) {
        val validationError = validateCustomerInput(name, email, phone, birthDate)
        if (validationError != null) {
            errorMessage = validationError
            return
        }

        errorMessage = ""
        viewModelScope.launch {
            repository.insertCustomer(Customer(name = name, email = email, phone = phone, birthDate = birthDate!!))
            loadCustomers()
            onSuccess()
        }
    }
    fun validateCustomerInput(
        name: String,
        email: String,
        phone: String,
        birthDate: Date?
    ): String? {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || birthDate == null) {
            return "Vui lòng điền đầy đủ thông tin và chọn ngày sinh."
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        if (!email.matches(emailRegex)) {
            return "Email không hợp lệ."
        }

        val phoneRegex = "^[0-9]{7,}$".toRegex()
        if (!phone.matches(phoneRegex)) {
            return "Số điện thoại không hợp lệ. Vui lòng nhập ít nhất 7 chữ số."
        }

        val currentDate = Date()
        if (birthDate.after(currentDate)) {
            return "Ngày sinh không hợp lệ, vui lòng chọn ngày trong quá khứ."
        }

        return null
    }

    fun updateError(message: String?) {
        errorMessage = message
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            loadCustomers()
        }
    }
    fun updateCustomer(
        customer: Customer,
        name: String,
        email: String,
        phone: String,
        birthDate: Date?,
        onSuccess: () -> Unit
    ) {
        val validationError = validateCustomerInput(name, email, phone, birthDate)
        if (validationError != null) {
            errorMessage = validationError
            return
        }

        errorMessage = ""

        val updatedCustomer = customer.copy(
            name = name,
            email = email,
            phone = phone,
            birthDate = birthDate!!
        )

        viewModelScope.launch {
            repository.updateCustomer(updatedCustomer)
            loadCustomers()
            onSuccess()
        }
    }


}
