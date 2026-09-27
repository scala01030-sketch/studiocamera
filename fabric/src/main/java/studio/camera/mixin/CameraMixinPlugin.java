package studio.camera.mixin;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
public final class CameraMixinPlugin implements IMixinConfigPlugin {
    @Override public void onLoad(String packageName) {}
    @Override public boolean shouldApplyMixin(String target, String mixin) {return !FabricLoader.getInstance().isModLoaded("posestudio");}
    @Override public String getRefMapperConfig() {return null;}
    @Override public void acceptTargets(Set<String> mine,Set<String> others) {}
    @Override public List<String> getMixins() {return null;}
    @Override public void preApply(String target,ClassNode node,String mixin,IMixinInfo info) {}
    @Override public void postApply(String target,ClassNode node,String mixin,IMixinInfo info) {}
}
