package lt.markmerkk.widgets.main

interface MainContract {

    interface View { }
    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
    }
}