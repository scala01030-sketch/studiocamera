package studio.camera.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

public final class CameraClient {
    public static final KeyMapping TOGGLE=new KeyMapping("key.studiocamera.toggle",GLFW.GLFW_KEY_F9,"key.categories.studiocamera");
    public static final KeyMapping PANEL=new KeyMapping("key.studiocamera.panel",GLFW.GLFW_KEY_F10,"key.categories.studiocamera");
    public static final KeyMapping CAPTURE=new KeyMapping("key.studiocamera.capture",GLFW.GLFW_KEY_F12,"key.categories.studiocamera");
    public static void register() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterKeyMappingsEvent e)->{e.register(TOGGLE);e.register(PANEL);e.register(CAPTURE);});
        var bus=MinecraftForge.EVENT_BUS;
        bus.addListener((TickEvent.ClientTickEvent e)-> {if(e.phase==TickEvent.Phase.END){
            while(TOGGLE.consumeClick()) CameraSession.INSTANCE.toggle();
            while(PANEL.consumeClick()) CameraSession.INSTANCE.togglePanel();
            while(CAPTURE.consumeClick()) CameraSession.INSTANCE.toggleCapture();
            CameraSession.INSTANCE.tick();
        }});
        bus.addListener((TickEvent.RenderTickEvent e)-> {if(e.phase==TickEvent.Phase.START) CameraSession.INSTANCE.frame();});
        bus.addListener((ClientPlayerNetworkEvent.LoggingOut e)->CameraSession.INSTANCE.exit());
        bus.addListener((ViewportEvent.ComputeCameraAngles e)-> {if(CameraSession.INSTANCE.active){var c=CameraSession.INSTANCE.camera;e.setYaw((float)c.yaw);e.setPitch((float)c.pitch);e.setRoll((float)c.roll);}});
        bus.addListener((ViewportEvent.ComputeFov e)-> {if(CameraSession.INSTANCE.active && e.usedConfiguredFov())e.setFOV(CameraSession.INSTANCE.camera.fov);});
        bus.addListener((RenderHandEvent e)-> {if(CameraSession.INSTANCE.active)e.setCanceled(true);});
        bus.addListener((RenderGuiOverlayEvent.Pre e)-> {if(CameraSession.INSTANCE.active)e.setCanceled(true);});
    }
}
