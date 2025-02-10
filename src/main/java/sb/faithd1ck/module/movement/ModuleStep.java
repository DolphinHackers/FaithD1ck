package sb.faithd1ck.module.movement;

import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.TickUpdateEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.utils.player.PlayerUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.stats.StatList;

import static sb.faithd1ck.utils.IMinecraft.mc;
import static sb.faithd1ck.utils.player.PlayerUtils.strafe;

public class ModuleStep extends CheatModule {
    public ModuleStep() {
        super("Step", Category.MOVEMENT);
    }
    public boolean stepped = false;

    @Override
    public String getSuffix() {
        return "BlocksMC";
    }

    public boolean couldStep() {
        EntityPlayerSP player = mc.thePlayer;
        if (player == null || player.isSneaking() || mc.gameSettings.keyBindJump.isKeyDown()) {
            return false;
        }

        float yaw = PlayerUtils.getMoveYaw(mc.thePlayer.rotationYaw);
        double heightOffset = 1.001335979112147;

        for (int i = -10; i <= 10; i++) {
            double adjustedYaw = yaw + Math.toRadians(i * 8.0);
            double x = -Math.sin(adjustedYaw) * 0.2;
            double z = Math.cos(adjustedYaw) * 0.2;

            if (!mc.theWorld.getCollisionBoxes(player.getEntityBoundingBox().offset(x, heightOffset, z)).isEmpty()) {
                return false;
            }
        }

        return true;
    }
    
    public final Handler<TickUpdateEvent> tickUpdateEventHandler = event -> {
        if (mc.thePlayer != null) {
            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedHorizontally &&
                    !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInLava() && 
                    !mc.thePlayer.isInWater() && !mc.thePlayer.isInWeb &&
                    couldStep()){
                mc.thePlayer.isAirBorne = true;
                mc.thePlayer.triggerAchievement(StatList.jumpStat);
                mc.thePlayer.jump();

                mc.timer.timerSpeed = 5f;
                stepped = true;
            }
            if (stepped) {
                switch (mc.thePlayer.offGroundTicks) {
                    case 1:
                        mc.timer.timerSpeed = 0.2f;
                        break;
                    case 2:
                        mc.timer.timerSpeed = 4f;
                        break;
                    case 3:
                        strafe(0.27F);
                        mc.timer.timerSpeed = 1f;
                        stepped = false;
                        break;
                    case 0:
                        mc.timer.timerSpeed = 1f;
                        break;
                }
            }
        }
    };
}
