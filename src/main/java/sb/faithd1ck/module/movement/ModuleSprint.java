package sb.faithd1ck.module.movement;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.UpdateEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.module.world.ModuleScaffold;
import sb.faithd1ck.utils.player.PlayerUtils;
import sb.faithd1ck.value.ValueBoolean;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleSprint extends CheatModule {

    public ModuleSprint() {
        super("Sprint", Category.MOVEMENT);
    }
    public static ValueBoolean allDirection = new ValueBoolean("AllDirection", false);

    private final Handler<UpdateEvent> tickUpdateEventHandler = event -> {
        if(FaithD1ck.moduleManager.getModule(ModuleScaffold.class).getState())return;
        if(FaithD1ck.moduleManager.getModule(ModuleTargetStrafe.class).getState() && (!mc.thePlayer.onGround || mc.thePlayer.onGroundTicks < 3)) return;
        if ((mc.thePlayer.moveForward > 0 || allDirection.getValue()) && mc.thePlayer.getFoodStats().getFoodLevel() > 6 && PlayerUtils.isMoving()) {
            mc.thePlayer.setSprinting(true);
        }
    };
}
