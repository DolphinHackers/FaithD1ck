package sb.faithd1ck.utils;

import com.google.gson.JsonObject;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static sb.faithd1ck.utils.IMinecraft.mc;

public class ClientUtils {
    public static final Logger LOGGER = LogManager.getLogger("FaithD1ck");

    public static void displayChatMessage(final String message) {
        if (mc.thePlayer == null) {
            LOGGER.info("(MCChat) " + message);
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", message);
        mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()));
    }
}
