package cz.martinforejt.piskvorky.server.core.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import cz.martinforejt.piskvorky.server.core.database.schema.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 25.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
object DatabaseFactory {

    fun init() {
        // mysql (mainly first) startup takes longer time (and wait-for-it.sh not always do his job)
        for (i in 1..10) {
            try {
                // try to connect to db
                Database.connect(hikariSource())
                break
            //} catch (e: com.mysql.cj.jdbc.exceptions.CommunicationsException) {
            } catch (e: Exception) {
                exposedLogger.debug("Cannot connect to db, trying it again in $i seconds")
                if (i == 10) throw e
                else Thread.sleep(i * 1000L)
            }
        }

        // create tables and prefilled data
        transaction {
            SchemaUtils.create(
                Users,
                Friendships,
                GameInvitations,
                GameResults,
                LostPasswords
            )

            prefilledUsers()
        }
    }

    /**
     * Create hikari data source
     */
    private fun hikariSource(): HikariDataSource {
        //val address = "localhost"
        //val user = "root"
        //val pass = "password"
        //val port = 3306
        //val db = "piskvorky_db"

        val address = System.getenv("SERVER_DB_ADDRESS")
        val user = System.getenv("SERVER_DB_USER")
        val pass = System.getenv("SERVER_DB_PASSWORD")
        val port = System.getenv("SERVER_DB_PORT")
        val db = System.getenv("SERVER_DB_NAME")

        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://$address:$port/$db?serverTimezone=UTC"
            driverClassName = "com.mysql.cj.jdbc.Driver"
            username = user
            password = pass
            maximumPoolSize = 10
        }

        config.validate()
        return HikariDataSource(config)
    }

    /**
     * Insert some prefilled data
     */
    private fun prefilledUsers() {
        Users.insertIgnore {
            it[id] = 1
            it[email] = "admin@admin.com"
            it[password] = "yLFxE1NAJNs/i5IA/Cmx/tHGQAwtxr8afOzF0O+Ed9Y=" // test123
            it[active] = true
            it[admin] = true
            it[created] = LocalDateTime.now()
        }

    }
}