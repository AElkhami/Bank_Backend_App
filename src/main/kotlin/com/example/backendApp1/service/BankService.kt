package com.example.backendApp1.service

import com.example.backendApp1.datasource.BankDataSource
import com.example.backendApp1.model.Bank
import org.springframework.stereotype.Service

@Service
class BankService(private val dataSource: BankDataSource) {
    fun getBanks(): Collection<Bank> = dataSource.retrieveBanks()
    fun getBank(accountNumber: String): Bank = dataSource.retrieveBank(accountNumber)
    fun addBank(bank: Bank) = dataSource.addBank(bank)
}