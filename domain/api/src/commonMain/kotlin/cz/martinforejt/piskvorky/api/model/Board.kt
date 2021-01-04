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

import cz.martinforejt.piskvorky.api.model.BoardCell

import kotlinx.serialization.*
import kotlinx.serialization.internal.CommonEnumSerializer

/**
 * 
 * @param cells 
 */
@Serializable
data class Board (
    @SerialName(value = "cells") @Required val cells: kotlin.collections.List<BoardCell>
)
