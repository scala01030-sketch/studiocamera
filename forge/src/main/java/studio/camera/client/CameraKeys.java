package studio.camera.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import studio.camera.ui.CameraScreen;

public final class CameraKeys {
    public static boolean mapped(int key,int scan) {return CameraClient.TOGGLE.matches(key,scan)||CameraClient.PANEL.matches(key,scan)||CameraClient.CAPTURE.matches(key,scan);}
    public static boolean press(int key,int scan,int modifiers) {
        var s=CameraSession.INSTANCE;var mc=Minecraft.getInstance();
        if(CameraClient.TOGGLE.matches(key,scan)) {s.toggle();return true;}
        if(!s.active) return false;
        if(CameraClient.PANEL.matches(key,scan)) {s.togglePanel();return true;}
        if(CameraClient.CAPTURE.matches(key,scan)) {s.toggleCapture();return true;}
        if(key==GLFW.GLFW_KEY_ESCAPE && mc.screen==null){s.panel();return true;}
        if(key==GLFW.GLFW_KEY_Z && (modifiers&GLFW.GLFW_MOD_CONTROL)!=0 && !(mc.screen instanceof CameraScreen)) {s.undo();return true;}
        return false;
    }
    public static boolean mouse(int button) {
        var s=CameraSession.INSTANCE;
        if(CameraClient.TOGGLE.matchesMouse(button)){s.toggle();return true;}
        if(!s.active)return false;
        if(CameraClient.PANEL.matchesMouse(button)){s.togglePanel();return true;}
        if(CameraClient.CAPTURE.matchesMouse(button)){s.toggleCapture();return true;}
        return false;
    }
    public static Component hint() {return Component.translatable("studiocamera.hint.keys",CameraClient.TOGGLE.getTranslatedKeyMessage(),CameraClient.PANEL.getTranslatedKeyMessage(),CameraClient.CAPTURE.getTranslatedKeyMessage());}
}
