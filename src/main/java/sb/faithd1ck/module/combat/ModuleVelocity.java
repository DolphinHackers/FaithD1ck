package sb.faithd1ck.module.combat;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.LivingUpdateEvent;
import sb.faithd1ck.event.impl.PacketEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.utils.DebugUtil;
import sb.faithd1ck.value.ValueBoolean;
import sb.faithd1ck.value.ValueInt;
import sb.faithd1ck.value.ValueMode;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemFood;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.vialoadingbase.ViaLoadingBase;
import net.viamcp.fixes.AttackOrder;

import javax.vecmath.Vector2d;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleVelocity extends CheatModule {
    public ModuleVelocity() {
        super("Velocity", Category.COMBAT);
    }

    private ValueMode mode = new ValueMode("Mode", new String[]{"Cancel", "Watchdog","WatchDog2"}, "Watchdog");
    private ValueInt horizontal = new ValueInt("Horizontal", 0, 0, 100).visible(() -> mode.is("Cancel"));
    private ValueInt vertical = new ValueInt("Vertical", 0, 0, 100).visible(() -> mode.is("Cancel"));
    private ValueBoolean explosion = new ValueBoolean("Explosion",true);
    private ValueBoolean lagbackCheck = new ValueBoolean("Lagback",true).visible(() -> mode.is("Watchdog"));
    private ValueBoolean debug = new ValueBoolean("Debug",false);
    public boolean velocityInput;
    private boolean attacked;
    private double reduceXZ;
    public float velocityYaw;
    private int lastVelocityTick = 0;
    private int lagbackTimes = 0;
    private long lastLagbackTime = System.currentTimeMillis();
    private boolean lastTickStopMoving = false;

    @Override
    public String getSuffix() {
        return mode.getValue();
    }

    private final Handler<LivingUpdateEvent> livingUpdateEventHandler = event -> {
        if (lastTickStopMoving) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionZ = 0;
            lastTickStopMoving = false;
        }
    };

    private final Handler<PacketEvent> packetHandler = event -> {
        if (event.getType() == PacketEvent.Type.RECEIVE) {
            final Packet<?> packet = event.getPacket();

            if (packet instanceof S12PacketEntityVelocity) {
                if (mode.is("Cancel")) {
                    if (((S12PacketEntityVelocity) packet).getEntityID() == mc.thePlayer.getEntityId()) {
                        if (horizontal.getValue().equals(0) && vertical.getValue().equals(0)) {
                            event.setCancelled(true);
                        } else {
                            ((S12PacketEntityVelocity) packet).setMotionX(((S12PacketEntityVelocity) packet).getMotionX() * horizontal.getValue() / 100);
                            ((S12PacketEntityVelocity) packet).setMotionY(((S12PacketEntityVelocity) packet).getMotionY() * vertical.getValue() / 100);
                            ((S12PacketEntityVelocity) packet).setMotionZ(((S12PacketEntityVelocity) packet).getMotionZ() * horizontal.getValue() / 100);
                        }
                    }
                }

                if(mode.is("Watchdog")){
                    if (((S12PacketEntityVelocity) packet).getEntityID() == mc.thePlayer.getEntityId()) {
                        lastVelocityTick = mc.thePlayer.ticksExisted;
                        event.setCancelled(true);
                        if (mc.thePlayer.onGround || ((S12PacketEntityVelocity) packet).getMotionY() / 8000.0D < .2 || ((S12PacketEntityVelocity) packet).getMotionY() / 8000.0D > .41995) {
                            mc.thePlayer.motionY = ((S12PacketEntityVelocity) packet).getMotionY() / 8000.0D;
                        }
                        if (debug.getValue()) {
                            DebugUtil.log("§cKnockback tick: " + mc.thePlayer.ticksExisted);
                        }
                    }
                }

                if(mode.is("WatchDog2") && ((S12PacketEntityVelocity) packet).getEntityID() == mc.thePlayer.getEntityId()){
                    // Check if this is a regular velocity update
                        if (mc.thePlayer.onGround) {
                            ((S12PacketEntityVelocity) packet).setMotionX((int) (mc.thePlayer.motionX * 8000));
                            ((S12PacketEntityVelocity) packet).setMotionZ((int) (mc.thePlayer.motionZ * 8000));
                        } else {
                            event.setCancelled(true);
                        }
                    }
            }

            if (packet instanceof S27PacketExplosion) {
                S27PacketExplosion wrappedPacket = ((S27PacketExplosion) packet);
                if(!mode.is("Grim") && explosion.getValue() && (wrappedPacket.func_149149_c() >= 0.02 || wrappedPacket.func_149144_d() >= 0.02 || wrappedPacket.func_149147_e() >= 0.02)) {
                    event.setCancelled(true);
                    lastVelocityTick = mc.thePlayer.ticksExisted;
                    if (debug.getValue()) {
                       DebugUtil.log("§cRecevied explosion packet");
                    }
                }
            }

            if (packet instanceof S08PacketPlayerPosLook && mode.is("Watchdog") && mc.thePlayer.ticksExisted >= 40 && mc.thePlayer.ticksExisted - lastVelocityTick <= 20) {
                if (System.currentTimeMillis() - lastLagbackTime <= 4000) {
                    lagbackTimes += 1;
                } else {
                    lagbackTimes = 1;
                }
                lastLagbackTime = System.currentTimeMillis();
            }
        }
    };
}
