package sb.faithd1ck.module.combat;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.UpdateEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.value.ValueFloat;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleCriticals extends CheatModule {
    public ModuleCriticals() {
        super("Criticals", Category.COMBAT);
    }
    public static ValueFloat timer0 = new ValueFloat("Timer-0", 0.5f, 0.0f, 1.0f);
    public static ValueFloat timer1 = new ValueFloat("Timer-1", 1.2f, 0.0f, 2.0f);

    private boolean flag = false;
    private int tick = 0;

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
    }

    private Handler<UpdateEvent> updateEventHandler = event -> {
        if (mc.thePlayer.ticksExisted <= 10) return;
        if (!(FaithD1ck.moduleManager.getModule(ModuleKillAura.class).getState() && ModuleKillAura.target != null)) {
            tick = 0;
            flag = false;
            mc.timer.timerSpeed = 1f;
            return;
        }
        if (!mc.thePlayer.onGround) {
            tick++;
        } else {
            tick = 0;
        }
        if (!flag && tick >= 3 && FaithD1ck.moduleManager.getModule(ModuleKillAura.class).getState() && ModuleKillAura.target != null && mc.thePlayer.getDistanceToEntity(ModuleKillAura.target) <= ModuleKillAura.getRange()) {
            flag = true;
            tick = 0;
            mc.timer.timerSpeed = timer0.getValue();
        }

        if (flag && (tick >= 2 || mc.thePlayer.onGround)) {
            flag = false;
            tick = 0;
            mc.timer.timerSpeed = timer1.getValue();
        }
    };
}
