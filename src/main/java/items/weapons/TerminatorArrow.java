package items.weapons;

import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * Vanilla's arrow in every respect but one: it does not let the hit it just landed be mistaken for a miss.
 *
 * <p><b>Why it has to exist.</b> This plugin cancels the vanilla damage event and recomputes, so
 * {@code hurtOrSimulate} returns false on every single arrow hit and {@code AbstractArrow.onHitEntity} takes
 * its MISS branch on the way out: {@code deflect(ProjectileDeflection.REVERSE, ...)}, which scales the motion
 * and spins the yaw 180, and then {@code setDeltaMovement(delta.scale(0.2))}. That is the bounce off a mob,
 * and on a piercing arrow it is visible - the plugin used to put the velocity back a tick later, by which
 * time the client had already been sent the reversed vector and rendered the flip.
 *
 * <p><b>Why it is shaped like this.</b> The motion and rotation are saved before the super call and put back
 * after it, rather than vanilla's hit logic being copied out here. Everything in that method is wanted
 * exactly as vanilla wrote it - the pierce bookkeeping in {@code piercingIgnoreEntityIds}, the impact sound,
 * Flame, the discard - and a copy would have to be re-read against the jar every version. Undone in the SAME
 * tick, so the reversed vector never reaches a client and there is nothing to see.
 *
 * <p>An arrow vanilla removed on the way through is left alone: there is nothing to put back.
 *
 * <p>Only arrows the plugin spawns itself can be this class, so a vanilla bow's piercing arrow still needs
 * {@code CustomDamage.customMobs}'s next-tick velocity restore. A chunk that saves and reloads one of these
 * gets a plain vanilla arrow back, which is fine: they live about a second.
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
