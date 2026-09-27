package studio.camera.mixin;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.camera.client.CameraSession;
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method={"bobView","bobHurt"},at=@At("HEAD"),cancellable=true)
    private void camera$steady(CallbackInfo ci) {if(CameraSession.INSTANCE.active)ci.cancel();}
    @Inject(method="render",at=@At(value="INVOKE",target="Lnet/minecraft/client/gui/GuiGraphics;flush()V"))
    private void camera$guide(float partial,long finish,boolean world,CallbackInfo ci) {if(world)studio.camera.ui.CameraHud.draw();}
}
