package sb.faithd1ck.module.player;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.PacketEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.utils.player.Rotation;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleNoRotateSet extends CheatModule {
    public ModuleNoRotateSet() {
        super("NoRotateSet", Category.PLAYER);
    }

    public final Handler<PacketEvent> packetEventHandler = event -> {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            if (mc.thePlayer != null) {
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.getPacket();
                FaithD1ck.INSTANCE.getRotationManager().setRotation(new Rotation(packet.yaw, packet.pitch), 180f, false);
                packet.yaw = mc.thePlayer.rotationYaw;
                packet.pitch = mc.thePlayer.rotationPitch;
            }
        }
    };
}
