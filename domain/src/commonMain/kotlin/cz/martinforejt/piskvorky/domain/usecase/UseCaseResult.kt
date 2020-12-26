package cz.martinforejt.piskvorky.domain.usecase

/**
 * Special type of [UseCase] with [Result]
 *
 * @author [Martin Forejt](mailto:me@martinforejt.cz)
 */
interface UseCaseResult<R : Any, in P> : UseCase<Result<R>, P>