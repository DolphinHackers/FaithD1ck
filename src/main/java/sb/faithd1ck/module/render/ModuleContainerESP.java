package sb.faithd1ck.module.render;

import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.Render3DEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.module.world.ModuleContainerAura;
import sb.faithd1ck.utils.render.RenderUtils;
import net.minecraft.tileentity.*;

import java.awt.*;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleContainerESP extends CheatModule {
    public ModuleContainerESP() {
        super("ContainerESP", Category.RENDER);
    }

    private Handler<Render3DEvent> render3DEventHandler = event -> {
        try {
            float gamma = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = 100000.0F;

            for (final TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
                if (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityBrewingStand || tileEntity instanceof TileEntityFurnace) {
                    Color color;

                    color = new Color(255, 43, 28);
                    if (ModuleContainerAura.openedContainer.contains(tileEntity.getPos())) {
                        color = new Color(37, 247, 240);
                    }

                    RenderUtils.drawBlockBox(tileEntity.getPos(), color, false);
                }
            }

            RenderUtils.glColor(new Color(255, 255, 255, 255));
            mc.gameSettings.gammaSetting = gamma;
        } catch (Exception ignored) {
        }
    };
}
