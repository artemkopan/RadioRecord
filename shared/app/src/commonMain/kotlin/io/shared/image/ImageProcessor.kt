package io.shared.image

expect class ImageProcessor {

    suspend fun <Source : Any> getImage(source: Source, width: Int, height: Int): Image

    suspend fun generatePalette(image: Image): Palette

    suspend fun getLightness(palette: Palette): ImageLightness

    suspend fun getDominantColor(palette: Palette, defaultColor: Color): Color

    suspend fun getDarkerColor(color: Color): Color

}
