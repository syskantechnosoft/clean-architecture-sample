package api

import Config
import adapters.MySqlUserRepository
import api.HttpDsl.`create user`
import api.HttpDsl.`delete user`
import api.HttpDsl.`list users`
import domain.ports.UserRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.testcontainers.containers.MySQLContainer

class IntegrationWithMySqlTest {

    private lateinit var webApp: WebApp
    private lateinit var userRepository: UserRepository
    private lateinit var dbServer: MySQLContainer<Nothing>
    private lateinit var database: Database

    @BeforeAll
    @Suppress("unused")
    fun setup() {
        dbServer = MySQLContainer<Nothing>("mysql")
        dbServer.start()
        database = Database.connect(
            url = dbServer.jdbcUrl,
            user = dbServer.username,
            password = dbServer.password,
            driver = dbServer.driverClassName,
        )
        userRepository = MySqlUserRepository(database)
        webApp = WebApp(object : Config() {
            override val repo = userRepository
        }, 8081).start()
    }

    @AfterAll
    @Suppress("unused")
    fun `tear down`() {
        webApp.close()
        dbServer.stop()
    }

    @BeforeEach
    fun `before each`() {
        transaction(database) {
            object : Table("users") {}.deleteAll()
        }
    }

    @Test
    fun `create a user when posting a user json`() {
        `create user`("luis.s@gmail.com", "Luís Soares", "password")
        `create user`("miguel.s@gmail.com", "Miguel Soares", "f47!3#$5g%")

        val userList = `list users`()

        JSONAssert.assertEquals(
            """ [ { "name": "Luís Soares", "email": "luis.s@gmail.com" },
                            { "name": "Miguel Soares", "email": "miguel.s@gmail.com" } ] """,
            userList.body(),
            false
        )
    }

    @Test
    fun `create two users when posting two different requests`() {
        `create user`("luis.s@gmail.com", "Luís Soares", "password")
        `create user`("miguel.s@gmail.com", "Miguel Soares", "f47!3#$5g%")

        val userList = `list users`()

        JSONAssert.assertEquals(
            """ [ { "name": "Luís Soares", "email": "luis.s@gmail.com" },
                            { "name": "Miguel Soares", "email": "miguel.s@gmail.com" } ] """,
            userList.body(),
            false
        )
    }

    @Test
    fun `do not create a repeated user when posting twice`() {
        `create user`("luis.1@gmail.com", "Luís Soares", "password")

        val creation2Response = `create user`("luis.1@gmail.com", "Luís Soares", "password")

        assertEquals(409, creation2Response.statusCode())
        JSONAssert.assertEquals(
            """ [ { "name": "Luís Soares", "email": "luis.1@gmail.com" }] """,
            `list users`().body(),
            false
        )
    }

    @Test
    fun `delete a user after creation`() {
        `create user`("luis.s@gmail.com", "Luís Soares", "password")
        `create user`("miguel.s@gmail.com", "Miguel Soares", "f47!3#\$5g%")

        val deleteResponse = `delete user`("luis.s@gmail.com")

        assertEquals(204, deleteResponse.statusCode())
        val usersAfter = `list users`()
        JSONAssert.assertEquals(
            """ [ { "name": "Miguel Soares", "email": "miguel.s@gmail.com" } ] """,
            usersAfter.body(),
            false
        )
    }
}