package org.connect4jet.project

import org.connect4jet.project.ui.App
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        App()
    }
}