package service

import core.ApiClient
import core.ApiResult
import cz.martinforejt.piskvorky.api.Api
import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.UserWithToken
import cz.martinforejt.piskvorky.domain.service.AuthenticationService
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import kotlinx.browser.localStorage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.w3c.dom.get
import org.w3c.dom.set

/**
 * [AuthenticationService] implementation for web client
 *
 * Created by Martin Forejt on 27.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class AuthenticationServiceImpl : AuthenticationService {

    private val format = Json { serializersModule = SerializersModule { } }

    @ExperimentalSerializationApi
    override suspend fun login(request: LoginRequest): Result<UserWithToken> {
        val res = ApiClient.post<LoginResponse>(Api.EP.LOGIN, format.encodeToString(LoginRequest.serializer(), request))
        return if (res.isSuccess) {
            val data = res.data!!
            val user = UserWithToken(request.email, data.token)
            store(user.email, user.token)
            Result(user)
        } else {
            logout()
            res.toErrorResult()
        }
    }

    @ExperimentalSerializationApi
    override suspend fun register(request: RegisterRequest): Result<UserWithToken> {
        val res = ApiClient.post<LoginResponse>(Api.EP.REGISTER, format.encodeToString(RegisterRequest.serializer(), request))
        return if (res.isSuccess) {
            val data = res.data!!
            val user = UserWithToken(request.email, data.token)
            store(user.email, user.token)
            Result(user)
        } else {
            res.toErrorResult()
        }
    }

    @ExperimentalSerializationApi
    override suspend fun changePassword(request: ChangePasswordRequest, token: String): Result<Unit> {
        val res = ApiClient.post<Unit>(
            Api.EP.CHANGE_PASSWORD,
            format.encodeToString(ChangePasswordRequest.serializer(), request),
            token
        )
        return if (res.isSuccess) {
            Result(Unit)
        } else {
            res.toErrorResult()
        }
    }

    @ExperimentalSerializationApi
    override suspend fun lostPassword(request: LostPasswordRequest): Result<Unit> {
        val res = ApiClient.post<Unit>(Api.EP.LOST_PASSWORD, format.encodeToString(LostPasswordRequest.serializer(), request))
        return if (res.isSuccess) {
            Result(Unit)
        } else {
            res.toErrorResult()
        }
    }

    @ExperimentalSerializationApi
    override suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit> {
        val res = ApiClient.post<Unit>(Api.EP.RESET_PASSWORD, format.encodeToString(ResetPasswordRequest.serializer(), request))
        return if (res.isSuccess) {
            Result(Unit)
        } else {
            res.toErrorResult()
        }
    }

    private fun <T : Any> ApiResult<*>.toErrorResult(): Result<T> {
        val error = error!!
        return Result(
            error = Error(
                error.code,
                error.message
            )
        )
    }

    private fun store(email: String, token: String) {
        localStorage["user_email"] = email
        localStorage["user_token"] = token
    }

    override fun logout() {
        localStorage.removeItem("user_email")
        localStorage.removeItem("user_token")
    }

    override fun getCurrentUser(): UserWithToken? {
        val email = localStorage["user_email"]
        val token = localStorage["user_token"]
        return if (email != null && token != null) {
            UserWithToken(email, token)
        } else {
            null
        }
    }
}