package studio.camera.ui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import studio.camera.client.*;
public final class CameraHud {
    public static void draw(DrawContext g) {
        var mc=MinecraftClient.getInstance();var s=CameraSession.INSTANCE;
        if(!s.active||s.capture||mc.currentScreen!=null)return;
        g.fill(4,4,Math.min(mc.getWindow().getScaledWidth()-4,610),34,0xa51a202b);
        g.drawText(mc.textRenderer,CameraKeys.hint(),8,8,0xffe0eaff,false);
        g.drawText(mc.textRenderer,Text.translatable("studiocamera.hint.fly"),8,22,0xffc1cbd6,false);
    }
}
