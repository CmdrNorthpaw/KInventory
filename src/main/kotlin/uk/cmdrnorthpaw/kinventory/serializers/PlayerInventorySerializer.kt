package uk.cmdrnorthpaw.kinventory.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry
import uk.cmdrnorthpaw.kinventory.model.SerializablePlayerInventory
import uk.cmdrnorthpaw.kinventory.KInventory
import uk.cmdrnorthpaw.kinventory.model.SerializableArmourPiece
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack
import uk.cmdrnorthpaw.kinventory.model.SerializableItemStack.Companion.serializable

class PlayerInventorySerializer(private val player: PlayerEntity) : KSerializer<PlayerInventory> {
    override fun deserialize(decoder: Decoder): PlayerInventory {
        return decoder.decodeSerializableValue(SerializablePlayerInventory.serializer()).toInventory(player)
    }

    override val descriptor: SerialDescriptor = SerializablePlayerInventory.serializer().descriptor

    override fun serialize(encoder: Encoder, value: PlayerInventory) {
        val items = mutableListOf<SerializableItemStack>()
        val armour = mutableListOf<SerializableArmourPiece>()

        for (stack in value.main) {
            if (stack.isEmpty && !KInventory.Config.serializeEmpty) continue
            else items.add(SerializableItemStack(getKey(stack), stack.count, stack.tag?.toString()))
        }

        for (stack in value.armor) {
            if ((stack.isEmpty && !KInventory.Config.serializeEmpty) || stack.item !is ArmorItem) continue
            val armourItem = stack.item as ArmorItem
            armour.add(SerializableArmourPiece(getKey(stack), stack.tag?.toString(), armourItem.slotType))
        }

        val offHandStack = value.offHand[0].serializable()

        val inventory = SerializablePlayerInventory(items.toTypedArray(), armour.toTypedArray(), offHandStack, value.player.totalExperience)
        encoder.encodeSerializableValue(SerializablePlayerInventory.serializer(), inventory)
    }

    private fun getKey(stack: ItemStack) = Registry.ITEM.getKey(stack.item).toString()
}