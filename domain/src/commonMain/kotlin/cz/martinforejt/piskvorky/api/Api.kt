package cz.martinforejt.piskvorky.api

/**
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
object Api {

    //const val BASE_URL = "http://http://192.168.0.104/:9090"
    private const val REST_SCHEMA = "http"
    private const val WS_SCHEMA = "ws"
    private const val BASE_URL = "localhost"
    private const val PORT = "9090"
    private const val API_VERSION = "v1"

    fun String.apiUrl() = "$REST_SCHEMA://$BASE_URL:$PORT/$API_VERSION$this"

    fun String.wsUrl() = "$WS_SCHEMA://$BASE_URL:$PORT/$API_VERSION$this"

    object EP {
        const val LOGIN = "/login"
        const val REGISTER = "/register"
        const val LOST_PASSWORD = "/lost-passwd"
        const val RESET_PASSWORD = "/reset-passwd"
        const val CHANGE_PASSWORD = "/profile/change-passwd"
        const val LOBBY = "/lobby"

        const val FRIENDS_LIST = "/friends"
        const val ADD_FRIEND = "/friends/add"
        const val CANCEL_FRIEND = "/friends/cancel"
        const val FRIEND_REQUESTS = "/friend/requests"
    }

}
