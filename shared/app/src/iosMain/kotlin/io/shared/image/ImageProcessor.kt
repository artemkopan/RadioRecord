package io.shared.image

actual class ImageProcessor {

    actual suspend fun <Source : Any> getImage(
        source: Source,
        width: Int,
        height: Int
    ): Image {
        TODO("Not yet implemented")
    }

    actual suspend fun generatePalette(image: Image): Palette {
        TODO("Not yet implemented")
    }

    actual suspend fun getLightness(palette: Palette): ImageLightness {
        TODO("Not yet implemented")
    }

    actual suspend fun getDominantColor(
        palette: Palette,
        defaultColor: Color
    ): Color {
        TODO("Not yet implemented")
    }

    actual suspend fun getDarkerColor(color: Color): Color {
        TODO("Not yet implemented")
    }

}