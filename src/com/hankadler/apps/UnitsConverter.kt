package com.hankadler.apps

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

class UnitsConverter: App(MainView::class)

class MainView: View() {
    private val UNITS = FXCollections.observableArrayList("in to ft", "lb to kg")
    private val selectedUnit = SimpleStringProperty(UNITS[0])
    private val input = SimpleDoubleProperty()
    private val output = SimpleDoubleProperty()

    override val root = vbox(spacing = 8.0) {
        title = "Units Converter v0.1.0"
        paddingAll = 8.0

        hbox(spacing = 4.0) {
            label("Units: ")
            alignment = Pos.CENTER_RIGHT
            combobox(selectedUnit, UNITS)
        }

        hbox(spacing = 4.0) {
            label("Input: ") {
                style { fontWeight = FontWeight.BOLD }
            }
            alignment = Pos.CENTER_RIGHT
            textfield(input) {
                prefWidth = 196.0
            }
        }

        hbox(spacing = 4.0) {
            label("Output: ") {
                style { fontWeight = FontWeight.BOLD}
            }
            alignment = Pos.CENTER_RIGHT
            textfield(output) {
                isDisable = true
                prefWidth = 196.0
            }
        }

        hbox {
            alignment = Pos.CENTER
            button("Convert") {
                action {
                    output.set(
                        ConvertController()
                            .convert(input.value, selectedUnit.value))
                }
            }
        }
    }
}

class ConvertController: Controller() {
    fun convert(input: Double, conversionType: String): Double {
        return when (conversionType) {
            "in to ft" -> Converter.inchesToFeet(input)
            "lb to kg" -> Converter.poundsToKilograms(input)
            else -> input
        }
    }
}

object Converter {
    fun inchesToFeet(inches: Double): Double {
        return inches / 12
    }

    fun poundsToKilograms(pounds: Double): Double {
        return pounds / 2.2
    }
}

fun main() {
    launch<UnitsConverter>()
}
