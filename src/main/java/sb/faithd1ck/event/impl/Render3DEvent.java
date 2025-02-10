package sb.faithd1ck.event.impl;

import sb.faithd1ck.event.Event;

public class Render3DEvent extends Event {
    private final float partialTicks;

    public Render3DEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
