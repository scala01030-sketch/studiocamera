package studio.camera.mixin;

import java.util.List;
import java.util.Set;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;


public final class CameraMixinPlugin implements IMixinConfigPlugin {
    @Override public void onLoad(String packageName) {}
    @Override public boolean shouldApplyMixin(String target, String mixin) {
        var mods=FMLLoader.getLoadingModList();
        if(mods==null) throw new IllegalStateException("Studio Camera requires the Forge mod discovery list before mixin selection");
        return mods.getMods().stream().noneMatch(m->m.getModId().equals("posestudio"));
    }
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> mine, Set<String> others) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String target, ClassNode node, String mixin, IMixinInfo info) {}
    @Override public void postApply(String target, ClassNode node, String mixin, IMixinInfo info) {}
}
