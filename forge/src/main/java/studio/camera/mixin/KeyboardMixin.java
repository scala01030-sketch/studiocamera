package studio.camera.mixin;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.camera.client.*;
import studio.camera.ui.CameraScreen;
@Mixin(KeyboardHandler.class)
public abstract class KeyboardMixin {
    @Inject(method="keyPress",at=@At("HEAD"),cancellable=true)
    private void camera$keys(long window,int key,int scan,int action,int modifiers,CallbackInfo ci) {
        var mc=Minecraft.getInstance();if(window!=mc.getWindow().getWindow()||(mc.screen!=null&&!(mc.screen instanceof CameraScreen)))return;
        if(action==GLFW.GLFW_REPEAT&&(CameraKeys.mapped(key,scan)||(key==GLFW.GLFW_KEY_ESCAPE&&CameraSession.INSTANCE.active))){ci.cancel();return;}
        if(action==GLFW.GLFW_PRESS&&CameraKeys.press(key,scan,modifiers))ci.cancel();
    }
}
