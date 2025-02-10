package sb.faithd1ck.event.impl;

import sb.faithd1ck.event.Event;
import net.minecraft.entity.Entity;

public class EntityJoinWorldEvent extends Event {
    public final Entity entity;

    public EntityJoinWorldEvent(Entity entity) {
        this.entity = entity;
    }
}
