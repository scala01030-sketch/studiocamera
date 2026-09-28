package studio.camera.mixin;
import net.minecraft.client.render.Camera;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.camera.client.CameraSession;
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setPos(double x,double y,double z);
    @Shadow protected abstract void setRotation(float yaw,float pitch);
    @Shadow private boolean thirdPerson;
    @Shadow @Final private Quaternionf rotation;
    @Shadow @Final private Vector3f horizontalPlane;
    @Shadow @Final private Vector3f verticalPlane;
    @Shadow @Final private Vector3f diagonalPlane;
    @Inject(method="update",at=@At("RETURN"))
    private void camera$view(World world,Entity entity,boolean third,boolean inverse,float delta,CallbackInfo ci) {
        if(!CameraSession.INSTANCE.active)return;
        var c=CameraSession.INSTANCE.camera;thirdPerson=true;setPos(c.x,c.y,c.z);setRotation((float)c.yaw,(float)c.pitch);

        rotation.rotateZ((float)-Math.toRadians(c.roll));
        horizontalPlane.set(0,0,-1).rotate(rotation);
        verticalPlane.set(0,1,0).rotate(rotation);
        diagonalPlane.set(1,0,0).rotate(rotation);
    }
}
