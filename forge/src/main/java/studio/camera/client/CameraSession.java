package studio.camera.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.multiplayer.ClientLevel;
import org.lwjgl.glfw.GLFW;
import studio.camera.core.CameraRig;
import studio.camera.ui.CameraScreen;

public final class CameraSession {
    public static final CameraSession INSTANCE=new CameraSession();
    public final CameraRig camera=new CameraRig();
    public boolean active, capture;
    private boolean previousHud;
    private CameraType previousPerspective;
    private ClientLevel world;
    private long previousFrame;
    private double focusX, focusY, focusZ, focusYaw, focusHeight, focusWidth;
    private boolean moving, looking;
    public void toggle() { if(active) exit(); else enter(); }
    public boolean enter() {
        var mc=Minecraft.getInstance();
        if(active || mc.level==null || mc.player==null || !mc.player.isAlive()) return false;
        var view=mc.gameRenderer.getMainCamera(); var pos=view.getPosition();
        camera.begin(pos.x,pos.y,pos.z,view.getYRot(),view.getXRot(),mc.options.fov().get());
        previousHud=mc.options.hideGui; previousPerspective=mc.options.getCameraType(); world=mc.level;
        active=true; capture=false; mc.options.hideGui=true; mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        focusPlayer(); previousFrame=System.nanoTime(); panel(); return true;
    }
    public void exit() {
        if(!active) return;
        endGesture(); active=false; capture=false; world=null; previousFrame=0;
        var mc=Minecraft.getInstance(); mc.options.hideGui=previousHud; mc.options.setCameraType(previousPerspective);
        if(mc.screen instanceof CameraScreen) mc.setScreen(null);
    }
    public void tick() {
        if(!active) return;
        var mc=Minecraft.getInstance();
        if(mc.level!=world || mc.player==null || !mc.player.isAlive()) exit();
        else mc.options.hideGui=true;
    }
    public void panel() { if(active) {endGesture();capture=false;Minecraft.getInstance().setScreen(new CameraScreen());} }
    public void fly() { if(active) {endGesture();capture=false;Minecraft.getInstance().setScreen(null);} }
    public void togglePanel() { if(!active) return; if(Minecraft.getInstance().screen instanceof CameraScreen) fly(); else panel(); }
    public void toggleCapture() { if(active) {endGesture();capture=!capture;Minecraft.getInstance().setScreen(null);} }
    public void focusPlayer() {
        var p=Minecraft.getInstance().player; if(p==null) return;
        var center=p.getBoundingBox().getCenter(); focusX=center.x;focusY=center.y;focusZ=center.z;
        focusYaw=p.getYRot(); focusHeight=p.getBbHeight();focusWidth=p.getBbWidth();
    }
    public void preset(int side,double viewportRatio) {
        camera.preset(focusX,focusY,focusZ,focusYaw,side,focusHeight,focusWidth,viewportRatio);
    }
    public void frame() {
        long now=System.nanoTime(); double dt=previousFrame==0?0:(now-previousFrame)*1e-9;previousFrame=now;
        var mc=Minecraft.getInstance();
        if(!active || mc.screen!=null || !mc.isWindowActive()) {endGesture();return;}
        long w=mc.getWindow().getWindow();
        double forward=down(w,GLFW.GLFW_KEY_W)-down(w,GLFW.GLFW_KEY_S),side=down(w,GLFW.GLFW_KEY_D)-down(w,GLFW.GLFW_KEY_A);
        double up=down(w,GLFW.GLFW_KEY_SPACE)-down(w,GLFW.GLFW_KEY_LEFT_SHIFT);
        double roll=down(w,GLFW.GLFW_KEY_E)-down(w,GLFW.GLFW_KEY_Q),zoom=down(w,GLFW.GLFW_KEY_RIGHT_BRACKET)-down(w,GLFW.GLFW_KEY_LEFT_BRACKET);
        boolean changed=forward!=0||side!=0||up!=0||roll!=0||zoom!=0;
        if(changed && !moving) camera.beginGesture(); moving=changed;
        camera.move(forward,side,up,roll,zoom,down(w,GLFW.GLFW_KEY_LEFT_CONTROL)>0,dt);
        // A mouse turn shares the current movement transaction until all input is idle.
        if(!moving && !looking) camera.endGesture(); looking=false;
    }
    private static int down(long w,int k) { return GLFW.glfwGetKey(w,k)==GLFW.GLFW_PRESS?1:0; }
    public void look(double dx,double dy) {camera.beginGesture();looking=true;camera.look(dx,dy);}
    public void scroll(double steps) {endGesture();camera.beginGesture();camera.zoom(steps);camera.endGesture();}
    public void endGesture() {camera.endGesture();moving=looking=false;}
    public void undo() {endGesture();camera.undo();if(Minecraft.getInstance().screen instanceof CameraScreen screen) screen.refresh();}
}
