package sb.faithd1ck.module.world;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.MotionEvent;
import sb.faithd1ck.event.impl.PacketEvent;
import sb.faithd1ck.event.impl.UpdateEvent;
import sb.faithd1ck.event.impl.WorldEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.module.movement.ModuleSpeed;
import sb.faithd1ck.ui.notifiction.NotificationType;
import sb.faithd1ck.utils.DebugUtil;
import sb.faithd1ck.utils.ServerUtils;
import sb.faithd1ck.utils.Servers;
import sb.faithd1ck.utils.player.PlayerUtils;
import sb.faithd1ck.value.ValueBoolean;
import sb.faithd1ck.value.ValueMode;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;

import java.util.ArrayList;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleDisabler extends CheatModule {
    private ValueMode mode = new ValueMode("Mode", new String[]{"BlocksMC"}, "BlocksMC");
    private int lastSlot = 0;
    public ModuleDisabler() {
        super("Disabler",Category.WORLD);
    }

    @Override
    public String getSuffix() {
        return mode.getValue();
    }

    public static ArrayList<C0FPacketConfirmTransaction> c0fStorage = new ArrayList<>();
    
    public Handler<WorldEvent> worldEventHandler = event -> {
        lastSlot = 0;
    };

    private Handler<PacketEvent> packetEventHandler = event -> {
        Packet<?> packet = event.getPacket();
        if (event.getType() == PacketEvent.Type.SEND) {
            if (packet instanceof C09PacketHeldItemChange) {
                if (lastSlot == ((C09PacketHeldItemChange) packet).getSlotId()) {
                    event.setCancelled(true);
                }
                lastSlot = ((C09PacketHeldItemChange) packet).getSlotId();
            }
            if (mode.is("BlocksMC")) {
                if (event.getPacket() instanceof C0BPacketEntityAction && 
                        (((C0BPacketEntityAction) event.getPacket()).getAction() == C0BPacketEntityAction.Action.START_SPRINTING || 
                                ((C0BPacketEntityAction) event.getPacket()).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING)) {
                    event.setCancelled(true);
                }
            }
        } else {
            if (packet instanceof S09PacketHeldItemChange) {
                lastSlot = ((S09PacketHeldItemChange) packet).getHeldItemHotbarIndex();
            }
        }
    };
}
