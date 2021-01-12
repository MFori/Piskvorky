package cz.martinforejt.piskvorky.server.features.admin.model

import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Created by Martin Forejt on 12.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
fun Parameters.asEditUserRequest() = EditUserRequest(
    admin = this.contains("admin"),
    active = this.contains("active"),
    pass = this["pass"],
    passConfirm = this["pass-confirm"],
    save = this.contains("save"),
    delete = this.contains("delete")
)

@Serializable
data class EditUserRequest(
    val admin: Boolean,
    val active: Boolean,
    val pass: String?,
    val passConfirm: String?,
    val save: Boolean,
    val delete: Boolean
)