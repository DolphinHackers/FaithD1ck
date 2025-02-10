package sb.faithd1ck.event.impl;

import sb.faithd1ck.event.Event;

public class KeyEvent extends Event {
    private final int key;

    public int getKey() {
        return key;
    }

    public KeyEvent(int key) {
        this.key = key;
    }
}
