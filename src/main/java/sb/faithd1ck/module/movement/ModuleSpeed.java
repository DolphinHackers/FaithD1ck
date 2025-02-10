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

    private ValueMode mode = new ValueMode("Mode", new String[]{"BlocksMC", "EntityBoost"}, "BlocksMC");
    ValueBoolean safeY = new ValueBoolean("SafeY",true).visible(() -> mode.is("BlocksMC"));
    ValueBoolean faststop = new ValueBoolean("FastStop",true).visible(() -> mode.is("BlocksMC"));
    ValueBoolean lowhop = new ValueBoolean("Lowhop",true).visible(() -> mode.is("BlocksMC"));
    ValueBoolean fullStrafe = new ValueBoolean("FullStrafe",false).visible(() -> mode.is("BlocksMC"));
    ValueBoolean dmgBoost = new ValueBoolean("DamageBoost",true).visible(() -> mode.is("BlocksMC"));
    ValueBoolean dmgLowhop = new ValueBoolean("DamageLowhop",false).visible(() -> mode.is("BlocksMC"));
    ValueFloat speed = new ValueFloat("Speed", 0.08f, 0f, 0.1f).visible(() -> mode.is("EntityBoost"));
    ValueBoolean follow = new ValueBoolean("FollowTargetOnSpace",true).visible(() -> mode.is("EntityBoost"));
    ValueBoolean mcount = new ValueBoolean("MultiCount",true).visible(() -> mode.is("EntityBoost"));
    ValueBoolean antivoid = new ValueBoolean("AntiVoid",true).visible(() -> mode.is("EntityBoost"));
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
        } else if (mode.is("entityboost")) {
            if (event.isPre()) {
                entityBoost();
            }
        }
    };

    private void entityBoost() {
        EntityPlayerSP thePlayer = mc.thePlayer;
        AxisAlignedBB playerBox = mc.thePlayer.boundingBox.expand(1.0, 1.0, 1.0);
        int c = 0;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase && !(entity instanceof EntityArmorStand) && entity.getEntityId() != mc.thePlayer.getEntityId() && playerBox.intersectsWith(entity.boundingBox) && entity.getEntityId() != -8 && entity.getEntityId() != -1337 && !FaithD1ck.moduleManager.getModule(ModuleBlink.class).getState()) {
                c += 1;
                if (mcount.getValue()) {
                    break;
                }
            }
        }
        if (c > 0) {
            double strafeOffset = c * speed.getValue();
            double speedOffset = c * speed.getValue();

            if (thePlayer.movementInput.moveForward == 0 && thePlayer.movementInput.moveStrafe == 0) {
                if (thePlayer.motionX > strafeOffset) {
                    thePlayer.motionX -= strafeOffset;
                } else if (thePlayer.motionX < -strafeOffset) {
                    thePlayer.motionX += strafeOffset;
                } else {
                    thePlayer.motionX = 0.0;
                }
                if (thePlayer.motionZ > strafeOffset) {
                    thePlayer.motionZ -= strafeOffset;
                } else if (thePlayer.motionZ < -strafeOffset) {
                    thePlayer.motionZ += strafeOffset;
                } else {
                    thePlayer.motionZ = 0.0;
                }

            }
            float yaw = getYaw();

            double mx = -Math.sin(Math.toRadians(yaw));

            if (mx < 0.0) {
                if (thePlayer.motionX > strafeOffset) {
                    thePlayer.motionX -= strafeOffset;
                } else
                    thePlayer.motionX += mx * speedOffset;

            } else if (mx > 0.0) {
                if (thePlayer.motionX < -strafeOffset) {
                    thePlayer.motionX += strafeOffset;
                } else
                    thePlayer.motionX += mx * speedOffset;

            }

            double mz = Math.cos(Math.toRadians(yaw));
            if (mz < 0.0) {
                if (thePlayer.motionZ > strafeOffset) {
                    thePlayer.motionZ -= strafeOffset;
                } else
                    thePlayer.motionZ += mz * speedOffset;

            } else if (mz > 0.0) {
                if (thePlayer.motionZ < -strafeOffset) {
                    thePlayer.motionZ += strafeOffset;
                } else
                    thePlayer.motionZ += mz * speedOffset;
            }
        }
    }
    private float getYaw() {
        if(follow.getValue() && mc.gameSettings.keyBindJump.pressed){
            float yaw = FaithD1ck.INSTANCE.getRotationManager().rotation.getX();
            if (antivoid.getValue() && isVoid(yaw)) {
                yaw += 180.0f;
            }
            return yaw;
        }else{
            return mc.thePlayer.rotationYaw;
        }
    }

    private boolean isVoid(float yaw) {
        double mx = -Math.sin(Math.toRadians(yaw));
        double mz = Math.cos(Math.toRadians(yaw));
        double posX = mc.thePlayer.posX + (1.5 * mx);
        double posZ = mc.thePlayer.posZ + (1.5 * mz);
        for (int i = 0; i < 16; i++) {
            if (!mc.theWorld.isAirBlock(new BlockPos(posX, mc.thePlayer.posY - i, posZ))) {
                return false;
            }
        }
        return true;
    }
}
