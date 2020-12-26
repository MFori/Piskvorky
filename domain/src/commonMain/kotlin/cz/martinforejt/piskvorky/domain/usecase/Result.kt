package cz.martinforejt.piskvorky.domain.usecase

/**
 * Result
 *
 * @author [Martin Forejt](mailto:me@martinforejt.cz)
 */
data class Result<T : Any>(
    val data: T? = null,
    val error: Error? = null
) {
    val isSuccessful = data != null && error == null
}