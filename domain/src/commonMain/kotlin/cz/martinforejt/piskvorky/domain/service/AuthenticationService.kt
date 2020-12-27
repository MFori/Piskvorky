package cz.martinforejt.piskvorky.domain.service

import cz.martinforejt.piskvorky.api.model.LoginRequest
import cz.martinforejt.piskvorky.api.model.RegisterRequest
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

    fun logout()

    fun getCurrentUser(): UserWithToken?
}