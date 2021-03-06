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

import cz.martinforejt.piskvorky.api.model.FriendRequest

import kotlinx.serialization.*
import kotlinx.serialization.internal.CommonEnumSerializer

/**
 * 
 * @param requests 
 */
@Serializable
data class FriendsRequestsResponse (
    @SerialName(value = "requests") @Required val requests: kotlin.collections.List<FriendRequest>
)

