package studio.camera.mixin;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.camera.client.CameraSession;
@Mixin(KeyboardInput.class)
public abstract class PlayerInputMixin extends Input {
    @Inject(method="tick",at=@At("RETURN"))
    private void camera$input(CallbackInfo ci) {if(CameraSession.INSTANCE.active){playerInput=PlayerInput.DEFAULT;movementVector=Vec2f.ZERO;}}
}
