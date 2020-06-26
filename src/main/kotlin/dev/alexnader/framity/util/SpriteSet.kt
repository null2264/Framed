package dev.alexnader.framity.util

import dev.alexnader.framity.mixin.AccessibleBakedQuad
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.texture.MissingSprite
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.util.math.Direction
import java.util.*
import kotlin.collections.HashMap

/**
 * A defaulted mutable map from [Direction] to [Sprite].
 */
class SpriteSet(private val defaultSprite: Sprite) {
    companion object {
        @Suppress("deprecation")
        val FALLBACK_SPRITE: Sprite =
            MinecraftClient.getInstance()
                .getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX)
                .apply(MissingSprite.getMissingSpriteId())
    }

    private val quads = HashMap<Direction?, List<BakedQuad>>()
    var isDefault = true
        private set

    fun clear() {
        isDefault = true
    }

    fun prepare(model: BakedModel, rand: Random?) {
        this.quads.clear()
        isDefault = false

        (0..6).map(ModelHelper::faceFromIndex).forEach { dir ->
            model.getQuads(null, dir, rand)
                ?.takeIf { it.isNotEmpty() }
                ?.let { this.quads[dir] = mutableListOf<BakedQuad>().apply { this.addAll(it) } }
        }
    }

    operator fun get(dir: Direction, index: Int): Sprite {
        return if (this.isDefault) {
            defaultSprite
        } else {
            this.getQuad(dir, index)
                ?.let { (it as AccessibleBakedQuad).sprite }
                ?: FALLBACK_SPRITE
        }
    }

    fun getQuad(dir: Direction, index: Int): BakedQuad? {
        val quadList = this.quads[dir] ?: return null

        if (index >= quadList.size) {
            return null
        }

        return quadList[index]
    }

    fun hasColor(dir: Direction, index: Int): Boolean {
        return if (this.isDefault) {
            false
        } else {
            this.getQuad(dir, index)?.hasColor() ?: false
        }
    }

    fun getQuadCount(dir: Direction) =
        this.quads[dir]?.size ?: -1
}