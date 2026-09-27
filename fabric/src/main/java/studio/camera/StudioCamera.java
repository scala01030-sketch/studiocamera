package studio.camera;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;
public final class StudioCamera implements ClientModInitializer {
    @Override public void onInitializeClient() {
        if(FabricLoader.getInstance().isModLoaded("posestudio")) {
            LoggerFactory.getLogger("StudioCamera").info("Pose Studio installed: standalone camera disabled (no keys, UI or camera hooks).");
            return;
        }
        studio.camera.client.CameraClient.register();
    }
}
