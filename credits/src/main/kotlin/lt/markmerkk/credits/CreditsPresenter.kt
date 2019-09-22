package lt.markmerkk.credits

import lt.markmerkk.repositories.CreditsRepository
import org.slf4j.LoggerFactory
import rx.Scheduler
import rx.Subscription

class CreditsPresenter(
        private val creditsRepository: CreditsRepository,
        private val ioScheduler: Scheduler,
        private val uiScheduler: Scheduler
): CreditsContract.Presenter {

    private var view: CreditsContract.View? = null
    private var entriesDisposable: Subscription? = null
    private var findEntryDisposable: Subscription? = null

    override fun onAttach(view: CreditsContract.View) {
        this.view = view
        entriesDisposable = creditsRepository.creditEntries()
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .subscribe({ entries ->
                val creditTitles = entries.map { it.title }
                this.view?.renderCreditEntries(creditTitles)
                findCreditDetails(creditTitles.first())
            }, { error ->
                logger.warn("Error reading credit entries", error)
            })
    }

    override fun onDetach() {
        this.view = null
        entriesDisposable?.unsubscribe()
        findEntryDisposable?.unsubscribe()
    }

    fun findCreditDetails(creditTitle: String) {
        findEntryDisposable?.unsubscribe()
        findEntryDisposable = creditsRepository.creditEntries()
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .subscribe({ entries ->
                val entry = entries
                    .first { it.title == creditTitle }
                view?.showCreditDetails(entry)
            }, { error ->
                logger.warn("Error trying to look for entry", error)
            })
    }

    companion object {
        val logger = LoggerFactory.getLogger(CreditsPresenter::class.java)!!
    }

}