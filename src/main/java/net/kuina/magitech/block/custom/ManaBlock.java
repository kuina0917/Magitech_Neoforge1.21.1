package net.kuina.magitech.block.custom;

import net.kuina.magitech.fluid.magitechfluids;
import net.kuina.magitech.item.magitechitems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ManaBlock extends LiquidBlock {
    private static final int CONVERSION_TICKS = 40;
    private final Map<UUID, Integer> pendingConversion = new HashMap<>();

    public ManaBlock(BlockBehaviour.Properties properties) {
        super(magitechfluids.MANA.get(),
                properties.mapColor(MapColor.COLOR_CYAN)
                        .strength(100f).lightLevel(s -> 10)
                        .noCollission().noLootTable().liquid()
                        .pushReaction(PushReaction.DESTROY)
                        .sound(SoundType.EMPTY).replaceable());
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!(entity instanceof ItemEntity itemEntity) || level.isClientSide) return;

        ItemStack stack = itemEntity.getItem();
        UUID id = itemEntity.getUUID();

        // --- TESTBLOCK → DIRT（液体残る） ---
        if (stack.getItem() == magitechitems.TESTBLOCK.get()) {
            int time = pendingConversion.getOrDefault(id, 0) + 1;
            pendingConversion.put(id, time);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                        itemEntity.getX(), itemEntity.getY() + 0.25, itemEntity.getZ(),
                        15, 0.25, 0.25, 0.25, 0.01);
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        itemEntity.getX(), itemEntity.getY() + 0.1, itemEntity.getZ(),
                        10, 0.2, 0.2, 0.2, 0.005);
            }

            if (time >= CONVERSION_TICKS) {
                pendingConversion.remove(id);
                itemEntity.discard();

                ItemStack result = new ItemStack(Items.DIRT, stack.getCount());
                ItemEntity newItem = new ItemEntity(level,
                        pos.getX() + 0.5,
                        pos.getY() + 1.0,
                        pos.getZ() + 0.5,
                        result);
                level.addFreshEntity(newItem);

                level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 5.0F, 1.0F);

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ENCHANT,
                            pos.getX() + 0.5,
                            pos.getY() + 1.0,
                            pos.getZ() + 0.5,
                            30, 0.3, 0.3, 0.3, 0.03);
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                            pos.getX() + 0.5,
                            pos.getY() + 1.0,
                            pos.getZ() + 0.5,
                            1, 0, 0, 0, 0);
                }
            }
        }

        // --- IRON_INGOT → LOW_MANA_INGOT（液体消える） ---
        if (stack.getItem() == Items.IRON_INGOT) {
            int time = pendingConversion.getOrDefault(id, 0) + 1;
            pendingConversion.put(id, time);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                        itemEntity.getX(), itemEntity.getY() + 0.25, itemEntity.getZ(),
                        20, 0.25, 0.25, 0.25, 0.01);
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        itemEntity.getX(), itemEntity.getY() + 0.1, itemEntity.getZ(),
                        10, 0.2, 0.2, 0.2, 0.005);
            }

            if (time >= CONVERSION_TICKS) {
                pendingConversion.remove(id);
                itemEntity.discard();

                // 液体を空気に置き換える（消える）
                level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);

                ItemStack result = new ItemStack(magitechitems.LOW_MANA_INGOT.get(), stack.getCount());
                ItemEntity newItem = new ItemEntity(level,
                        pos.getX() + 0.5,
                        pos.getY() + 1.0,
                        pos.getZ() + 0.5,
                        result);
                level.addFreshEntity(newItem);

                level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 5.0F, 0.95F);

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ENCHANT,
                            pos.getX() + 0.5,
                            pos.getY() + 1.0,
                            pos.getZ() + 0.5,
                            40, 0.4, 0.4, 0.4, 0.03);
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                            pos.getX() + 0.5,
                            pos.getY() + 1.0,
                            pos.getZ() + 0.5,
                            2, 0, 0, 0, 0);
                }
            }
        }
    }
}