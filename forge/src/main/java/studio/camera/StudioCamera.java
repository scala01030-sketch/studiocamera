package studio.camera;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.LoggerFactory;

@Mod("studiocamera")
public final class StudioCamera {
    public StudioCamera() {
        if(ModList.get().isLoaded("posestudio")) {
            LoggerFactory.getLogger("StudioCamera").info("Pose Studio installed: standalone camera disabled (no keys, UI or camera hooks).");
            return;
        }
        if(FMLEnvironment.dist==Dist.CLIENT) studio.camera.client.CameraClient.register();
    }
}
