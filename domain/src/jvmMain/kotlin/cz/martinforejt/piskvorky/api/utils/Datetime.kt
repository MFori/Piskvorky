package cz.martinforejt.piskvorky.api.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Created by Martin Forejt on 02.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
object ApiUtils {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .withZone(ZoneId.of("UTC"))

    fun LocalDateTime.formatApi(): String = this.format(dateFormatter)
}