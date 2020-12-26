package cz.martinforejt.piskvorky.domain.usecase

/**
 * Error
 *
 * @author [Martin Forejt](mailto:me@martinforejt.cz)
 */
data class Error(
    val code: Int,
    val message: String? = null,
    val throwable: Throwable? = null
)