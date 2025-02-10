package sb.faithd1ck.module.player;

import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.*;
import sb.faithd1ck.event.impl.MotionEvent;
import sb.faithd1ck.event.impl.PacketEvent;
import sb.faithd1ck.event.impl.TickUpdateEvent;
import sb.faithd1ck.event.impl.WorldEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.utils.ClientUtils;
import sb.faithd1ck.utils.player.InventoryUtil;
import sb.faithd1ck.value.ValueBoolean;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.*;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import java.util.concurrent.LinkedBlockingQueue;

import static sb.faithd1ck.utils.IMinecraft.mc;

public class ModuleAutoGoldenApple extends CheatModule {
    private final ValueBoolean autoDisable = new ValueBoolean("AutoDisable", true);
    private final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();
    private boolean disableLogger = false;
    private int count = 0;
    private boolean lastSprinting = false;
    private boolean thanksMojang = false;

    private double motionX = 0.0;
    private double motionY = 0.0;
    private double motionZ = 0.0;

    public ModuleAutoGoldenApple() {
        super("AutoGoldenApple", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null) {
            return;
        }
        motionX = mc.thePlayer.motionX;
        motionY = mc.thePlayer.motionY;
        motionZ = mc.thePlayer.motionZ;
        count = 0;
        packets.clear();
    }

    @Override
    public void onDisable() {
        clearPackets();
        mc.thePlayer.motionX = motionX;
        mc.thePlayer.motionY = motionY;
        mc.thePlayer.motionZ = motionZ;
        count = 0;
        packets.clear();
    }

    private final Handler<MotionEvent> motionHandler = event -> {
        if (event.isPre()) {
            if (!mc.thePlayer.onGround) {
                mc.thePlayer.motionX = 0.0;
                mc.thePlayer.motionY = 0.0;
                mc.thePlayer.motionZ = 0.0;
            }
        } else {
            if (mc.thePlayer.motionX != 0.0 || mc.thePlayer.motionZ != 0.0 || mc.thePlayer.motionY != 0.0) {
                motionX = mc.thePlayer.motionX;
                motionY = mc.thePlayer.motionY;
                motionZ = mc.thePlayer.motionZ;
            }
        }
    };
    
    private final Handler<WorldEvent> worldHandler = event -> {
        this.setState(false);
    };

    private final Handler<TickUpdateEvent> tickHandler = event -> {
        if (mc.thePlayer == null || mc.thePlayer.isDead) {
            setState(false);
        }

        final int goldenAppleSlot = InventoryUtil.findItem2(36, 45, Items.golden_apple);

        if (goldenAppleSlot == -1) {
            ClientUtils.displayChatMessage("You haven't any GoldenApple!");
            setState(false);
            return;
        }

        mc.gameSettings.keyBindForward.pressed = false;
        mc.gameSettings.keyBindBack.pressed = false;
        mc.gameSettings.keyBindLeft.pressed = false;
        mc.gameSettings.keyBindRight.pressed = false;
        mc.gameSettings.keyBindJump.pressed = false;

        for (int i = 0; i < 2; i++) {
            try {
                disableLogger = true;
                if (!packets.isEmpty()) {
                    final Packet<?> packet = packets.take();
                    mc.getNetHandler().addToSendQueue(packet);
                    if (packet instanceof C03PacketPlayer) {
                        count--;
                    }
                    if (packet instanceof C07PacketPlayerDigging) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    }
                }
                disableLogger = false;
            } catch (Exception e) {
                disableLogger = false;
            }
        }

        if (count >= 32) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(goldenAppleSlot - 36), true);
            mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(goldenAppleSlot - 36)), true);
            clearPackets();
            mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem), true);
            ClientUtils.displayChatMessage("GApple eaten");
            if (autoDisable.getValue()) setState(false);
        }
    };

    private final Handler<PacketEvent> packetHandler = event -> {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof C0BPacketEntityAction) {
            if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                if (lastSprinting) {
                    if (!thanksMojang) {
                        thanksMojang = true;
                        return;
                    }
                    return;
                }
                lastSprinting = true;
            } else if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                if (!lastSprinting) {
                    if (!thanksMojang) {
                        thanksMojang = true;
                        return;
                    }
                    return;
                }
                lastSprinting = false;
            }
        }

        if (mc.thePlayer == null || disableLogger) return;
        if (event.getType().equals(PacketEvent.Type.SEND)) {
            if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart || packet instanceof C00PacketServerQuery || packet instanceof C01PacketChatMessage || packet instanceof C01PacketEncryptionResponse || packet instanceof C01PacketPing || (packet instanceof C09PacketHeldItemChange) || (packet instanceof C0EPacketClickWindow)) {
                return;
            }
            if (packet instanceof C03PacketPlayer) {
                count++;
            }
            event.setCancelled(true);
            packets.add(packet);
        }
    };

    private void clearPackets() {
        try {
            disableLogger = true;
            packets.removeIf(packet -> (packet instanceof C07PacketPlayerDigging));
            while (!packets.isEmpty()) {
                mc.getNetHandler().addToSendQueue(packets.take());
            }
            count = 0;
            disableLogger = false;
        } catch (Exception e) {
            disableLogger = false;
        }
    }
}
