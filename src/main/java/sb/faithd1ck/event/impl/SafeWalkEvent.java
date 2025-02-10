package sb.faithd1ck.event.impl;


import sb.faithd1ck.event.Event;

public class SafeWalkEvent extends Event {

    private boolean safe;

    public boolean isSafe() {
        return this.safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

}
