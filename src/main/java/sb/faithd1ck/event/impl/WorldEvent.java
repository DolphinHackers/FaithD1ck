package sb.faithd1ck.event.impl;

import sb.faithd1ck.event.Event;
import net.minecraft.client.multiplayer.WorldClient;

public class WorldEvent extends Event {
    private final WorldClient worldClient;

    public WorldEvent(final WorldClient worldClient) {
        this.worldClient = worldClient;
    }

    public WorldClient getWorld() {
        return worldClient;
    }
}
