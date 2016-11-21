package lt.markmerkk.utils

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding
import javafx.scene.control.TextField
import javafx.util.Callback
import lt.markmerkk.interactors.IssueSearchInteractor
import org.controlsfx.control.textfield.AutoCompletionBinding

/**
 * @author mariusmerkevicius
 * @since 2016-10-03
 */
class AutoCompletionBindingIssues(
        private val issueSearchInteractor: IssueSearchInteractor,
        private val textField: TextField
) : AutoCompletionTextFieldBinding<String>(
        textField,
        Callback<AutoCompletionBinding.ISuggestionRequest, Collection<kotlin.String>> {
            param ->
            if (param.userText.trim().isEmpty()) {
                emptyList()
            } else {
                issueSearchInteractor.searchIssues(param.userText)
                        .toBlocking() // Has its own threading
                        .firstOrDefault(emptyList())
                        .map { it.toString() }
                        .take(30)
            }
        }
) {
    init {
        setDelay(500L)
        visibleRowCount = 10
        textField.widthProperty().addListener { observableValue, oldValue, newValue -> prefWidth = newValue.toDouble() }
    }
}