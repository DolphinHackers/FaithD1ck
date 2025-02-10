package sb.faithd1ck.event.impl;

import sb.faithd1ck.event.Event;

public class StepEvent extends Event {
    private float stepHeight;
    private final Type type;

    public StepEvent(final float stepHeight, final Type type) {
        this.stepHeight = stepHeight;
        this.type = type;
    }

    public float getStepHeight() {
        return stepHeight;
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        PRE, POST
    }
}
