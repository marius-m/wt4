package lt.markmerkk.views;

import com.jfoenix.controls.JFXTextArea;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Forms {@link JFXTextArea} height by ghost label to enable expanding of the text area
 * whenever entering text
 */
public class JFXScrollFreeTextArea extends StackPane {

    private Logger logger = LoggerFactory.getLogger(JFXScrollFreeTextArea.class);
    private Label label;
    private JFXTextArea textArea;
    private Character enterChar = new Character((char) 10);
    private Region content;
    private SimpleDoubleProperty contentHeight = new SimpleDoubleProperty();

    private static final double TOP_PADDING = 3D;
    private static final double BOTTOM_PADDING = 6D;

    public JFXScrollFreeTextArea() {
        super();
        configure();
    }

    private void configure() {
        setAlignment(Pos.TOP_LEFT);
        this.textArea = new JFXTextArea() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (content == null) {
                    content = (Region) lookup(".content");
                    contentHeight.bind(content.heightProperty());
                    content.heightProperty().addListener((paramObservableValue, paramT1, paramT2) -> {
                        logger.debug("Content: {}", paramT2.doubleValue());
                    });
                }
            }

            ;
        };
        this.textArea.setWrapText(true);
        this.label = new Label();
        this.label.setWrapText(true);
        this.label.prefWidthProperty().bind(this.textArea.widthProperty());
        this.label.setTextFill(Color.WHITE);
        label.textProperty().bind(new StringBinding() {
            {
                bind(textArea.textProperty());
            }

            @Override
            protected String computeValue() {
                final String inputText = textArea.getText();
                if (inputText != null && !inputText.isEmpty()) {
                    final Character lastChar = inputText.charAt(inputText.length() - 1);
                    if (lastChar.equals(enterChar)) {
                        return inputText + " ";
                    }
                }
                return inputText;
            }
        });
        label.heightProperty().addListener((paramObservableValue, paramT1, paramT2) -> {
            layoutForNewLine();
        });
        getChildren().addAll(label, textArea);
    }

    private void layoutForNewLine() {
        double newHeight = label.getHeight() + TOP_PADDING + BOTTOM_PADDING;
        textArea.setPrefHeight(newHeight);
        textArea.setMinHeight(textArea.getPrefHeight());
    }

    public JFXTextArea getTextArea() {
        return textArea;
    }

}