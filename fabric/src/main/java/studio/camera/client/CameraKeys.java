package studio.camera.client;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import studio.camera.ui.CameraScreen;
public final class CameraKeys {
    public static boolean mapped(KeyInput key) {return CameraClient.TOGGLE.matchesKey(key)||CameraClient.PANEL.matchesKey(key)||CameraClient.CAPTURE.matchesKey(key);}
    public static boolean press(KeyInput key) {
        var s=CameraSession.INSTANCE;var mc=MinecraftClient.getInstance();
        if(CameraClient.TOGGLE.matchesKey(key)){s.toggle();return true;}
        if(!s.active)return false;
        if(CameraClient.PANEL.matchesKey(key)){s.togglePanel();return true;}
        if(CameraClient.CAPTURE.matchesKey(key)){s.toggleCapture();return true;}
        if(key.key()==GLFW.GLFW_KEY_ESCAPE&&mc.currentScreen==null){s.panel();return true;}
        if(key.key()==GLFW.GLFW_KEY_Z&&(key.modifiers()&GLFW.GLFW_MOD_CONTROL)!=0&&!(mc.currentScreen instanceof CameraScreen)){s.undo();return true;}
        return false;
    }
    public static boolean mouse(MouseInput button) {
        var click=new Click(0,0,button);var s=CameraSession.INSTANCE;
        if(CameraClient.TOGGLE.matchesMouse(click)){s.toggle();return true;}
        if(!s.active)return false;
        if(CameraClient.PANEL.matchesMouse(click)){s.togglePanel();return true;}
        if(CameraClient.CAPTURE.matchesMouse(click)){s.toggleCapture();return true;}
        return false;
    }
    public static Text hint() {return Text.translatable("studiocamera.hint.keys",CameraClient.TOGGLE.getBoundKeyLocalizedText(),CameraClient.PANEL.getBoundKeyLocalizedText(),CameraClient.CAPTURE.getBoundKeyLocalizedText());}
}
