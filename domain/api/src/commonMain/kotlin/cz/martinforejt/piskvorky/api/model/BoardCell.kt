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

import cz.martinforejt.piskvorky.api.model.BoardValue

import kotlinx.serialization.*
import kotlinx.serialization.internal.CommonEnumSerializer

/**
 * 
 * @param x 
 * @param y 
 * @param value 
 */
@Serializable
data class BoardCell (
    @SerialName(value = "x") @Required val x: kotlin.Int,
    @SerialName(value = "y") @Required val y: kotlin.Int,
    @SerialName(value = "value") @Required val value: BoardValue
)
