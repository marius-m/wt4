package lt.markmerkk.credits

import lt.markmerkk.entities.Credit

interface CreditsContract {
    interface View {
        fun renderCreditEntries(entries: List<String>)
        fun showCreditDetails(entry: Credit)
    }
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
    }
}