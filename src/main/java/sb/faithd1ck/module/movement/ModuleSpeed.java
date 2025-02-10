package sb.faithd1ck.module.movement;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.MotionEvent;
import sb.faithd1ck.event.impl.UpdateEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.module.player.ModuleBlink;
import sb.faithd1ck.utils.player.PlayerUtils;
import sb.faithd1ck.value.ValueBoolean;
import sb.faithd1ck.value.ValueFloat;
import sb.faithd1ck.value.ValueMode;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleSpeed extends CheatModule {
    public ModuleSpeed() {
        super("Speed",Category.MOVEMENT);
    }

    private ValueMode mode = new ValueMode("Mode", new String[]{"BlocksMC"}, "BlocksMC");
    ValueBoolean safeY = new ValueBoolean("SafeY",true).visible(() -> mode.is("BlocksMC"));
    ValueBoolean faststop = new ValueBoolean("FastStop",true).visible(() -> mode.is("BlocksMC"));
    ValueBoolean lowhop = new ValueBoolean("Lowhop",true).visible(() -> mode.is("BlocksMC"));
    ValueBoolean fullStrafe = new ValueBoolean("FullStrafe",false).visible(() -> mode.is("BlocksMC"));
    ValueBoolean dmgBoost = new ValueBoolean("DamageBoost",true).visible(() -> mode.is("BlocksMC"));
    ValueBoolean dmgLowhop = new ValueBoolean("DamageLowhop",false).visible(() -> mode.is("BlocksMC"));
    private int offGroundticks;
    @Override
    public String getSuffix() {
        return mode.getValue();
    }

    private final Handler<UpdateEvent> updateEventHandler = event -> {
        if (mc.thePlayer.onGround) {
            offGroundticks = 0;
        } else {
            offGroundticks++;
        }
        if (mode.is("BlocksMC")) {
            mc.thePlayer.setSprinting(true);
        }
    };

    private final Handler<MotionEvent> motionEventHandler = event -> {
        if (mode.is("BlocksMC")) {
            if (faststop.getValue() && !PlayerUtils.isMoving()) {
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
            }
            if (event.isPre()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
                if (PlayerUtils.isMoving()) {
                    if (!lowhop.getValue()) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            PlayerUtils.strafe();
                        }
                    } else {
                        switch (mc.thePlayer.offGroundTicks) {
                            case 0:
                                mc.thePlayer.jump();
                                PlayerUtils.strafe();
                                break;
                            case 4:
                                if (safeY.getValue()) {
                                    if (mc.thePlayer.posY % 1.0 == 0.16610926093821377) mc.thePlayer.motionY = -0.09800000190734863;
                                } else mc.thePlayer.motionY = -0.09800000190734863;
                        }
                    }

                    if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null && (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier()) > 0 && mc.thePlayer.offGroundTicks == 3) {
                        mc.thePlayer.motionX *= 1.12;
                        mc.thePlayer.motionZ *= 1.12;
                    }
                    
                    if (fullStrafe.getValue()) {
                        PlayerUtils.strafe(PlayerUtils.getSpeed() - 0.004);
                    } else if (mc.thePlayer.offGroundTicks == 6) {
                        PlayerUtils.strafe();
                    }
                    
                    if (dmgBoost.getValue() && mc.thePlayer.hurtTime > 8) {
                        PlayerUtils.strafe(Math.max(PlayerUtils.getSpeed(), 0.7));
                    }
                    
                    if (dmgLowhop.getValue() && mc.thePlayer.hurtTime > 0 && mc.thePlayer.motionY > 0) {
                        mc.thePlayer.motionY = -0.15;
                    }
                }
            }
        }
    };
}
