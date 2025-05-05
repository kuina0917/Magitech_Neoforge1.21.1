package net.kuina.magitech.client.renderer;

import net.kuina.magitech.entity.custom.ZoltrakProjectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class ZoltrakProjectileRenderer extends ThrownItemRenderer<ZoltrakProjectile> {
    public ZoltrakProjectileRenderer(EntityRendererProvider.Context context) {
        super(context, 1.0F, true);
    }
}
