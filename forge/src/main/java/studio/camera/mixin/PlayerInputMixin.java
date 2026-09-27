package studio.camera.mixin;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.camera.client.CameraSession;
@Mixin(LocalPlayer.class)
public abstract class PlayerInputMixin {
    @Inject(method="aiStep",at=@At(value="INVOKE",target="Lnet/minecraft/client/player/Input;tick(ZF)V",shift=At.Shift.AFTER))
    private void camera$input(CallbackInfo ci) {
        if(!CameraSession.INSTANCE.active)return;var p=(LocalPlayer)(Object)this;
        p.input.forwardImpulse=p.input.leftImpulse=0;p.input.jumping=p.input.shiftKeyDown=false;
        p.input.up=p.input.down=p.input.left=p.input.right=false;
    }
}
