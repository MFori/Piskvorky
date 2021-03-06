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
 * @param x 
 * @param y 
 */
@Serializable
data class Move (
    @SerialName(value = "x") @Required val x: kotlin.Int,
    @SerialName(value = "y") @Required val y: kotlin.Int
)

