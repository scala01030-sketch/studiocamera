package studio.camera.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import studio.camera.client.CameraKeys;
import studio.camera.client.CameraSession;


public final class CameraScreen extends Screen {
    private final CameraSession s=CameraSession.INSTANCE;
    private final List<EditBox> values=new ArrayList<>();
    private final String[] labels={"x","y","z","yaw","pitch","roll","fov","speed"};
    private int panel, drag=-1;
    private boolean compact;
    private boolean dragging;
    private Component message=Component.empty();
    public CameraScreen() {super(Component.translatable("studiocamera.title"));}
    @Override public boolean isPauseScreen() {return false;}
    @Override protected void init() {refresh();}
    private Button button(String key,int x,int y,int width,Runnable action) {
        return addRenderableWidget(Button.builder(Component.translatable("studiocamera."+key),b->action.run()).bounds(x,y,width,20).build());
    }
    public void refresh() {
        clearWidgets();values.clear();panel=Math.min(192,Math.max(160,width/3));
        compact=height<340;
        int actionWidth=Math.min(62,Math.max(42,(width-112)/4));
        button("fly",104,4,actionWidth,s::fly);button("capture",106+actionWidth,4,actionWidth,s::toggleCapture);
        button("undo",108+actionWidth*2,4,actionWidth,()->{s.undo();message=Component.translatable("studiocamera.undo_count",s.camera.undoCount());});
        button("exit",110+actionWidth*3,4,actionWidth,s::exit);
        int viewWidth=(width-panel-12)/4;
        String[] views={"front","back","left","right"};
        for(int i=0;i<4;i++){final int side=i;button("view."+views[i],6+i*viewWidth,32,viewWidth-2,()->{s.preset(side,(double)(width-panel)/width);refresh();});}
        var c=s.camera;double[] v={c.x,c.y,c.z,c.yaw,c.pitch,c.roll,c.fov,c.speed};
        for(int i=0;i<labels.length;i++) {
            var box=new EditBox(font,width-panel+75,(compact?44:49)+i*(compact?16:21),panel-83,compact?14:18,Component.translatable("studiocamera.property."+labels[i]));
            box.setMaxLength(24);box.setValue(String.format(Locale.ROOT,"%.3f",v[i]));values.add(addRenderableWidget(box));
        }
        button("apply",width-panel+6,compact?176:220,panel-12,this::apply);
        int focusWidth=compact?(width-panel-18)/2:panel-12;
        button("focus",compact?6:width-panel+6,compact?height-58:244,focusWidth,()->{s.focusPlayer();message=Component.translatable("studiocamera.focused");});
        button("reset_roll",compact?12+focusWidth:width-panel+6,compact?height-58:268,focusWidth,()->{c.beginGesture();c.roll=0;c.endGesture();refresh();});
    }
    public void apply() {
        try {
            double[] v=new double[8];for(int i=0;i<v.length;i++)v[i]=Double.parseDouble(values.get(i).getValue());
            if(!s.camera.apply(v))throw new IllegalArgumentException();
            message=Component.translatable("studiocamera.applied");refresh();
        }catch(IllegalArgumentException e){message=Component.translatable("studiocamera.invalid");}
    }
    @Override public void render(GuiGraphics g,int mx,int my,float partial) {
        g.fill(0,0,width,28,0xe51a202b);g.fill(width-panel,28,width,height-34,0xdb1a202b);g.fill(0,height-34,width,height,0xe51a202b);
        g.drawString(font,title,6,10,0xffd5e6ff,false);
        g.drawString(font,Component.translatable("studiocamera.properties"),width-panel+6,34,0xffbdd5ef,false);
        for(int i=0;i<labels.length;i++)g.drawString(font,Component.translatable("studiocamera.property."+labels[i]),width-panel+6,(compact?48:54)+i*(compact?16:21),0xffeeeeee,false);
        if(height>334)g.drawString(font,message,width-panel+6,296,0xffffd878,false);
        g.drawString(font,CameraKeys.hint(),6,height-29,0xffe0eaff,false);
        g.drawString(font,Component.translatable("studiocamera.hint.drag"),6,height-15,0xffc1cbd6,false);
        super.render(g,mx,my,partial);
    }
    @Override public boolean keyPressed(int key,int scan,int mods) {
        if(key==GLFW.GLFW_KEY_Z&&(mods&GLFW.GLFW_MOD_CONTROL)!=0) {

            if(getFocused() instanceof EditBox)return super.keyPressed(key,scan,mods);
            s.undo();return true;
        }
        if(key==GLFW.GLFW_KEY_ENTER||key==GLFW.GLFW_KEY_KP_ENTER){apply();return true;}
        return super.keyPressed(key,scan,mods);
    }
    private boolean viewport(double x,double y){return x>=0&&x<width-panel&&y>=56&&y<height-(compact?62:34);}
    @Override public boolean mouseClicked(double x,double y,int button) {
        if(viewport(x,y)&&button>=0&&button<=2){drag=button;dragging=false;setFocused(null);return true;}
        return super.mouseClicked(x,y,button);
    }
    @Override public boolean mouseDragged(double x,double y,int button,double dx,double dy) {
        if(drag==button){
            if(!dragging){s.camera.beginGesture();dragging=true;}
            if(button==0)s.camera.look(dx,dy);else if(button==1)s.camera.pan(dx,dy);else s.camera.zoom(-dy*.25);
            return true;
        }
        return super.mouseDragged(x,y,button,dx,dy);
    }
    @Override public boolean mouseReleased(double x,double y,int button) {
        if(drag==button){s.camera.endGesture();drag=-1;dragging=false;refresh();return true;}
        return super.mouseReleased(x,y,button);
    }
    @Override public boolean mouseScrolled(double x,double y,double steps) {
        if(viewport(x,y)){s.scroll(steps);refresh();return true;}
        return super.mouseScrolled(x,y,steps);
    }
    @Override public void removed(){s.endGesture();drag=-1;}
    @Override public void onClose(){s.fly();}
}
