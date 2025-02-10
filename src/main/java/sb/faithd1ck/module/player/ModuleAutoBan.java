package sb.faithd1ck.module.player;

import cn.hutool.core.util.RandomUtil;
import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.UpdateEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleAutoBan extends CheatModule {
    public ModuleAutoBan() {
        super("AutoBan", Category.PLAYER);
    }

    public final Handler<UpdateEvent> eventHandler = event -> {
        if (mc.thePlayer != null && mc.theWorld != null) {
            mc.thePlayer.motionY = RandomUtil.randomDouble(-10, 10);
            mc.thePlayer.motionX = RandomUtil.randomDouble(-10, 10);
            mc.thePlayer.motionZ = RandomUtil.randomDouble(-10, 10);
            mc.thePlayer.capabilities.isFlying = true;
            mc.thePlayer.capabilities.isCreativeMode = true;
            for (int i = 0; i < 50; i++) {
                mc.getNetHandler().sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                mc.getNetHandler().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
            }
        }
    };
}
