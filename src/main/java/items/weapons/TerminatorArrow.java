package items.weapons;

import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Vanilla arrow that doesn't treat its hit as a miss. We cancel the damage event, so {@code hurtOrSimulate}
 * returns false and {@code onHitEntity} takes the MISS branch: {@code deflect(REVERSE)} plus
 * {@code delta.scale(0.2)}, a visible bounce on a piercing arrow. Restoring velocity a tick later was too late,
 * the client had already rendered the flip.
 * Saves motion and rotation around the super call instead of copying vanilla's hit logic (pierce bookkeeping,
 * sound, Flame, discard), so nothing has to be re-checked each version. Same tick, so no client sees it.
 * Vanilla bow arrows still rely on {@code CustomDamage.customMobs}'s next-tick restore. A chunk reload turns
 * these into plain arrows, fine since they live about a second.
 */
public class TerminatorArrow extends Arrow {
	public TerminatorArrow(Level level, double x, double y, double z, ItemStack pickup, @Nullable ItemStack weapon) {
		super(level, x, y, z, pickup, weapon);
	}

	@Override
	protected void onHitEntity(EntityHitResult hit) {
		Vec3 motion = getDeltaMovement();
		float yaw = getYRot();
		float pitch = getXRot();
		super.onHitEntity(hit);
		if(isRemoved()) return;
		setDeltaMovement(motion);
		setYRot(yaw);
		setXRot(pitch);
	}
}
