package domain.usecases

import domain.model.User
import domain.model.toEmail
import domain.model.toPassword
import domain.model.toUserId
import domain.ports.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ListUsersTest {

    @Test
    fun `it returns a list of users from the repo`() {
        val repository = mockk<UserRepository> {
            every { findAll() } returns listOf(
                User(
                    id = "abc1".toUserId(),
                    email = "email@test.com".toEmail(),
                    name = "Luís Soares",
                    password = "password".toPassword()
                )
            )
        }
        val listUsers = ListUsers(repository)

        val users = listUsers()

        verify(exactly = 1) { repository.findAll() }
        assertEquals(
            listOf(
                User(
                    id = "abc1".toUserId(),
                    email = "email@test.com".toEmail(),
                    name = "Luís Soares",
                    password = "password".toPassword()
                )
            ),
            users
        )
    }
}