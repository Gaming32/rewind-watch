package io.github.gaming32.rewindwatch.client;

import io.github.gaming32.rewindwatch.ResourceLocations;
import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.client.entity.FakePlayerRenderer;
import io.github.gaming32.rewindwatch.client.shaders.RewindWatchRenderState;
import io.github.gaming32.rewindwatch.client.shaders.RewindWatchRenderTypes;
import io.github.gaming32.rewindwatch.entity.RewindWatchEntityTypes;
import io.github.gaming32.rewindwatch.network.serverbound.ServerboundAnimationStatePayload;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.state.PlayerAnimationState;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.jetbrains.annotations.Nullable;

@Mod(value = RewindWatch.MOD_ID, dist = Dist.CLIENT)
public class RewindWatchClient {
    private static final ResourceLocation POST_GRAYSCALE =
        ResourceLocations.rewindWatch("shaders/post/grayscale.json");
    private static final ResourceLocation POST_DISSOLVE_GRAYSCALE =
        ResourceLocations.rewindWatch("shaders/post/dissolve_grayscale.json");

    public RewindWatchClient(IEventBus bus) {
        bus.addListener(this::registerEntityRenderers);
        bus.addListener(this::createEntityAttributes);
        NeoForge.EVENT_BUS.addListener(this::renderLiving);
        NeoForge.EVENT_BUS.addListener(this::tickPlayer);
    }

    private void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RewindWatchEntityTypes.FAKE_PLAYER.get(), FakePlayerRenderer::new);
    }

    private void createEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(RewindWatchEntityTypes.FAKE_PLAYER.get(), Player.createAttributes().build());
    }

    private void renderLiving(RenderLivingEvent.Pre<?, ?> event) {
        updateDissolveOpacity(
            event.getEntity(),
            event.getPartialTick()
        );
    }

    private void tickPlayer(ClientTickEvent.Post event) {
        final var player = Minecraft.getInstance().player;
        if (player == null) return;
        final var animation = player.walkAnimation;
        player.connection.send(new ServerboundAnimationStatePayload(new PlayerAnimationState(
            animation.position(), animation.speed()
        )));
    }

    public static RenderType getEffectRenderType(
        EntityEffect effect, ResourceLocation texture, RenderType defaultRenderType
    ) {
        return switch (effect) {
            case EntityEffect.Simple.NONE -> defaultRenderType;
            case EntityEffect.Simple.GRAYSCALE -> RewindWatchRenderTypes.entityTranslucentGrayscale(texture);
            case EntityEffect.Dissolve dissolve -> switch (dissolve.type()) {
                case TRANSPARENT -> RewindWatchRenderTypes.entityTranslucentDissolve(texture);
                case GRAYSCALE, TRANSPARENT_GRAYSCALE -> RewindWatchRenderTypes.entityTranslucentDissolveGrayscale(texture);
            };
        };
    }

    @Nullable
    public static ResourceLocation getEffectPostShader(EntityEffect effect) {
        return switch (effect) {
            case EntityEffect.Simple.GRAYSCALE -> POST_GRAYSCALE;
            case EntityEffect.Dissolve dissolve -> switch (dissolve.type()) {
                case GRAYSCALE -> POST_DISSOLVE_GRAYSCALE;
                case TRANSPARENT_GRAYSCALE -> POST_GRAYSCALE;
                default -> null;
            };
            default -> null;
        };
    }

    public static void updateDissolveOpacity(Entity entity, float partialTick) {
        if (
            RWAttachments.getEntityEffect(entity) instanceof
                EntityEffect.Dissolve(var startTick, var endTick, var type, var in)
        ) {
            final var currentTick = entity.level().getGameTime();
            var progress = Math.clamp(
                (currentTick - startTick + partialTick) / (endTick - startTick), 0f, 1f
            );
            if (!in) {
                progress = 1f - progress;
            }
            if (type == EntityEffect.Dissolve.Type.TRANSPARENT_GRAYSCALE) {
                progress -= 1f;
            }
            RewindWatchRenderState.setDissolveOpacity(progress);
        }
    }
}
