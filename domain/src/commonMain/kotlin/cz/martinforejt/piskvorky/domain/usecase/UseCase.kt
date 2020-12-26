package cz.martinforejt.piskvorky.domain.usecase

/**
 * Core usecase
 * With param [P] that flows in usecase and result [R] which flows out of the usecase
 *
 * @author [Martin Forejt](mailto:me@martinforejt.cz)
 */
interface UseCase<out R, in P> {

    fun execute(params: P): R

}