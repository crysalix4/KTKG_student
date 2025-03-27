package com.zing.ktkg_student

class CustomerRepository(private val dao: CustomerDao) {

    suspend fun insertCustomer(customer: Customer) = dao.insertCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = dao.deleteCustomer(customer)

    suspend fun getAllCustomers(): List<Customer> = dao.getAllCustomers()
    suspend fun getCustomerById(id: Int): Customer? = dao.getCustomerById(id)
}
