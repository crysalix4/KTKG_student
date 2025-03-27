package com.zing.ktkg_student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
@Composable
fun CustomerScreen(customerViewModel: CustomerViewModel = viewModel()) {
    val customerList = customerViewModel.customerList
    val error = customerViewModel.errorMessage
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        if (error != null) {
            Text(text = error, color = MaterialTheme.colors.error)
        }
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                customerViewModel.addCustomer(name, email, phone) {
                    name = ""
                    email = ""
                    phone = ""
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add Customer")
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(customerList) { customer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("ID: ${customer.id}")
                        Text("Name: ${customer.name}")
                        Text("Email: ${customer.email}")
                        Text("Phone: ${customer.phone}")

                        Row {
                            Button(
                                onClick = {
                                    // Ví dụ: Xoá
                                    customerViewModel.deleteCustomer(customer)
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
