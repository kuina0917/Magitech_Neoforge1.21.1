package net.kuina.magitech.entity.custom;

import net.kuina.magitech.entity.magitechentities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LivingEntity;



public class ZoltrakProjectile extends ThrowableProjectile implements ItemSupplier {

    public ZoltrakProjectile(EntityType<? extends ZoltrakProjectile> type, Level level) {
        super(type, level);
    }

    public ZoltrakProjectile(Level level, LivingEntity shooter) {
        super(magitechentities.ZOLTRAK_PROJECTILE.get(), shooter, level);
    }
    private Vec3 startPos;  // 発射開始位置
    private static final double MAX_RANGE = 30.0; // 有効射程距離（ブロック単位）
    private static final double HOMING_RADIUS = 10.0;         // 追尾可能な半径
    private static final double HOMING_STRENGTH = 0.8;         // 追尾の強さ（0〜1）
    private LivingEntity homingTarget = null;                  // ロックオン対象




    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // データ同期なし
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        this.discard(); // 衝突時に削除
    }
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        Entity target = result.getEntity();              // 命中した対象
        Entity owner = this.getOwner();                  // 発射元（プレイヤー）

        // ダメージソースを設定（魔法ダメージ扱い）
        DamageSource source = owner != null
                ? this.damageSources().indirectMagic(this, owner)
                : this.damageSources().magic();

        // ダメージ量を設定（例: 6.0F）
        target.hurt(source, 6.0F);

        this.discard();  // 命中後に発射物を消す
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                    this.getX(), this.getY(), this.getZ(),
                    1, 0, 0, 0, 0);
        }
    }

    @Override
    public ItemStack getItem() {
        // 表示されるアイテム（仮にエンダーパール）
        return new ItemStack(Items.ENDER_PEARL);
    }
    @Override
    public void tick() {
        super.tick();

        // 射程の初期化と範囲チェック
        if (startPos == null) startPos = this.position();
        if (this.position().distanceTo(startPos) > MAX_RANGE) {
            this.discard();
            return;
        }

        // ホーミング処理（角度制限あり）
        if (!this.level().isClientSide) {
            Entity owner = this.getOwner();
            if (owner instanceof LivingEntity livingOwner) {
                Vec3 ownerLook = livingOwner.getLookAngle().normalize();
                double maxAngle = Math.toRadians(45);

                if (homingTarget == null || !homingTarget.isAlive()) {
                    homingTarget = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(HOMING_RADIUS))
                            .stream()
                            .filter(entity -> entity != this.getOwner())
                            .filter(LivingEntity::isAlive)
                            .filter(entity -> {
                                Vec3 toTarget = entity.position().subtract(this.position()).normalize();
                                return ownerLook.dot(toTarget) >= Math.cos(maxAngle);
                            })
                            .min((a, b) -> Double.compare(this.distanceToSqr(a), this.distanceToSqr(b)))
                            .orElse(null);
                }

                if (homingTarget != null) {
                    Vec3 direction = homingTarget.position().add(0, homingTarget.getBbHeight() / 2, 0).subtract(this.position()).normalize();
                    Vec3 newVelocity = this.getDeltaMovement().add(direction.scale(HOMING_STRENGTH)).normalize().scale(0.9);
                    this.setDeltaMovement(newVelocity);
                }
            }
        }

        // クライアント：パーティクル
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }}
