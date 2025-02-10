package sb.faithd1ck.module.player;

import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.PacketEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleChatBypass extends CheatModule {
    public String lastSend = "";
    public ModuleChatBypass() {
        super("ChatBypass", Category.PLAYER);
    }

    public final Handler<PacketEvent> packetEventHandler = event -> {
        Packet packet = event.getPacket();
        if (event.getType() == PacketEvent.Type.SEND) {
            if (packet instanceof C01PacketChatMessage) {
                lastSend = ((C01PacketChatMessage) packet).getMessage();
            }
        } else {
            if (lastSend.isEmpty()) return;
            if (packet instanceof S02PacketChat) {
                String msg = ((S02PacketChat) packet).getChatComponent().getUnformattedText();
                if (msg.contains("不合法")) {
                    mc.thePlayer.sendChatMessage(msg);
                    event.setCancelled(true);
                } else if (msg.contains("频繁发送重复")) {
                    event.setCancelled(true);
                }
            }
        }
    };
}
