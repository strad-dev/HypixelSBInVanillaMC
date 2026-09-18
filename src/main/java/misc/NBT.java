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
 * <p><b>The same file as M7 TAS's {@code nms/NBT}</b>, and it has to stay that way: both plugins write the
 * SkyBlock item id into the same compound so one client-side resource pack retextures both. Keep them in
 * step.
 *
 * <p>NMS rather than {@code ItemMeta}'s persistent data container, because a PDC key is always namespaced
 * ({@code skyblock:id}) and what reads these is looking for a bare {@code id} at the top level.
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
