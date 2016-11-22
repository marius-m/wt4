package lt.markmerkk.mvp

/**
 * @author mariusmerkevicius
 * @since 2016-11-22
 */
interface SearchOpenMvp {
    interface View {
        fun showOpenButton()
        fun hideOpenButton()
    }

    interface Presenter {
        fun handleInputChange(input: String)
        fun open(input: String)
    }

}