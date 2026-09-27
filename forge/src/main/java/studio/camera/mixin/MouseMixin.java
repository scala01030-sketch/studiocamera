package studio.camera.mixin;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.camera.client.*;
import studio.camera.ui.CameraScreen;
@Mixin(MouseHandler.class)
public abstract class MouseMixin {
    @Inject(method="onPress",at=@At("HEAD"),cancellable=true)
    private void camera$buttons(long window,int button,int action,int modifiers,CallbackInfo ci) {
        var mc=Minecraft.getInstance();
        if(window==mc.getWindow().getWindow()&&action==GLFW.GLFW_PRESS&&(mc.screen==null||mc.screen instanceof CameraScreen)&&CameraKeys.mouse(button))ci.cancel();
    }
    @Redirect(method="turnPlayer",at=@At(value="INVOKE",target="Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void camera$look(LocalPlayer player,double x,double y) {
        if(CameraSession.INSTANCE.active)CameraSession.INSTANCE.look(x,y);else player.turn(x,y);
    }
    @Inject(method="onScroll",at=@At("HEAD"),cancellable=true)
    private void camera$zoom(long window,double horizontal,double vertical,CallbackInfo ci) {
        var mc=Minecraft.getInstance();
        if(window==mc.getWindow().getWindow()&&mc.screen==null&&CameraSession.INSTANCE.active){CameraSession.INSTANCE.scroll(vertical);ci.cancel();}
    }
}
