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


public class ZoltrakProjectile extends ThrowableProjectile implements ItemSupplier {

    public ZoltrakProjectile(EntityType<? extends ZoltrakProjectile> type, Level level) {
        super(type, level);
    }

    public ZoltrakProjectile(Level level, LivingEntity shooter) {
        super(magitechentities.ZOLTRAK_PROJECTILE.get(), shooter, level);
    }
    private Vec3 startPos;  // 発射開始位置
    private static final double MAX_RANGE = 20.0; // 有効射程距離（ブロック単位）

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

        // 最初のtickで開始位置を記録
        if (startPos == null) {
            startPos = this.position();
        }

        // 射程距離を超えたら削除
        if (this.position().distanceTo(startPos) > MAX_RANGE) {
            this.discard();
            return;
        }
}}