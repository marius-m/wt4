package lt.markmerkk.ui_2.views.calendar_edit

import lt.markmerkk.entities.SimpleLog

interface QuickEditContract {

    interface LifecycleView {
        fun onAttach()
        fun onDetach()
    }

    interface SelectableView {
        fun onSelectLog(logId: Long)
    }

    interface MoveView {
        fun onAttach()
        fun onDetach()
    }

    interface MovePresenter {
        fun onAttach(view: MoveView)
        fun onDetach()
        fun moveForward(minutes: Int)
        fun moveBackward(minutes: Int)
        fun selectLogId(logId: Long)
    }

    interface ScaleView {
        fun onAttach()
        fun onDetach()
    }

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