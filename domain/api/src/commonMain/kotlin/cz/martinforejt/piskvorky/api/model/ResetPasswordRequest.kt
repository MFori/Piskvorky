/**
* Piskvorky API
* Piskvorky app server REST API specification
*
* The version of the OpenAPI document: v1
* 
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package cz.martinforejt.piskvorky.api.model


import kotlinx.serialization.*
import kotlinx.serialization.internal.CommonEnumSerializer

/**
 * 
 * @param email 
 * @param hash 
 * @param password 
 */
@Serializable
data class ResetPasswordRequest (
    @SerialName(value = "email") @Required val email: kotlin.String,
    @SerialName(value = "hash") @Required val hash: kotlin.String,
    @SerialName(value = "password") @Required val password: kotlin.String
)

