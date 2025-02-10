package sb.faithd1ck.module.world;

import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.AttackEvent;
import sb.faithd1ck.event.impl.MotionEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleAutoTool extends CheatModule {

    private int oldSlot;
    private int tick;

    /* constructors */
    public ModuleAutoTool() {
        super("AutoTool", Category.WORLD);
    }

    /* methods */
    private final Handler<MotionEvent> motionEventHandler = event -> {
        if(event.isPre()) {
            if (mc.playerController.isBreakingBlock()) {
                tick++;
                if (tick == 1) {
                    oldSlot = mc.thePlayer.inventory.currentItem;
                }
                mc.thePlayer.updateTool(mc.objectMouseOver.getBlockPos());
            } else if (tick > 0) {
                    mc.thePlayer.inventory.currentItem = oldSlot;
                tick = 0;
            }
        }
    };

    private final Handler<AttackEvent> attackHandler = event -> {
        if (mc.thePlayer.inventory.getCurrentItem() == null || (mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemAxe)) {
            mc.thePlayer.inventory.currentItem = 0;
        }
    };
}