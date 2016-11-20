package lt.markmerkk.mvp

import lt.markmerkk.entities.SimpleLog
import lt.markmerkk.entities.jobs.QueryListJob
import rx.Observable
import rx.Scheduler
import rx.util.async.Async

/**
 * @author mariusmerkevicius
 * @since 2016-10-25
 */
class LogInteractorImpl(
        private val resultProvider: QueryResultProvider<List<SimpleLog>>,
        private val ioScheduler: Scheduler
) : GraphMvp.LogInteractor {
    override fun loadLogs(
            fromMillis: Long,
            toMillis: Long
    ) : Observable<List<SimpleLog>> {
        return Async.fromCallable(
                {
                    val result = resultProvider.resultFrom(
                            QueryListJob(SimpleLog::class.java, {
                                "(start > ${fromMillis} AND start < ${toMillis})"
                            })
                    )
                    result ?: emptyList()
                }, ioScheduler
        )

    }
}