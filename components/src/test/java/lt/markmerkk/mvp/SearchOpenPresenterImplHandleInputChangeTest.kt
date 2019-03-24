package lt.markmerkk.mvp

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-11-22
 */
class SearchOpenPresenterImplHandleInputChangeTest {
    val view: SearchOpenMvp.View = mock()
    val interactor: HostServicesInteractor = mock()
    val presenter = SearchOpenPresenterImpl(view, interactor)

    @Test
    fun empty_hideButton() {
        // Arrange
        // Act
        presenter.handleInputChange("")

        // Assert
        verify(view).hideOpenButton()
    }

    @Test
    fun notTask_hideButton() {
        // Arrange
        // Act
        presenter.handleInputChange("cant_be_parsed_to_task")

        // Assert
        verify(view).hideOpenButton()
    }

    @Test
    fun notTask2_hideButton() {
        // Arrange
        // Act
        presenter.handleInputChange("111")

        // Assert
        verify(view).hideOpenButton()
    }

    @Test
    fun validTask_showButton() {
        // Arrange
        // Act
        presenter.handleInputChange("wt-41")

        // Assert
        verify(view).showOpenButton()
    }

}