package com.example.backendApp1.service

import com.example.backendApp1.datasource.BankDataSource
import com.example.backendApp1.model.Bank
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.core.IsNot.not
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BankServiceTest {

    private val dataSource: BankDataSource = mockk(relaxed = true)
    private val bankService = BankService(dataSource)

    @Test
    fun `should call its data source to retrieve banks`() {

        //when
        bankService.getBanks()

        //then
        verify(exactly = 1) { dataSource.retrieveBanks() }
    }

    @Test
    fun `should call its data source to retrieve bank with account number`() {
        //given
        val accountNumber = "1234"
        val selectedBank = Bank(accountNumber = accountNumber, trust = 3.14, transactionFee = 170)
        //when
        every { dataSource.retrieveBank(accountNumber) } returns (selectedBank)
        val bank = bankService.getBank(accountNumber)

        //then
        assertEquals(bank, selectedBank)
    }

    @Test
    fun `should add a bank to data source`() {
        //given
        val bank = Bank("a123", trust = 0.5, transactionFee = 5)

        //when
        every { dataSource.addBank(bank) } returns bank
        val createdBank = bankService.addBank(bank)

        //then
        assertEquals(createdBank, bank)
    }

    @Test
    fun `should update bank data`() {
        //given
        val bank = Bank("a123", trust = 0.5, transactionFee = 5)
        val bankAfterUpdate = Bank("a123", trust = 0.5, transactionFee = 4)

        //when
        every { dataSource.updateBank(bank) } returns bankAfterUpdate
        val updatedBank = bankService.updateBank(bank)

        //then
        assertEquals(updatedBank, bankAfterUpdate)
    }

    @Test
    fun `should remove bank with account number`() {
        //given
        val accountNumber = "1234"

        //when
        every { dataSource.removeBank(accountNumber) } returns Unit
        bankService.removeBank(accountNumber)

        //then
        assertThat(dataSource.retrieveBanks(), not(hasItem(dataSource.retrieveBank(accountNumber))))
    }

}