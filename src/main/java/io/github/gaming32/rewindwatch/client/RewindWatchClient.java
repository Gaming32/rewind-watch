package io.github.gaming32.rewindwatch.client;

import io.github.gaming32.rewindwatch.EntityEffect;
import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.client.render.RewindWatchRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = RewindWatch.MOD_ID, dist = Dist.CLIENT)
public class RewindWatchClient {
    public RewindWatchClient() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void renderLiving(RenderLivingEvent.Pre<?, ?> event) {
        if (
            event.getEntity().getData(RewindWatchAttachmentTypes.ENTITY_EFFECT) instanceof
                EntityEffect.Dissolve(var startTick, var endTick, var type, var in)
        ) {
            final var currentTick = event.getEntity().level().getGameTime();
            var progress = Math.clamp(
                (currentTick - startTick + event.getPartialTick()) / (endTick - startTick), 0f, 1f
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
