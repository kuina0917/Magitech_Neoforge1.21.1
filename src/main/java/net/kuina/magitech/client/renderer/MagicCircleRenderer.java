package net.kuina.magitech.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.kuina.magitech.entity.custom.MagicCircle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;


public class MagicCircleRenderer extends EntityRenderer<MagicCircle> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("magitech:textures/entity/magic_circle.png");
    public MagicCircleRenderer(EntityRendererProvider.Context context) {
        super(context);

    }

    @Override
    public void render(MagicCircle entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        System.out.println("MagicCircleRenderer is rendering!");
        poseStack.pushPose();

        // 若干浮かせてZファイト回避
        poseStack.translate(0.0f, 0.01f, 0.0f);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.scale(1.0f, 1.0f, 1.0f); // 描画サイズ調整（必要に応じて変更）

        VertexConsumer builder = bufferSource.getBuffer( RenderType.entityTranslucent(ResourceLocation.parse("magitech:textures/entity/magic_circle.png")));
        float size = 1.0f;
        Matrix4f matrix = poseStack.last().pose();

        builder.addVertex(matrix, -size, 0, -size).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0.0f, 1.0f, 0.0f);
        builder.addVertex(matrix, -size, 0,  size).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0.0f, 1.0f, 0.0f);
        builder.addVertex(matrix,  size, 0,  size).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0.0f, 1.0f, 0.0f);
        builder.addVertex(matrix,  size, 0, -size).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0.0f, 1.0f, 0.0f);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MagicCircle entity) {
        return ResourceLocation.parse("magitech:textures/entity/magic_circle.png");


    }
}