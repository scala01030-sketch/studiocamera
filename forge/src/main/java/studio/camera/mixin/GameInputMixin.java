package studio.camera.mixin;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.camera.client.CameraSession;
@Mixin(Minecraft.class)
public abstract class GameInputMixin {
    @Inject(method="handleKeybinds",at=@At("HEAD"),cancellable=true)
    private void camera$controls(CallbackInfo ci) {
        if(!CameraSession.INSTANCE.active)return;
        for(var key:((Minecraft)(Object)this).options.keyMappings)while(key.consumeClick()){}
        ci.cancel();
    }
}
