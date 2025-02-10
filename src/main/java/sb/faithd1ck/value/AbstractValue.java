package sb.faithd1ck.value;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.utils.Valider;

public abstract class AbstractValue<T> {
    private final String name;
    private T value;
    private Valider visible;

    public AbstractValue(final String name, final T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public <T extends AbstractValue<?>> T visible(final Valider visible) {
        this.visible = visible;
        return (T) this;
    }

    public boolean isVisible() {
        return this.visible == null || this.visible.validate();
    }

    public void setValue(T value) {
        this.value = value;
        if (!FaithD1ck.INSTANCE.isInitializing()) FaithD1ck.configManager.saveConfig(FaithD1ck.configManager.modulesConfig);
    }

    public abstract Object toYML();
    public abstract void fromYML(final String yaml);
}
