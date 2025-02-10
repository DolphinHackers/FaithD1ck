package sb.faithd1ck.module.player;

import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.PacketEvent;
import sb.faithd1ck.event.impl.WorldEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.utils.ClientUtils;
import sb.faithd1ck.utils.MSTimer;
import sb.faithd1ck.utils.player.PlayerUtils;
import sb.faithd1ck.utils.player.PredictPlayer;
import sb.faithd1ck.value.ValueBoolean;
import sb.faithd1ck.value.ValueInt;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;

import static sb.faithd1ck.utils.IMinecraft.mc;

public class ModuleAntiVoid extends CheatModule {
    private final ValueInt pullbackTime = new ValueInt("PullbackTime", 800, 500, 2000);
    private final ValueBoolean onlyVoid = new ValueBoolean("OnlyVoid", false);
    public double[] lastGroundPos = new double[3];
    public static MSTimer timer = new MSTimer();
    public static ArrayList<Packet> packets = new ArrayList<>();

    public ModuleAntiVoid() {
        super("AntiVoid", Category.PLAYER);
    }

    private final Handler<WorldEvent> worldEventHandler = event -> {
        if (!packets.isEmpty()) {
            for (Packet packet : packets)
                mc.getNetHandler().sendPacketNoEvent(packet);
            packets.clear();
        }
    };

    private final Handler<PacketEvent> packetEventHandler = e -> {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.thePlayer.capabilities.allowFlying || mc.thePlayer.ticksExisted < 20)
            return;
        final PredictPlayer predictPlayer = new PredictPlayer();


        if (e.getType() == PacketEvent.Type.SEND) {
            if (e.getPacket() instanceof C03PacketPlayer) {
                if ((predictPlayer.findCollision(80) == null || !onlyVoid.getValue()) && !PlayerUtils.isBlockUnder(mc.thePlayer) && mc.thePlayer.motionY < 0) {
                    if (timer.delay(pullbackTime.getValue())) {
                        ClientUtils.displayChatMessage(EnumChatFormatting.AQUA + "[AntiVoid] " + EnumChatFormatting.RED + "Pulling back");
                        mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastGroundPos[0], lastGroundPos[1] + 10, lastGroundPos[2], true));
                        timer.reset();
                    }
                } else {
                    timer.reset();
                }
            }
        }
    };
}
