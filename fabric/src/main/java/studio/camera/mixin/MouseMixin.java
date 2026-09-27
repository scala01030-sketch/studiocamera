package studio.camera.mixin;
import net.minecraft.client.Mouse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.input.MouseInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import studio.camera.client.*;
import studio.camera.ui.CameraScreen;
@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Inject(method="onMouseButton",at=@At("HEAD"),cancellable=true)
    private void camera$buttons(long window,MouseInput button,int action,CallbackInfo ci) {
        var mc=MinecraftClient.getInstance();
        if(window==mc.getWindow().getHandle()&&action==GLFW.GLFW_PRESS&&(mc.currentScreen==null||mc.currentScreen instanceof CameraScreen)&&CameraKeys.mouse(button))ci.cancel();
    }
    @ModifyArgs(method="updateMouse",at=@At(value="INVOKE",target="Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void camera$look(Args arguments) {
        if(CameraSession.INSTANCE.active) {
            CameraSession.INSTANCE.look(arguments.get(0),arguments.get(1));
            arguments.set(0,0.0);arguments.set(1,0.0);
        }
    }
    @Inject(method="onMouseScroll",at=@At("HEAD"),cancellable=true)
    private void camera$zoom(long window,double horizontal,double vertical,CallbackInfo ci) {
        var mc=MinecraftClient.getInstance();
        if(window==mc.getWindow().getHandle()&&mc.currentScreen==null&&CameraSession.INSTANCE.active){CameraSession.INSTANCE.scroll(vertical);ci.cancel();}
    }
}
