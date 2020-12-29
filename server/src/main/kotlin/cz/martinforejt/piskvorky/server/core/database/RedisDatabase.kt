package cz.martinforejt.piskvorky.server.core.database

import redis.clients.jedis.Jedis

/**
 * Created by Martin Forejt on 28.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
private const val REDIS_ADDRESS = "localhost"
private const val REDIS_PORT = 6379
private const val REDIS_DB_NAME = "piskvorky_redis"

class RedisDatabase {
    val client = Jedis(REDIS_ADDRESS, REDIS_PORT).also { jedis ->
        jedis.connect()
    }
    val dbName = REDIS_DB_NAME
}