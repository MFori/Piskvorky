package service

import cz.martinforejt.piskvorky.api.model.LoginRequest
import cz.martinforejt.piskvorky.api.model.RegisterRequest
import cz.martinforejt.piskvorky.domain.model.UserWithToken
import cz.martinforejt.piskvorky.domain.service.AuthenticationService
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

/**
 * Created by Martin Forejt on 27.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class AuthenticationServiceImpl : AuthenticationService {

    override fun login(request: LoginRequest): Result<UserWithToken> {
        if(request.email == "admin@admin.com" && request.password == "pass123") {
            val user = UserWithToken("admin@admin.com", "token123")
            localStorage["user_email"] = user.email
            localStorage["user_token"] = user.token
            return Result(user)
        } else {
            return Result(error = cz.martinforejt.piskvorky.domain.usecase.Error(1, "invalid"))
        }
    }

    override fun register(request: RegisterRequest): Result<UserWithToken> {
        TODO("Not yet implemented")
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