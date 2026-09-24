package misc;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Reads and writes {@code minecraft:custom_data} on a stack.
 *
 * <p><b>Same file as M7 TAS's {@code nms/NBT}; keep them in step</b>: both write the SkyBlock id into the same
 * compound so one resource pack retextures both.
 *
 * <p>NMS, not the PDC, because a PDC key is always namespaced ({@code skyblock:id}) and readers want a bare
 * top-level {@code id}.
 *
 * @see SkyblockId
 */
public class NBT {
	public static net.minecraft.world.item.ItemStack modify(net.minecraft.world.item.ItemStack stack, Consumer<CompoundTag> consumer) {
		// ItemStack#update is too freaky
		CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update(consumer);
		stack.set(DataComponents.CUSTOM_DATA, data);
		return stack;
	}

	public static org.bukkit.inventory.ItemStack modify(org.bukkit.inventory.ItemStack stack, Consumer<CompoundTag> consumer) {
		return modify(CraftItemStack.asNMSCopy(stack), consumer).asBukkitCopy();
	}

	public static @Nullable String getId(org.bukkit.inventory.ItemStack stack) {
		return getId(CraftItemStack.asNMSCopy(stack));
	}

	public static @Nullable String getId(net.minecraft.world.item.ItemStack stack) {
		return getCustomData(stack).getString("id").orElse(null);
	}

	public static CompoundTag getCustomData(net.minecraft.world.item.ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}
}
