package cz.martinforejt.piskvorky.domain.repository

import co.touchlab.kermit.Logger

expect fun createDb()

expect fun getLogger(): Logger