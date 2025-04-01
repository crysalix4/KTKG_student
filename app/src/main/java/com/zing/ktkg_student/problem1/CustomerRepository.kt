package com.zing.ktkg_student.problem1

class CustomerRepository(private val dao: CustomerDao) {

    suspend fun insertCustomer(customer: Customer) = dao.insertCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = dao.deleteCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = dao.updateCustomer(customer)

    suspend fun getAllCustomers(): List<Customer> = dao.getAllCustomers()
    suspend fun getCustomerById(id: Int): Customer? = dao.getCustomerById(id)
}
