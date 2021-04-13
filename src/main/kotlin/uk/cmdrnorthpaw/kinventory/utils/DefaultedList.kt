package uk.cmdrnorthpaw.kinventory.utils

import net.minecraft.util.collection.DefaultedList
import java.lang.UnsupportedOperationException

/**
 * Kotlin's incompatibility with [DefaultedList] means that you have to use the set() function to add elements instead of add()
 * or you risk an [UnsupportedOperationException] which does not print to the console
 * This convenience function, similar to [addAll], uses set() to add multiple elements to the list, avoiding the error.
 * */
fun <E: Any> DefaultedList<E>.setAll(elements: Iterable<E>): List<E> {
    elements.forEachIndexed { index, e -> this.set(index, e) }
    return elements.toList()
}