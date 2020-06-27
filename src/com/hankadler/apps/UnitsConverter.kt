package com.hankadler.apps

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import tornadofx.*

class UnitsConverter: App(MainView::class)

class MainView: View("Units Converter v0.2.0") {
    private val conversion = SimpleStringProperty(CONVERSIONS[0])
    private val isReversed = SimpleBooleanProperty(false)
    private val fromField = SimpleObjectProperty<TextField>()
    private val toField = SimpleObjectProperty<TextField>()
    private val fromText = SimpleStringProperty(String.format("%12s", "Inches: "))
    private val toText = SimpleStringProperty(String.format("%12s", "Centimeters: "))
    private val fromValue = SimpleDoubleProperty(0.0)
    private val toValue = SimpleDoubleProperty(0.0)
    private val leftImage = SimpleObjectProperty(IMAGES["in"])
    private val middleImage = SimpleObjectProperty(IMAGES["to"])
    private val rightImage = SimpleObjectProperty(IMAGES["cm"])
    private val convertButton = SimpleObjectProperty<Button>()
    private val controller = MainController()

    override val root = vbox {
        primaryStage.resizableProperty().value = false
        primaryStage.centerOnScreen()

        paddingAll = 8

        hbox {
            alignment = Pos.CENTER_LEFT
            paddingTop = 4

            label(text = "Units: ") {
                paddingLeft = 4
                paddingRight = 4
            }
            combobox(property = conversion, values = CONVERSIONS) {
                setOnAction {
                    controller.updateUnitImages(conversion.value, leftImage, rightImage)
                    controller.updateLabels(conversion.value, fromText, toText)
                    fromValue.value = 0.0
                    toValue.value = 0.0
                }
            }

            label(text = "Reverse: ") {
                paddingLeft = 8
                paddingRight = 4
            }
            radiobutton {
                val stateText = selectedProperty().stringBinding { if (it == true) "ON " else "OFF" }
                textProperty().bind(stateText)
                action {
                    isReversed.value = isSelected
                    controller.updateArrowImage(this, middleImage)
                    controller.updateFromAndToFieldState(this, fromField, toField)
                }
            }
        }

        hbox {
            alignment = Pos.CENTER

            imageview(leftImage)
            imageview(middleImage)
            imageview(rightImage)
        }

        hbox {
            paddingAll = 4
            paddingTop = 0

            hbox {
                alignment = Pos.CENTER_LEFT
                hgrow =  Priority.ALWAYS

                label(fromText) {
                    paddingRight = 4
                }
                fromField.value = textfield(fromValue) {
                    prefWidth = 75.0
                    shortcut("Enter") { convertButton.value.fire() }
                }
            }

            hbox {
                alignment = Pos.CENTER_RIGHT
                hgrow =  Priority.ALWAYS

                label(toText) {
                    paddingRight = 4
                }
                toField.value = textfield(toValue) {
                    isDisable = true
                    prefWidth = 75.0
                    shortcut("Enter") { convertButton.value.fire() }
                }
            }
        }

        hbox {
            alignment = Pos.CENTER
            paddingTop = 8
            paddingBottom = 4

            convertButton.value = button("Convert") {
                action {
                    if (!isReversed.value) {
                        toValue.value = controller.convert(
                            fromValue.value, CONVERSION_FACTORS[conversion.value]!!, isReversed.value
                        )
                    } else {
                        fromValue.value = controller.convert(
                            toValue.value, CONVERSION_FACTORS[conversion.value]!!, isReversed.value
                        )
                    }
                }
            }
        }
    }

    companion object {
        val CONVERSIONS = FXCollections.observableArrayList("in to cm", "lb to kg")
        val IMAGES = mapOf<String, Image>(
            "empty" to Image("empty150x150.png"),
            "in" to Image("paperclip150x150.png"),
            "cm" to Image("pin150x150.png"),
            "lb" to Image("1pound150x150.png"),
            "kg" to Image("1kilogram150x150.png"),
            "from" to Image("from75x75.png"),
            "to" to Image("to75x75.png")
        )
        val CONVERSION_FACTORS = mapOf<String, Double>("in to cm" to (2.54), "lb to kg" to (1 / 2.2))
    }
}

class MainController: Controller() {
    fun updateUnitImages(
        conversion: String,
        leftImage: SimpleObjectProperty<Image?>,
        rightImage: SimpleObjectProperty<Image?>
    ) {
        val matchFromAndToUnitsRegex = Regex("(\\w+)\\s+\\w+\\s+(\\w+)")

        val fromUnit = matchFromAndToUnitsRegex.matchEntire(conversion)?.groups?.get(1)?.value
        val toUnit = matchFromAndToUnitsRegex.matchEntire(conversion)?.groups?.get(2)?.value

        leftImage.value = MainView.IMAGES[fromUnit]
        rightImage.value = MainView.IMAGES[toUnit]
    }

    fun updateArrowImage(control: RadioButton, image: SimpleObjectProperty<Image?>) {
        image.value = if (control.isSelected) {
            MainView.IMAGES["from"]
        } else {
            MainView.IMAGES["to"]
        }
    }

    fun updateLabels(conversion: String, fromText: SimpleStringProperty, toText: SimpleStringProperty) {
        when (conversion) {
            "in to cm" -> {
                fromText.value = String.format("%12s", "Inches: ")
                toText.value = String.format("%12s", "Centimeters: ")
            }
            "lb to kg" -> {
                fromText.value = String.format("%12s", "Pounds: ")
                toText.value = String.format("%12s", "Kilograms: ")
            }
        }
    }

    fun updateFromAndToFieldState(
        control: RadioButton,
        fromField: SimpleObjectProperty<TextField>,
        toField: SimpleObjectProperty<TextField>
    ) {
        fromField.value.isDisable = control.isSelected
        toField.value.isDisable = !control.isSelected

        if (control.isSelected) {
            fromField.value.text = "0"
        } else {
            toField.value.text = "0"
        }
    }

    fun convert (input: Double, factor: Double, reverse: Boolean = false): Double {
        return if (reverse) {
            input / factor
        } else {
            input * factor
        }
    }
}

fun main() {
    launch<UnitsConverter>()
}
