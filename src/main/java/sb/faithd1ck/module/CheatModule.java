package sb.faithd1ck.module;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.event.Listener;
import sb.faithd1ck.ui.notifiction.NotificationType;
import sb.faithd1ck.value.AbstractValue;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public abstract class CheatModule implements Listener {
    private String name;
    private Category category;
    private int keyBind;
    private boolean state;
    private boolean isHidden = false;
    private final List<AbstractValue<?>> values = new ArrayList<>();
    private boolean valuesAdded = false;
    private boolean isExpanded = false;
    public float slide = 0F;
    public float slideStep = 0F;
    public float height = 0F;

    public CheatModule(final String name, final Category category) {
        this(name, category, Keyboard.KEY_NONE);
    }

    public CheatModule(final String name, final Category category, final int keyBind) {
        this.name = name;
        this.category = category;
        this.keyBind = keyBind;
    }

    public int getKeyBind() {
        return keyBind;
    }

    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }

    public String getName() {
        return name;
    }

    public String getSuffix() {
        return "";
    }

    public boolean suffixIsNotEmpty() {
        return !getSuffix().isEmpty();
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public boolean getState() {
        return state;
    }

    public Category getCategory() {
        return category;
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public void setState(boolean state) {
        if (this.state == state) return;
        this.state = state;
        if (!FaithD1ck.INSTANCE.isInitializing()) {
            if (state) {
                onEnable();
                FaithD1ck.notificationManager.pop("Enabled", "Enabled " + getName() + ".", 1000, NotificationType.SUCCESS);
            } else {
                onDisable();
                FaithD1ck.notificationManager.pop("Disabled", "Disabled " + getName() + ".", 1000, NotificationType.ERROR);
            }
        }
    }   

    public void toggle() {
        setState(!getState());
        if (!FaithD1ck.INSTANCE.isInitializing())
            FaithD1ck.configManager.saveConfig(FaithD1ck.configManager.modulesConfig);
    }

    public List<AbstractValue<?>> getValues() {
        if (!valuesAdded) {
            Arrays.stream(this.getClass().getDeclaredFields()).forEach(field -> {
                field.setAccessible(true);
                try {
                    final Object object = field.get(this);
                    if (object instanceof AbstractValue) {
                        values.add((AbstractValue<?>) object);
                    }
                } catch (final Exception ignored) {

                }
            });
            valuesAdded = true;
        }

        return values;
    }

    public AbstractValue<?> getValue(final String valueName) {
        return this.values.stream().filter(value -> value.getName().toLowerCase().equals(valueName.toLowerCase())).findFirst().orElse(null);
    }

    @Override
    public boolean isAccessible() {
        return state;
    }
}
