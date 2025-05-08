package net.kuina.magitech.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.kuina.magitech.entity.custom.MagicCircleRapidFire;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class MagicCircleRapidFireRenderer extends EntityRenderer<MagicCircleRapidFire> {

    private static final ResourceLocation TEXTURE = ResourceLocation.parse("magitech:textures/entity/magic_circle.png");

    public MagicCircleRapidFireRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(MagicCircleRapidFire entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {

        poseStack.pushPose();

        // 魔法陣の微調整（少し上に浮かせる）
        poseStack.translate(0, 0.01, 0);

        // プレイヤーの向きに合わせて回転
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));

        // 地面に寝かせる
        poseStack.mulPose(Axis.XP.rotationDegrees(90));

        // スケーリング（直径2ブロック相当）
        float size = 1.0f;
        poseStack.scale(size, size, size);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE));
        Matrix4f matrix = poseStack.last().pose();

        // 表面（上向き）
        builder.addVertex(matrix, -1, 0, -1).setColor(255, 255, 255, 255).setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
        builder.addVertex(matrix, -1, 0, 1).setColor(255, 255, 255, 255).setUv(0, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
        builder.addVertex(matrix, 1, 0, 1).setColor(255, 255, 255, 255).setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);
        builder.addVertex(matrix, 1, 0, -1).setColor(255, 255, 255, 255).setUv(1, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, 1, 0);

        // 裏面（下向き、片面描画対策）
        builder.addVertex(matrix, -1, 0, -1).setColor(255, 255, 255, 255).setUv(1, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, -1, 0);
        builder.addVertex(matrix, 1, 0, -1).setColor(255, 255, 255, 255).setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, -1, 0);
        builder.addVertex(matrix, 1, 0, 1).setColor(255, 255, 255, 255).setUv(0, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, -1, 0);
        builder.addVertex(matrix, -1, 0, 1).setColor(255, 255, 255, 255).setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0, -1, 0);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MagicCircleRapidFire entity) {
        return TEXTURE;
    }
}
