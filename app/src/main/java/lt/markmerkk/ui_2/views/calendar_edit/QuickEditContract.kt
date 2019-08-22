package lt.markmerkk.ui_2.views.calendar_edit

interface QuickEditContract {

    interface ContainerView {
        fun changeLogSelection(selectId: Long)
        fun changeToNoSelection()
        fun selectedId(): Long
    }

    interface ContainerPresenter {
        fun onAttach(view: ContainerView)
        fun onDetach()
    }

    interface MoveView { }

    interface MovePresenter {
        fun onAttach(view: MoveView)
        fun onDetach()
        fun moveForward(minutes: Int): Long
        fun moveBackward(minutes: Int): Long
    }

    interface ScaleView { }

    interface ScalePresenter {
        fun onAttach(view: ScaleView)
        fun onDetach()
        fun shrinkFromStart(minutes: Int): Long
        fun expandToStart(minutes: Int): Long
        fun shrinkFromEnd(minutes: Int): Long
        fun expandToEnd(minutes: Int): Long
    }

    /**
     * Provides selected entry id
     */
    interface SelectEntryProvider {
        fun entryId(): Long
        fun suggestNewEntry(newEntryId: Long)
    }

}