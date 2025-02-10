package sb.faithd1ck.config.impl;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.config.AbstractConfig;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.value.AbstractValue;

import org.lwjgl.input.Keyboard;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ModuleConfig extends AbstractConfig {

    public ModuleConfig(File file) {
        super(file);
    }

    @Override
    public void load() {
        final Yaml yaml = new Yaml();
        try {
            InputStream inputStream = new FileInputStream(getFile());
            Map<String, Object> data = yaml.load(inputStream);
            inputStream.close();
            if (data != null) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Map) {
                        Map<?, ?> moduleData = (Map<?, ?>) value;
                        CheatModule module = FaithD1ck.moduleManager.getModule(key);
                        if (module != null) {
                            if (moduleData.containsKey("State"))
                                module.setState((Boolean) moduleData.get("State"));
                            if (moduleData.containsKey("Hidden"))
                                module.setHidden((Boolean) moduleData.get("Hidden"));
                            if (moduleData.containsKey("KeyBind"))
                                module.setKeyBind(Keyboard.getKeyIndex(moduleData.get("KeyBind").toString()));
                            if (!module.getValues().isEmpty()) {
                                for (AbstractValue<?> moduleValue : module.getValues()) {
                                    String valueName = moduleValue.getName();
                                    if (moduleData.containsKey(valueName))
                                        moduleValue.fromYML(moduleData.get(valueName).toString());
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Handle file not found exception
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IO exception
        }
    }

    @Override
    public void save() {
        final Yaml yaml = new Yaml();
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
        options.setPrettyFlow(true);
        try {
            final BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(Files.newOutputStream(getFile().toPath()), StandardCharsets.UTF_8));
            Map<String, Object> data = new HashMap<>();

            FaithD1ck.moduleManager.getModules().forEach(module -> {
                Map<String, Object> moduleData = new HashMap<>();
                moduleData.put("State", module.getState());
                moduleData.put("Hidden", module.isHidden());
                moduleData.put("KeyBind", Keyboard.getKeyName(module.getKeyBind()));
                if (!module.getValues().isEmpty()) {
                    for (final AbstractValue<?> value : module.getValues()) {
                        moduleData.put(value.getName(), value.toYML());
                    }
                }
                data.put(module.getName(), moduleData);
            });

            yaml.dump(data, writer);
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}