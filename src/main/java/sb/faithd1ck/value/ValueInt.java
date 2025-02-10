package sb.faithd1ck.value;

import org.yaml.snakeyaml.Yaml;

public class ValueInt extends AbstractValue<Integer> {
    private final Integer minimum;
    private final Integer maximum;
    public ValueInt(String name, Integer value, Integer minimum, Integer maximum) {
        super(name, value);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public Integer getMinimum() {
        return minimum;
    }

    public Integer getMaximum() {
        return maximum;
    }

    @Override
    public Integer toYML() {
        return this.getValue();
    }

    @Override
    public void fromYML(String yaml) {
        final Integer value = new Yaml().load(yaml);
        this.setValue(value);
    }
}
