package uk.cmdrnorthpaw.kinventory.model

import kotlinx.serialization.Serializable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack.Companion.serializable
import uk.cmdrnorthpaw.kinventory.serializers.NotSerializable

/**
 * Represents any [Inventory] that needs to be serialized.
 * In its base form, currently supports inventories with [ItemStack]s in them.
 * Fluid support will be added when a fluid storage API is merged with Fabric.
 * @param [T]: The [Inventory] that this class represents.
 * @property items: A [List] of [SerializableItemStack] that represents
 * */
@Serializable
abstract class SerializableInventory<T: @kotlinx.serialization.Serializable(NotSerializable::class) Inventory>(open val items: List<SerializableItemStack>) {
    /**
     * Converts this [SerializableInventory] to an actual [Inventory] of type [T]
     * For polymorphism reasons this has to be called without any parameters,
     * so anything this inventory needs to be converted must be part of the class itself.
     * */
    abstract fun toInventory(): T

    interface SerializableInventoryCompanion<T: Inventory, S: SerializableInventory<T>> {
        fun getSerializable(from: T): S
    }
}