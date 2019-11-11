package lt.markmerkk.widgets.main

interface MainContract {

    interface View {
        fun onAutoSyncLockChange(isLocked: Boolean)
    }
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()

        /**
         * Checks if auto-sync should be locked-out from updating
         */
        fun updateAutoSyncLock(
                isOpenLogDetails: Boolean,
                isOpeningLogDetails: Boolean,
                isOpenTickets: Boolean,
                isOpeningTickets: Boolean
        ): Boolean
    }
}