package lt.markmerkk.widgets.help.html

import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class HtmlParserTest {

    private lateinit var htmlParser: HtmlParser2
    private val inputRaw = """
<h1>Recent ticket filter</h1>
<ul>
    <li>Use ARROW KEYS to move</li>
    <li>Hold CTRL/CMD + ARROW KEYS to expand / subtract</li>
    <li>Hold SHIFT + ARROW KEYS to enter 'turbo mode'</li>
    <li>Press CTRL/CMD + S to save region</li>
    <li>Holding 'SHIFT' will lock y axis when dragging region with a mouse</li>
    <li>Holding 'ALT' will lock x axis when dragging region with a mouse</li>
    <li>To edit multiple components: press secondary mouse button on another region, press 'Update'</li>
</ul>
    """.trimIndent()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        htmlParser = HtmlParser2()
    }

    @Test
    fun valid() {
        // Assemble
        // Act
        val result = htmlParser.parse(inputRaw)

        // Assert
        Assertions.assertThat(result).isEqualTo(true)
    }
}