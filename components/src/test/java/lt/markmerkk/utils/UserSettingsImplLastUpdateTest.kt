package lt.markmerkk.utils

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.*
import org.junit.Test

/**
 * @author mariusmerkevicius
 * *
 * @since 2016-08-13
 */
class UserSettingsImplLastUpdateTest {

    val coreSettings: HashSettings = mock()

    @Test
    fun noInput_returnDefault() {
        val settings = UserSettingsImpl(coreSettings)

        settings.onAttach()
        val result = settings.lastUpdate

        assertEquals(-1, result)
    }

    @Test
    fun validValue_returnValue() {
        doReturn("1").whenever(coreSettings).get(UserSettingsImpl.LAST_UPDATE)
        val settings = UserSettingsImpl(coreSettings)

        settings.onAttach()
        val result = settings.lastUpdate

        assertEquals(1, result)
        verify(coreSettings).load()
    }

    @Test
    fun malformValue_returnDefault() {
        doReturn("malformed_value").whenever(coreSettings).get(UserSettingsImpl.LAST_UPDATE)
        val settings = UserSettingsImpl(coreSettings)

        settings.onAttach()
        val result = settings.lastUpdate

        assertEquals(-1, result)
        verify(coreSettings).load()
    }

    @Test
    fun saveValue_triggerSave() {
        val settings = UserSettingsImpl(coreSettings)

        settings.lastUpdate = 1
        settings.onDetach()

        assertEquals(1, settings.lastUpdate)
        verify(coreSettings).save()
    }
}