package lt.markmerkk.ui_2.views.date

interface DateChangeContract {

    interface View {
        fun onAttach()
        fun onDetach()
        fun render(title: String)
    }

    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
        fun onClickNext()
        fun onClickPrev()
        fun onClickDate()
    }

}