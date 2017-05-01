package lt.markmerkk.ui_2

import com.airhacks.afterburner.views.FXMLView
import com.jfoenix.controls.JFXListView
import com.jfoenix.controls.JFXPopup
import com.jfoenix.svg.SVGGlyph
import com.jfoenix.svg.SVGGlyphLoader
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import java.net.URL
import java.util.*

class TreeContextMenuPresenter : Initializable {

    @FXML lateinit var jfxRoot: JFXPopup
    @FXML lateinit var jfxListView: JFXListView<Label>
    @FXML lateinit var jfxLabelUpdate: Label
    @FXML lateinit var jfxLabelDelete: Label
    @FXML lateinit var jfxLabelClone: Label

    private val glyphUpdate = glyphUpdate().apply { setSize(GLYPH_SIZE, GLYPH_SIZE) }
    private val glyphDelete = glyphDelete().apply { setSize(GLYPH_SIZE, GLYPH_SIZE) }
    private val glyphClone = glyphClone().apply { setSize(GLYPH_SIZE, GLYPH_SIZE) }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        jfxLabelUpdate.graphic = glyphUpdate
        jfxLabelDelete.graphic = glyphDelete
        jfxLabelClone.graphic = glyphClone
        jfxListView.selectionModel.selectedIndexProperty().addListener { observable, oldValue, newValue ->
            println("Selected " + newValue)
            jfxRoot.close()
        }
    }

    //region Glyphs

    private fun glyphUpdate(): SVGGlyph {
        return SVGGlyph(
                -1,
                "update",
                "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z",
                Color.BLACK
        )
    }

    private fun glyphDelete(): SVGGlyph {
        return SVGGlyph(
                -1,
                "delete",
                "M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z",
                Color.BLACK
        )
    }

    private fun glyphClone(): SVGGlyph {
        return SVGGlyph(
                -1,
                "delete",
                "M16.5 12c1.38 0 2.49-1.12 2.49-2.5S17.88 7 16.5 7C15.12 7 14 8.12 14 9.5s1.12 2.5 2.5 2.5zM9 11c1.66 0 2.99-1.34 2.99-3S10.66 5 9 5C7.34 5 6 6.34 6 8s1.34 3 3 3zm7.5 3c-1.83 0-5.5.92-5.5 2.75V19h11v-2.25c0-1.83-3.67-2.75-5.5-2.75zM9 13c-2.33 0-7 1.17-7 3.5V19h7v-2.25c0-.85.33-2.34 2.37-3.47C10.5 13.1 9.66 13 9 13z",
                Color.BLACK
        )
    }

    //endregion

    companion object {
        private const val GLYPH_SIZE: Double = 16.0
    }

}