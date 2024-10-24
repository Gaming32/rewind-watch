package io.github.gaming32.rewindwatch.client;

import io.github.gaming32.rewindwatch.ResourceLocations;
import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.client.entity.FakePlayerRenderer;
import io.github.gaming32.rewindwatch.client.shaders.RewindWatchRenderState;
import io.github.gaming32.rewindwatch.client.shaders.RewindWatchRenderTypes;
import io.github.gaming32.rewindwatch.entity.RewindWatchEntityTypes;
import io.github.gaming32.rewindwatch.network.serverbound.ServerboundClientStatePayload;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.state.LivingAnimationState;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.jetbrains.annotations.Nullable;

@Mod(value = RewindWatch.MOD_ID, dist = Dist.CLIENT)
public class RewindWatchClient {
    private static final ResourceLocation POST_GRAYSCALE =
        ResourceLocations.rewindWatch("shaders/post/grayscale.json");
    private static final ResourceLocation POST_DISSOLVE_GRAYSCALE =
        ResourceLocations.rewindWatch("shaders/post/dissolve_grayscale.json");

    public RewindWatchClient(IEventBus bus, ModContainer container) {
        bus.addListener(this::registerEntityRenderers);
        bus.addListener(this::createEntityAttributes);
        bus.addListener(this::configReloaded);
        NeoForge.EVENT_BUS.addListener(this::renderLiving);
        NeoForge.EVENT_BUS.addListener(this::tickPlayer);
        // HIGH so we don't have mods triggering actual behaviors before we cancel
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, this::interactionTriggered);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, this::renderHighlight);

        container.registerConfig(ModConfig.Type.CLIENT, RewindWatchClientConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RewindWatchEntityTypes.FAKE_PLAYER.get(), FakePlayerRenderer::new);
    }

    private void createEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(RewindWatchEntityTypes.FAKE_PLAYER.get(), Player.createAttributes().build());
    }

    private void configReloaded(ModConfigEvent.Reloading event) {
        final var minecraft = Minecraft.getInstance();
        if (event.getConfig().getSpec() == RewindWatchClientConfig.SPEC) {
            minecraft.execute(() -> {
                if (!minecraft.gameRenderer.getMainCamera().isDetached()) {
                    minecraft.gameRenderer.checkEntityPostEffect(minecraft.cameraEntity);
                }
            });
        }
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
        player.connection.send(new ServerboundClientStatePayload(
            new LivingAnimationState(animation.position(), animation.speed()),
            player.getDeltaMovement()
        ));
    }

    private void interactionTriggered(InputEvent.InteractionKeyMappingTriggered event) {
        final var player = Minecraft.getInstance().player;
        if (player != null && player.hasData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE)) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }

    private void renderHighlight(RenderHighlightEvent.Block event) {
        final var player = Minecraft.getInstance().player;
        if (player != null && player.hasData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE)) {
            event.setCanceled(true);
        }
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
        final var mode = RewindWatchClientConfig.CONFIG.postEffects.get();
        if (mode == PostEffectMode.OFF) {
            return null;
        }
        return switch (effect) {
            case EntityEffect.Simple.GRAYSCALE -> POST_GRAYSCALE;
            case EntityEffect.Dissolve dissolve -> switch (dissolve.type()) {
                case GRAYSCALE -> mode != PostEffectMode.SIMPLE ? POST_DISSOLVE_GRAYSCALE : POST_GRAYSCALE;
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
