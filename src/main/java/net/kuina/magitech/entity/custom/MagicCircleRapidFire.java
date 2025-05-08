package net.kuina.magitech.entity.custom;

import net.kuina.magitech.entity.magitechentities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class MagicCircleRapidFire extends Entity implements IEntityWithComplexSpawn {

    private LivingEntity caster;
    private int tickCount = 0;
    private int fireIndex = 0;

    private static final Vec3[] FIRE_POINTS = new Vec3[]{
            new Vec3(0.6, 0.1, 0.0),
            new Vec3(-0.6, 0.1, 0.0),
            new Vec3(0.0, 0.1, 0.6),
            new Vec3(0.0, 0.1, -0.6),
            new Vec3(0.45, 0.1, 0.45),
            new Vec3(-0.45, 0.1, -0.45),
    };

    // 同期用データ
    private static final EntityDataAccessor<Float> DATA_YAW =
            SynchedEntityData.defineId(MagicCircleRapidFire.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_OFFSET_X =
            SynchedEntityData.defineId(MagicCircleRapidFire.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_OFFSET_Y =
            SynchedEntityData.defineId(MagicCircleRapidFire.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_OFFSET_Z =
            SynchedEntityData.defineId(MagicCircleRapidFire.class, EntityDataSerializers.FLOAT);

    public MagicCircleRapidFire(EntityType<? extends MagicCircleRapidFire> type, Level level) {
        super(type, level);
    }

    public MagicCircleRapidFire(Level level, LivingEntity caster) {
        this(magitechentities.MAGIC_CIRCLE_RAPIDFIRE.get(), level);
        this.caster = caster;
        this.setPos(caster.getX(), caster.getY() + 1.0, caster.getZ());

        // 初期同期データを設定（サーバー側のみ）
        if (!level.isClientSide) {
            Vec3 offset = new Vec3(0.0, 1.5, -1.2).yRot((float) Math.toRadians(-caster.getYRot()));
            this.entityData.set(DATA_YAW, caster.getYRot());
            this.entityData.set(DATA_OFFSET_X, (float) offset.x);
            this.entityData.set(DATA_OFFSET_Y, (float) offset.y);
            this.entityData.set(DATA_OFFSET_Z, (float) offset.z);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_YAW, 0f);
        builder.define(DATA_OFFSET_X, 0f);
        builder.define(DATA_OFFSET_Y, 1.5f);
        builder.define(DATA_OFFSET_Z, -1.2f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void tick() {
        super.tick();

        if (caster == null || !caster.isAlive() || !caster.isUsingItem() || caster.getUseItem() != caster.getMainHandItem()) {
            this.discard();
            return;
        }

        if (!level().isClientSide) {
            float yaw = caster.getYRot();
            Vec3 offset = new Vec3(0.0, 1.5, -1.2).yRot((float) Math.toRadians(-yaw));
            this.entityData.set(DATA_YAW, yaw);
            this.entityData.set(DATA_OFFSET_X, (float) offset.x);
            this.entityData.set(DATA_OFFSET_Y, (float) offset.y);
            this.entityData.set(DATA_OFFSET_Z, (float) offset.z);
        }

        // 発射処理（4tickごと）
        if (!level().isClientSide && tickCount++ % 4 == 0) {
            fireProjectile();
        }

        // クライアント・サーバー共通で位置更新（renderのため）
        float ox = this.entityData.get(DATA_OFFSET_X);
        float oy = this.entityData.get(DATA_OFFSET_Y);
        float oz = this.entityData.get(DATA_OFFSET_Z);
        Vec3 basePos = caster.position().add(ox, oy, oz);
        this.setPos(basePos);
    }

    private void fireProjectile() {
        Vec3 offset = FIRE_POINTS[fireIndex % FIRE_POINTS.length];
        Vec3 rotated = offset.yRot((float) Math.toRadians(-caster.getYRot()));
        Vec3 firePos = this.position().add(rotated);

        ZoltrakProjectile proj = new ZoltrakProjectile(level(), caster);
        proj.setPos(firePos);
        proj.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, 1.5F, 0.3F);
        level().addFreshEntity(proj);

        fireIndex++;
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buf) {}
    @Override
    public void readSpawnData(RegistryFriendlyByteBuf buf) {}

    // Rendererから使うためにGetterを追加してもOK（必要なら）
    public float getRenderYaw() {
        return this.entityData.get(DATA_YAW);
    }
}
