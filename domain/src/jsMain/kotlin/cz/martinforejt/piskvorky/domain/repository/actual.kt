package cz.martinforejt.piskvorky.domain.repository

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


actual fun createDb() {
}

actual fun getLogger(): Logger = CommonLogger()