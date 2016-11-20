package lt.markmerkk.mvp

import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.entities.database.interfaces.IQueryJob
import lt.markmerkk.entities.database.interfaces.IResult

/**
 * @author mariusmerkevicius
 * @since 2016-10-25
 */
class QueryResultProviderImpl<T>(
        private val executor: IExecutor
): QueryResultProvider<T> {
    override fun resultFrom(query: IResult<T>): T? {
        executor.execute(query as IQueryJob)
        return query.result()
    }
}