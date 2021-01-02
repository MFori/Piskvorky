package cz.martinforejt.piskvorky.domain.service

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.UserWithToken
import cz.martinforejt.piskvorky.domain.usecase.Result

/**
 * Created by Martin Forejt on 27.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

interface AuthenticationService {

    suspend fun login(request: LoginRequest): Result<UserWithToken>

    suspend fun register(request: RegisterRequest): Result<UserWithToken>

    suspend fun changePassword(request: ChangePasswordRequest, token: String): Result<Unit>

    suspend fun lostPassword(request: LostPasswordRequest): Result<Unit>

    suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit>

    fun logout()

    fun getCurrentUser(): UserWithToken?

    fun hasUser() = getCurrentUser() != null
}