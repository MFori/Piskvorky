package cz.martinforejt.piskvorky.domain.repository

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Logger


actual fun createDb()  {
//    val driver =  JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
}

actual fun getLogger(): Logger = CommonLogger()