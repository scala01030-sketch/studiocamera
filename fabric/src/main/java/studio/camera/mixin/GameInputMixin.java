package studio.camera.mixin;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.camera.client.CameraSession;
@Mixin(MinecraftClient.class)
public abstract class GameInputMixin {
    @Inject(method="handleInputEvents",at=@At("HEAD"),cancellable=true)
    private void camera$controls(CallbackInfo ci) {
        if(!CameraSession.INSTANCE.active)return;
        for(var key:((MinecraftClient)(Object)this).options.allKeys)while(key.wasPressed()){}
        ci.cancel();
    }
}
