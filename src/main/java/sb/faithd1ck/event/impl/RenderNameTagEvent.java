package sb.faithd1ck.event.impl;

import sb.faithd1ck.event.CancelableEvent;
import net.minecraft.entity.EntityLivingBase;

public class RenderNameTagEvent extends CancelableEvent {

    private final EntityLivingBase entityLivingBase;

    public RenderNameTagEvent(EntityLivingBase entityLivingBase) {
        this.entityLivingBase = entityLivingBase;
    }

    public EntityLivingBase getEntityLivingBase() {
        return entityLivingBase;
    }

}
