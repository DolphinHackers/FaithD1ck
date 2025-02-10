package sb.faithd1ck.event.impl;

import sb.faithd1ck.event.Event;
import sb.faithd1ck.utils.Servers;

public class HypixelServerSwitchEvent extends Event {
    public final Servers lastServer;
    public final Servers server;

    public HypixelServerSwitchEvent(Servers lastServer, Servers server) {
        this.lastServer = lastServer;
        this.server = server;
    }
}
