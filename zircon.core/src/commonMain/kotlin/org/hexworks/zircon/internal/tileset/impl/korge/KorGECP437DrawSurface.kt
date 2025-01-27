package org.hexworks.zircon.internal.tileset.impl.korge

import korlibs.datastructure.IntArray2
import korlibs.korge.view.Container
import korlibs.korge.view.addTo
import korlibs.korge.view.fast.FSprite
import korlibs.korge.view.fast.FSprites
import korlibs.image.bitmap.Bitmap32
import korlibs.image.bitmap.BmpSlice
import korlibs.image.bitmap.bmp
import korlibs.image.bitmap.slice
import korlibs.image.color.Colors
import org.hexworks.zircon.api.data.CharacterTile
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size

class KorGECP437DrawSurface(
    tileSize: Size,
    gridSize: Size,
    private val glyphs: Array<out BmpSlice>
) : Container() {

    private val tileWidth = tileSize.width
    private val tileHeight = tileSize.height
    private val columns = gridSize.width
    private val rows = gridSize.height

    private val bgBitmap = Bitmap32(tileWidth, tileHeight, Colors.WHITE).slice()

    private val bgFSprites = FSprites(columns * rows)
    private val bgView = bgFSprites.createView(bgBitmap.bmp).addTo(this)
    private val bgMat = IntArray2(columns, rows) { bgFSprites.alloc().id }

    private val fgFSprites = FSprites(columns * rows)
    private val fgView = fgFSprites.createView(glyphs.first().bmp).addTo(this)
    private val fgMat = IntArray2(columns, rows) { fgFSprites.alloc().id }

    init {
        for (row in 0 until rows) {
            for (col in 0 until columns) {
                fgFSprites.apply {
                    val fsprite = FSprite(fgMat[col, row])
                    fsprite.x = col * tileWidth.toFloat()
                    fsprite.y = row * tileHeight.toFloat()
                }
                bgFSprites.apply {
                    val fsprite = FSprite(bgMat[col, row])
                    fsprite.x = col * tileWidth.toFloat()
                    fsprite.y = row * tileHeight.toFloat()
                    fsprite.colorMul = Colors.BLACK
                    fsprite.setTex(bgBitmap)
                }
            }
        }
    }

    fun drawTile(tile: CharacterTile, position: Position) {
        val character = tile.character
        val foregroundColor = tile.styleSet.foregroundColor
        val backgroundColor = tile.styleSet.backgroundColor
        val (x, y) = position
        bgFSprites.apply {
            FSprite(bgMat[x, y]).colorMul = backgroundColor.toRGBA()
        }
        fgFSprites.apply {
            val fsprite = FSprite(fgMat[x, y])
            fsprite.colorMul = foregroundColor.toRGBA()
            fsprite.setTex(glyphs[character.code])
        }
    }
}