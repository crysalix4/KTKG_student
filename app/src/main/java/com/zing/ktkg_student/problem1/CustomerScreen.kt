package com.zing.ktkg_student.problem1

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomerScreen(customerViewModel: CustomerViewModel = viewModel()) {
    val customerList = customerViewModel.customerList
    val error = customerViewModel.errorMessage

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val scaffoldState = rememberScaffoldState()
    var editingCustomer by remember { mutableStateOf<Customer?>(null) }
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            birthDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Background gradient cho toàn màn hình
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.primary.copy(alpha = 0.3f),
                        MaterialTheme.colors.background
                    )
                )
            )
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text("Customer Management", style = MaterialTheme.typography.h6) },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                    elevation = 12.dp
                )
            },
            backgroundColor = Color.Transparent,
            content = { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Form thêm mới khách hàng
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp), // bo tròn lại 16 dp
                            elevation = 8.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(MaterialTheme.colors.surface)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Add Customer",
                                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text("Name") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = "Name icon")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = "Email icon")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    label = { Text("Phone") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Phone, contentDescription = "Phone icon")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = birthDate,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Birth Date") },
                                    trailingIcon = {
                                        IconButton(onClick = { datePickerDialog.show() }) {
                                            Icon(Icons.Default.CalendarToday, contentDescription = "Select date")
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

//  hiển thị thông báo lỗi
                                if (!error.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colors.error,
                                        style = MaterialTheme.typography.body2
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        val date: Date? = try {
                                            dateFormat.parse(birthDate)
                                        } catch (e: Exception) {
                                            null
                                        }
                                        if (date == null) {
                                            customerViewModel.updateError("Invalid birth date.")
                                            return@Button
                                        }

                                        customerViewModel.addCustomer(name, email, phone, date) {
                                            // Reset form sau khi thêm thành công
                                            name = ""
                                            email = ""
                                            phone = ""
                                            birthDate = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text("Add Customer")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        Text(
                            text = "Customer List",
                            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(customerList) { customer ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = 4.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Name: ${customer.name}",
                                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Email: ${customer.email}",
                                        style = MaterialTheme.typography.body2
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Phone: ${customer.phone}",
                                        style = MaterialTheme.typography.body2
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Birth Date: ${dateFormat.format(customer.birthDate)}",
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                                Row {
                                    IconButton(onClick = { editingCustomer = customer }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit customer",
                                            tint = MaterialTheme.colors.primary
                                        )
                                    }
                                    IconButton(onClick = { customerViewModel.deleteCustomer(customer) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete customer",
                                            tint = MaterialTheme.colors.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        )
        if (editingCustomer != null) {
            val customer = editingCustomer!!
            var editName by remember { mutableStateOf(customer.name) }
            var editEmail by remember { mutableStateOf(customer.email) }
            var editPhone by remember { mutableStateOf(customer.phone) }
            var editBirthDate by remember { mutableStateOf(dateFormat.format(customer.birthDate)) }

            val editDatePickerDialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    editBirthDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            AlertDialog(
                onDismissRequest = { editingCustomer = null },
                title = {
                    Text(
                        text = "Edit Customer",
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editBirthDate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Birth Date") },
                            trailingIcon = {
                                IconButton(onClick = { editDatePickerDialog.show() }) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Select date"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        // Hiển thị thông báo lỗi nếu có
                        if (customerViewModel.errorMessage?.isNotEmpty() == true) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = customerViewModel.errorMessage ?: "",
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newDate = try {
                                dateFormat.parse(editBirthDate)
                            } catch (e: Exception) {
                                null
                            }
                            if (newDate == null) {
                                customerViewModel.updateError("Invalid birth date.")
                                return@Button
                            }
                            customerViewModel.updateCustomer(
                                customer,
                                editName,
                                editEmail,
                                editPhone,
                                newDate
                            ) {
                                editingCustomer = null
                            }
                        },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Update")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { editingCustomer = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
