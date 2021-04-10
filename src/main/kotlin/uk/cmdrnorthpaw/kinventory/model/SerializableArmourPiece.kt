package uk.cmdrnorthpaw.kinventory.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.entity.EquipmentSlot

@Serializable
class SerializableArmourPiece(
    private val armourItem: String,
    private val armourTags: String?,
    val slot: EquipmentSlot
) : SerializableItemStack(armourItem, 1, armourTags)