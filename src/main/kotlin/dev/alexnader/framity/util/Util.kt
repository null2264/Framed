package dev.alexnader.framity.util

import dev.alexnader.framity.LOGGER
import net.minecraft.item.ItemStack
import net.minecraft.state.State
import net.minecraft.state.property.Property
import net.minecraft.util.math.Vec3d
import java.util.*

infix fun <A, B, C> ((A) -> B).andThen(f: (B) -> C): (A) -> C =
    { arg -> f(this(arg)) }

fun <T> Optional<T>.orNull(): T? =
    this.orElse(null)

operator fun Vec3d.minus(rhs: Vec3d): Vec3d =
    this.subtract(rhs)

fun <N> minMax(a: N, b: N) where N: Number, N: Comparable<N> =
    if (a < b) {
        Pair(a, b)
    } else {
        Pair(b, a)
    }

/**
 * Checks if the value of [prop] for [a] and [b] is equal. Assumes [prop] in [a], checks [prop] in [b].
 */
fun <O, S, T: Comparable<T>> propEq(prop: Property<T>, a: State<O, S>, b: State<O, S>) =
    prop in b && a[prop] == b[prop]

/**
 * Returns a function that checks if all properties, except for those in [toIgnore], are equal for two states.
 */
fun <O, S> equalsIgnoring(toIgnore: Set<Property<*>>) =
    @Suppress("UNCHECKED_CAST")
    { a: State<O, S>, b: State<O, S> -> a.properties.asSequence()
        .filter { it !in toIgnore }
        .all { propEq(it as Property<Comparable<Comparable<*>>>, a, b) }
    }

object ItemStackEquality : Equality<ItemStack> {
    override fun checkEquality(a: ItemStack, b: ItemStack) = ItemStack.areEqual(a, b)

    override val name: String = "ItemStack::areEqual"
}

inline fun <T> T.log(message: T.() -> Any): T {
    LOGGER.info(this.message())
    return this
}
