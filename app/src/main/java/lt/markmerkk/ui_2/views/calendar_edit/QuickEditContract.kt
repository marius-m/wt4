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
        fun moveForward(minutes: Int)
        fun moveBackward(minutes: Int)
        fun selectLogId(logId: Long)
    }

    interface ScaleView { }

    interface ScalePresenter {
        fun onAttach(view: ScaleView)
        fun onDetach()
        fun shrinkFromStart(minutes: Int)
        fun expandToStart(minutes: Int)
        fun shrinkFromEnd(minutes: Int)
        fun expandToEnd(minutes: Int)
        fun selectLogId(logId: Long)
    }

}