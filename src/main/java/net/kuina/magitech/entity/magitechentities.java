package net.kuina.magitech.entity;

import net.kuina.magitech.entity.custom.MagicCircle;
import net.kuina.magitech.entity.custom.MagicCircleRapidFire;
import net.kuina.magitech.entity.custom.ZoltrakProjectile;
import net.kuina.magitech.magitech;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class magitechentities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, magitech.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ZoltrakProjectile>> ZOLTRAK_PROJECTILE =
            ENTITIES.register("zoltrak_projectile", () ->
                    EntityType.Builder.<ZoltrakProjectile>of(ZoltrakProjectile::new, MobCategory.MISC)
                            .sized(0.3f, 0.3f)
                            .build("zoltrak_projectile")
            );
    public static final DeferredHolder<EntityType<?>, EntityType<MagicCircle>> MAGIC_CIRCLE =
            ENTITIES.register("magic_circle", () ->
                    EntityType.Builder.<MagicCircle>of(MagicCircle::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(32)
                            .build("magic_circle")

            );
    public static final DeferredHolder<EntityType<?>, EntityType<MagicCircleRapidFire>> MAGIC_CIRCLE_RAPIDFIRE =
            ENTITIES.register("magic_circle_rapidfire", () ->
                    EntityType.Builder.<MagicCircleRapidFire>of(MagicCircleRapidFire::new, MobCategory.MISC)
                            .sized(1.2f, 1.2f) // 少し大きめ
                            .clientTrackingRange(32)
                            .build("magic_circle_rapidfire")
            );

}