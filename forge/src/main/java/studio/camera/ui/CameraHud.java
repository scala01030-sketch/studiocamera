package studio.camera.ui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import studio.camera.client.*;
public final class CameraHud {
    public static void draw() {
        var mc=Minecraft.getInstance();var s=CameraSession.INSTANCE;
        if(!s.active||s.capture||mc.screen!=null)return;
        var g=new GuiGraphics(mc,mc.renderBuffers().bufferSource());
        g.fill(4,4,Math.min(mc.getWindow().getGuiScaledWidth()-4,610),34,0xa51a202b);
        g.drawString(mc.font,CameraKeys.hint(),8,8,0xffe0eaff,false);
        g.drawString(mc.font,Component.translatable("studiocamera.hint.fly"),8,22,0xffc1cbd6,false);
        g.flush();
    }
}
