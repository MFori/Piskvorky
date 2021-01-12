package cz.martinforejt.piskvorky.server.core.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import cz.martinforejt.piskvorky.server.core.database.schema.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Created by Martin Forejt on 25.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
object DatabaseFactory {

    fun init() {
        Database.connect(hikariSource())

        transaction {
            SchemaUtils.create(
                Users,
                Friendships,
                GameInvitations,
                GameResults,
                LostPasswords
            )
        }
    }

    private fun hikariSource(): HikariDataSource {
        val address = "localhost"
        val user = "root"
        val pass = "password"
        val port = 3306
        val db = "piskvorky_db"

        //val address = System.getenv("SERVER_DB_ADDRESS")
        //val user = System.getenv("SERVER_DB_USER")
        //val pass = System.getenv("SERVER_DB_PASSWORD")
        //val port = System.getenv("SERVER_DB_PORT")
        //val db = System.getenv("SERVER_DB_NAME")

       //url = "jdbc:mysql://mysql:3306/piskvorky_db?serverTimezone=UTC",
       //driver = Driver::class.java.name,
       //user = user,
       //password = pass

        // TODO use env variables
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://$address:3306/piskvorky_db?serverTimezone=UTC"
            //jdbcUrl = "jdbc:mysql://mysql:3306/piskvorky_db?serverTimezone=UTC"
            driverClassName = "com.mysql.cj.jdbc.Driver"
            username = user
            password = pass
            maximumPoolSize = 10
        }

        config.validate()
        return HikariDataSource(config)
    }

}