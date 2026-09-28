package studio.camera.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import studio.camera.client.CameraKeys;
import studio.camera.client.CameraSession;


public final class CameraScreen extends Screen {
    private final CameraSession s=CameraSession.INSTANCE;
    private final List<TextFieldWidget> values=new ArrayList<>();
    private final String[] labels={"x","y","z","yaw","pitch","roll","fov","speed"};
    private int panel, drag=-1;
    private boolean compact;
    private boolean dragging;
    private Text message=Text.empty();
    public CameraScreen() {super(Text.translatable("studiocamera.title"));}
    @Override public boolean shouldPause() {return false;}
    @Override public void renderBackground(DrawContext context,int mouseX,int mouseY,float delta) {

    }
    @Override protected void init() {refresh();}
    private ButtonWidget button(String key,int x,int y,int width,Runnable action) {
        return addDrawableChild(ButtonWidget.builder(Text.translatable("studiocamera."+key),b->action.run()).dimensions(x,y,width,20).build());
    }
    public void refresh() {
        clearChildren();values.clear();panel=Math.min(192,Math.max(160,width/3));
        compact=height<340;
        int actionWidth=Math.min(62,Math.max(42,(width-112)/4));
        button("fly",104,4,actionWidth,s::fly);button("capture",106+actionWidth,4,actionWidth,s::toggleCapture);
        button("undo",108+actionWidth*2,4,actionWidth,()->{s.undo();message=Text.translatable("studiocamera.undo_count",s.camera.undoCount());});
        button("exit",110+actionWidth*3,4,actionWidth,s::exit);
        int viewWidth=(width-panel-12)/4;
        String[] views={"front","back","left","right"};
        for(int i=0;i<4;i++){final int side=i;button("view."+views[i],6+i*viewWidth,32,viewWidth-2,()->{s.preset(side,(double)(width-panel)/width);refresh();});}
        var c=s.camera;double[] v={c.x,c.y,c.z,c.yaw,c.pitch,c.roll,c.fov,c.speed};
        for(int i=0;i<labels.length;i++) {
            var box=new TextFieldWidget(textRenderer,width-panel+75,(compact?44:49)+i*(compact?16:21),panel-83,compact?14:18,Text.translatable("studiocamera.property."+labels[i]));
            box.setMaxLength(24);box.setText(String.format(Locale.ROOT,"%.3f",v[i]));values.add(addDrawableChild(box));
        }
        button("apply",width-panel+6,compact?176:220,panel-12,this::apply);
        int focusWidth=compact?(width-panel-18)/2:panel-12;
        button("focus",compact?6:width-panel+6,compact?height-58:244,focusWidth,()->{s.focusPlayer();message=Text.translatable("studiocamera.focused");});
        button("reset_roll",compact?12+focusWidth:width-panel+6,compact?height-58:268,focusWidth,()->{c.beginGesture();c.roll=0;c.endGesture();refresh();});
    }
    public void apply() {
        try {
            double[] v=new double[8];for(int i=0;i<v.length;i++)v[i]=Double.parseDouble(values.get(i).getText());
            if(!s.camera.apply(v))throw new IllegalArgumentException();
            message=Text.translatable("studiocamera.applied");refresh();
        }catch(IllegalArgumentException e){message=Text.translatable("studiocamera.invalid");}
    }
    @Override public void render(DrawContext g,int mx,int my,float partial) {
        g.fill(0,0,width,28,0xe51a202b);g.fill(width-panel,28,width,height-34,0xdb1a202b);g.fill(0,height-34,width,height,0xe51a202b);
        g.drawText(textRenderer,title,6,10,0xffd5e6ff,false);
        g.drawText(textRenderer,Text.translatable("studiocamera.properties"),width-panel+6,34,0xffbdd5ef,false);
        for(int i=0;i<labels.length;i++)g.drawText(textRenderer,Text.translatable("studiocamera.property."+labels[i]),width-panel+6,(compact?48:54)+i*(compact?16:21),0xffeeeeee,false);
        if(height>334)g.drawText(textRenderer,message,width-panel+6,296,0xffffd878,false);
        g.drawText(textRenderer,CameraKeys.hint(),6,height-29,0xffe0eaff,false);
        g.drawText(textRenderer,Text.translatable("studiocamera.hint.drag"),6,height-15,0xffc1cbd6,false);
        super.render(g,mx,my,partial);
    }
    @Override public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        int key=input.key(),scan=input.scancode(),mods=input.modifiers();
        if(key==GLFW.GLFW_KEY_Z&&(mods&GLFW.GLFW_MOD_CONTROL)!=0) {

            if(getFocused() instanceof TextFieldWidget)return super.keyPressed(input);
            s.undo();return true;
        }
        if(key==GLFW.GLFW_KEY_ENTER||key==GLFW.GLFW_KEY_KP_ENTER){apply();return true;}
        return super.keyPressed(input);
    }
    private boolean viewport(double x,double y){return x>=0&&x<width-panel&&y>=56&&y<height-(compact?62:34);}
    @Override public boolean mouseClicked(net.minecraft.client.gui.Click click,boolean doubleClick) {
        double x=click.x(),y=click.y();int button=click.button();
        if(viewport(x,y)&&button>=0&&button<=2){drag=button;dragging=false;setFocused(null);return true;}
        return super.mouseClicked(click,doubleClick);
    }
    @Override public boolean mouseDragged(net.minecraft.client.gui.Click click,double dx,double dy) {
        double x=click.x(),y=click.y();int button=click.button();
        if(drag==button){
            if(!dragging){s.camera.beginGesture();dragging=true;}
            if(button==0)s.camera.look(dx,dy);else if(button==1)s.camera.pan(dx,dy);else s.camera.zoom(-dy*.25);
            return true;
        }
        return super.mouseDragged(click,dx,dy);
    }
    @Override public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        double x=click.x(),y=click.y();int button=click.button();
        if(drag==button){s.camera.endGesture();drag=-1;dragging=false;refresh();return true;}
        return super.mouseReleased(click);
    }
    @Override public boolean mouseScrolled(double x,double y,double horizontal,double steps) {
        if(viewport(x,y)){s.scroll(steps);refresh();return true;}
        return super.mouseScrolled(x,y,horizontal,steps);
    }
    @Override public void removed(){s.endGesture();drag=-1;}
    @Override public void close(){s.fly();}
}
