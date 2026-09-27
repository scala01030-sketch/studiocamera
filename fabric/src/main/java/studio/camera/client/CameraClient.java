package studio.camera.client;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
public final class CameraClient {
    private static final KeyBinding.Category CATEGORY=KeyBinding.Category.create(Identifier.of("studiocamera","camera"));
    public static final KeyBinding TOGGLE=new KeyBinding("key.studiocamera.toggle",GLFW.GLFW_KEY_F9,CATEGORY);
    public static final KeyBinding PANEL=new KeyBinding("key.studiocamera.panel",GLFW.GLFW_KEY_F10,CATEGORY);
    public static final KeyBinding CAPTURE=new KeyBinding("key.studiocamera.capture",GLFW.GLFW_KEY_F12,CATEGORY);
    public static void register() {
        KeyBindingHelper.registerKeyBinding(TOGGLE);KeyBindingHelper.registerKeyBinding(PANEL);KeyBindingHelper.registerKeyBinding(CAPTURE);
        ClientTickEvents.END_CLIENT_TICK.register(mc->{
            while(TOGGLE.wasPressed())CameraSession.INSTANCE.toggle();
            while(PANEL.wasPressed())CameraSession.INSTANCE.togglePanel();
            while(CAPTURE.wasPressed())CameraSession.INSTANCE.toggleCapture();
            CameraSession.INSTANCE.tick();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler,mc)->CameraSession.INSTANCE.exit());
    }
}
