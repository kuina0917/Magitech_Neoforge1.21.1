package net.kuina.magitech.entity.custom;

import net.kuina.magitech.entity.magitechentities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class MagicCircle extends Entity implements IEntityWithComplexSpawn {
    private int lifeTicks = 10;

    public MagicCircle(EntityType<? extends MagicCircle> type, Level level) {
        super(type, level);
    }

    public MagicCircle(Level level, double x, double y, double z, float yaw) {
        this(magitechentities.MAGIC_CIRCLE.get(), level);
        this.setPos(x, y, z);
        this.setYRot(yaw);
        this.setYHeadRot(yaw);
        this.setYBodyRot(yaw);
    }

    public void fireProjectile(LivingEntity caster) {
        if (!this.level().isClientSide) {
            ZoltrakProjectile proj = new ZoltrakProjectile(this.level(), caster);
            proj.setPos(this.getX(), this.getY() + 0.1, this.getZ());
            proj.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, 1.5F, 0.0F);
            this.level().addFreshEntity(proj);
        }
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf) {}

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buf) {}

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && --lifeTicks <= 0) {
            this.discard();
        }
    }
}
