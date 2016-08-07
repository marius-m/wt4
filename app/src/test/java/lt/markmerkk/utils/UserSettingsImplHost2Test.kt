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
class UserSettingsImplHost2Test {

    val coreSettings: HashSettings = mock()

    @Test
    fun noInput_returnEmpty() {
        val settings = UserSettingsImpl(coreSettings)

        settings.onStart()
        val result = settings.host

        assertEquals("", result)
    }

    @Test
    fun validValue_returnValue() {
        doReturn("valid_value").whenever(coreSettings).get(UserSettingsImpl.HOST)
        val settings = UserSettingsImpl(coreSettings)

        settings.onStart()
        val result = settings.host

        assertEquals("valid_value", result)
        verify(coreSettings).load()
    }

    @Test
    fun saveValue_triggerSave() {
        val settings = UserSettingsImpl(coreSettings)

        settings.host = "new_value"

        assertEquals("new_value", settings.host)
        verify(coreSettings).save()
    }



}