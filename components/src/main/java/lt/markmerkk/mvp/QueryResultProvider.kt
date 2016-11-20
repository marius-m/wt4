package lt.markmerkk.mvp

import lt.markmerkk.entities.database.interfaces.IResult

/**
 * @author mariusmerkevicius
 * @since 2016-10-25
 */
interface QueryResultProvider<T> {
    fun resultFrom(query: IResult<T>): T?
}