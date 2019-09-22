package lt.markmerkk.widgets.settings

/**
 * Represents what should be shown on authorization view
 */
data class AuthViewModel(
        val showContainerWebview: Boolean,
        val showContainerStatus: Boolean,
        val showStatusEmoticon: StatusEmoticon,
        val showButtonSetupNew: Boolean,
        val textStatus: String
) {

    enum class StatusEmoticon {
        HAPPY,
        NEUTRAL,
        SAD,
        ;
    }

}