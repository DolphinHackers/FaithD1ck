package sb.faithd1ck.event;

public interface Handler<T extends Event> {
    void invoke(final T event);
}
