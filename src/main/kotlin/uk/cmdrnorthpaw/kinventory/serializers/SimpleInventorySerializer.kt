package uk.cmdrnorthpaw.kinventory.serializers

import net.minecraft.inventory.SimpleInventory
import uk.cmdrnorthpaw.kinventory.inventory.SerializableSimpleInventory
import uk.cmdrnorthpaw.kinventory.model.SurrogateInventorySerializer


/**
 * A convenience object that adds a serializer for [SimpleInventory].
 * Uses surrogate serialization and is basically identical to [SerializableSimpleInventory]
 * */
object SimpleInventorySerializer : SurrogateInventorySerializer<SimpleInventory, SerializableSimpleInventory>(
    SerializableSimpleInventory.serializer(), SerializableSimpleInventory.Companion
)