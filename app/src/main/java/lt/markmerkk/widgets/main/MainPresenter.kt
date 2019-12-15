package lt.markmerkk.widgets.main

class MainPresenter(): MainContract.Presenter {

    private var view: MainContract.View? = null

    override fun onAttach(view: MainContract.View) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

}