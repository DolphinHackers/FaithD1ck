package sb.faithd1ck.module.movement;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.MotionEvent;
import sb.faithd1ck.event.impl.PacketEvent;
import sb.faithd1ck.event.impl.SlowDownEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.module.combat.ModuleKillAura;
import sb.faithd1ck.utils.HYTUtils;
import sb.faithd1ck.utils.player.PlayerUtils;
import sb.faithd1ck.value.ValueBoolean;
import sb.faithd1ck.value.ValueMode;
import io.netty.buffer.Unpooled;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleNoSlow extends CheatModule {
    public ModuleNoSlow() {
        super("NoSlow", Category.MOVEMENT);
    }

    int usingItemTick = 0;

    private static final ValueMode mode = new ValueMode("Mode", new String[]{"BlocksMC"}, "BlocksMC");

    private final Handler<SlowDownEvent> slowDownHandler = event -> {

        if (mode.is("BlocksMC")) {
            if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) event.setCancelled(true);
        }
    };
}