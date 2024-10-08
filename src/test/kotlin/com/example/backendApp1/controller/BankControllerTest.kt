package com.example.backendApp1.controller

import com.example.backendApp1.model.Bank
import com.fasterxml.jackson.databind.ObjectMapper
import jdk.incubator.vector.VectorOperators
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class BankControllerTest
@Autowired constructor(
    val mockMvc: MockMvc, val objectMapper: ObjectMapper
) {

    private val baseUrl = "/api/banks"

    @Nested
    @DisplayName("GET api/banks/")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetBanks {

        @Test
        fun `should return all banks`() {
            // when/then
            mockMvc.get(baseUrl).andDo { print() }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0].accountNumber") {
                    value("1234")
                }
            }
        }
    }

    @Nested
    @DisplayName("GET api/banks/{bank}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetBank {

        @Test
        fun `should return bank with selected number`() {
            //given
            val accountNumber = 1234

            //when/then
            mockMvc.get("$baseUrl/$accountNumber").andDo { print() }.andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }
        }

        @Test
        fun `should return NOT FOUND error when account number doesn't exist`() {
            //given
            val accountNumber = 9310

            //when/then
            mockMvc.get("$baseUrl/$accountNumber").andDo { print() }.andExpect {
                status { isNotFound() }
            }
        }
    }

    @Nested
    @DisplayName("Post api/banks/{bank}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PostBank {

        @Test
        fun `should add new bank when receiving post request`() {
            //given
            val bank = Bank("a123", trust = 0.5, transactionFee = 5)

            //when
            val preformPost = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(bank)
            }

            //then
            preformPost
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                }
        }

        @Test
        fun `should return BAD REQUEST if the added bank already exists`() {
            //given
            val invalidBank = Bank("1234", trust = 0.5, transactionFee = 5)

            //when
            val preformPost = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(invalidBank)
            }

            //then
            preformPost
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
        }
    }

    @Nested
    @DisplayName("PATCH api/banks/{bank}")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PatchBank {

        @Test
        fun `should patch existing bank when receiving patch request`() {
            //given
            val bank = Bank("1234", trust = 0.5, transactionFee = 5)

            //when
            val performPatch = mockMvc.patch(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(bank)
            }

            //then
            performPatch
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { json(objectMapper.writeValueAsString(bank)) }
                }

            mockMvc.get("$baseUrl/${bank.accountNumber}")
                .andExpect { content { json(objectMapper.writeValueAsString(bank)) } }
        }

        @Test
        fun `should return BAD REQUEST if bank id doesn't exist`(){
            //given
            val invalidBank = Bank("a123", 0.5, 4)

            //when
            val preformPatch = mockMvc.patch(baseUrl){
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(invalidBank)
            }

            //then
            preformPatch
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
        }
    }
}