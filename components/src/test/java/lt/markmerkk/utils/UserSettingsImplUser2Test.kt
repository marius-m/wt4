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
 * @since 2016-08-07
 */
class UserSettingsImplUser2Test {

    val coreSettings: HashSettings = mock()

    @Test
    fun noInput_returnEmpty() {
        val settings = UserSettingsImpl(coreSettings)

        settings.onAttach()
        val result = settings.username

        assertEquals("", result)
    }

    @Test
    fun validValue_returnValue() {
        doReturn("valid_value").whenever(coreSettings).get(UserSettingsImpl.USER)
        val settings = UserSettingsImpl(coreSettings)

        settings.onAttach()
        val result = settings.username

        assertEquals("valid_value", result)
        verify(coreSettings).load()
    }

    @Test
    fun saveValue_triggerSave() {
        val settings = UserSettingsImpl(coreSettings)

        settings.username = "new_value"

        assertEquals("new_value", settings.username)
        verify(coreSettings).save()
    }



}