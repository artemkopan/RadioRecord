package io.radio.shared.data.image


interface ImageProcessor {

    suspend fun <Source : Any> getImage(source: Source, width: Int, height: Int): Image

    suspend fun generatePalette(image: Image): Palette

    suspend fun getLightness(palette: Palette): ImageLightness

    suspend fun getDominantColor(palette: Palette, defaultColor: Int): Int

    suspend fun getDarkerColor(color: Int): Int

}
