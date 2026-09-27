package studio.camera.mixin;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.camera.client.*;
import studio.camera.ui.CameraScreen;
@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Inject(method="onKey",at=@At("HEAD"),cancellable=true)
    private void camera$keys(long window,int action,KeyInput key,CallbackInfo ci) {
        var mc=MinecraftClient.getInstance();if(window!=mc.getWindow().getHandle()||(mc.currentScreen!=null&&!(mc.currentScreen instanceof CameraScreen)))return;
        if(action==GLFW.GLFW_REPEAT&&(CameraKeys.mapped(key)||(key.key()==GLFW.GLFW_KEY_ESCAPE&&CameraSession.INSTANCE.active))){ci.cancel();return;}
        if(action==GLFW.GLFW_PRESS&&CameraKeys.press(key))ci.cancel();
    }
}
