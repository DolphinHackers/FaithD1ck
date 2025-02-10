package sb.faithd1ck.event.impl;

import sb.faithd1ck.event.Event;
import org.lwjgl.util.vector.Vector2f;

public class LookEvent extends Event {
    private Vector2f rotation;



    public LookEvent(Vector2f rotation) {
        this.rotation = rotation;
    }

    
    public Vector2f getRotation() {
        return this.rotation;
    }

    
    
    public void setRotation(final Vector2f rotation) {
        this.rotation = rotation;
    }
}
    
