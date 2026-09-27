package studio.camera.mixin;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.camera.client.CameraSession;
@Mixin(InGameHud.class)
public abstract class HudMixin {
    @Inject(method="render",at=@At("HEAD"),cancellable=true)
    private void camera$hud(DrawContext context,RenderTickCounter counter,CallbackInfo ci) {
        if(CameraSession.INSTANCE.active){studio.camera.ui.CameraHud.draw(context);ci.cancel();}
    }
}
