package uk.cmdrnorthpaw.kinventory.serializers

import net.minecraft.inventory.SimpleInventory
import uk.cmdrnorthpaw.kinventory.inventory.SerializableSimpleInventory
import uk.cmdrnorthpaw.kinventory.model.SurrogateInventorySerializer

object SimpleInventorySerializer : SurrogateInventorySerializer<SimpleInventory, SerializableSimpleInventory>(
    SerializableSimpleInventory.serializer(), SerializableSimpleInventory.Companion
)