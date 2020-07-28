package io.radio.shared.feature.image


interface ImageProcessor {

    suspend fun <Source : Any> getImage(source: Source, width: Int, height: Int): Image

    suspend fun generatePalette(image: Image): Palette

    suspend fun getLightness(palette: Palette): ImageLightness

    suspend fun getDominantColor(palette: Palette, defaultColor: Int): Int

    suspend fun getDarkerColor(color: Int): Int

    suspend fun getDominantColor(
        source: Any,
        defaultColor: Int,
        width: Int = 100,
        height: Int = 100
    ): Int {
        val image = getImage(source, 100, 100)
        val palette = generatePalette(image)
        return getDominantColor(palette, defaultColor)
    }

}
