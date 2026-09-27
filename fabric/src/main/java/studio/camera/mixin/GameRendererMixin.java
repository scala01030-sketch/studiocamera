package studio.camera.mixin;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import studio.camera.client.CameraSession;
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method="render",at=@At("HEAD"))
    private void camera$frame(CallbackInfo ci) {CameraSession.INSTANCE.frame();}
    @Inject(method={"bobView","tiltViewWhenHurt","renderHand"},at=@At("HEAD"),cancellable=true)
    private void camera$steady(CallbackInfo ci) {if(CameraSession.INSTANCE.active)ci.cancel();}
    @Inject(method="getFov",at=@At("HEAD"),cancellable=true)
    private void camera$fov(Camera view,float delta,boolean configured,CallbackInfoReturnable<Float> ci) {
        if(CameraSession.INSTANCE.active&&configured)ci.setReturnValue((float)CameraSession.INSTANCE.camera.fov);
    }
}
