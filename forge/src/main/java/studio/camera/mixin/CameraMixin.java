package studio.camera.mixin;
import net.minecraft.client.Camera;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.camera.client.CameraSession;
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setPosition(double x,double y,double z);
    @Shadow protected abstract void setRotation(float yaw,float pitch);
    @Shadow private boolean detached;
    @Inject(method="setup",at=@At("RETURN"))
    private void camera$view(BlockGetter world,Entity entity,boolean third,boolean inverse,float delta,CallbackInfo ci) {
        if(!CameraSession.INSTANCE.active)return;
        var c=CameraSession.INSTANCE.camera;detached=true;setPosition(c.x,c.y,c.z);setRotation((float)c.yaw,(float)c.pitch);
    }
}
