package sb.faithd1ck;

import com.google.common.reflect.ClassPath;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import sb.faithd1ck.command.CommandManager;
import sb.faithd1ck.command.impl.ModuleCommand;
import sb.faithd1ck.component.SmoothCameraComponent;
import sb.faithd1ck.config.ConfigManager;
import sb.faithd1ck.event.EventManager;
import sb.faithd1ck.hackerdetector.HackerDetector;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.module.ModuleManager;
import sb.faithd1ck.ui.font.FontManager;
import sb.faithd1ck.ui.notifiction.NotificationManager;
import sb.faithd1ck.utils.SlotSpoofManager;
import sb.faithd1ck.utils.player.RotationManager;
import sb.faithd1ck.utils.tasks.TaskManager;
import net.minecraft.util.ResourceLocation;
import net.vialoadingbase.ViaLoadingBase;
import net.viamcp.ViaMCP;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class FaithD1ck {
    public static FaithD1ck INSTANCE;
    public static final String NAME = "FaithD1ck";
    public static String VERSION = "250208-BlocksMC";
    public static boolean IS_BETA = true;
    public static final ResourceLocation cape = new ResourceLocation("client/cape.png");
    public static boolean verified = false; // this is a temporary boolean
    private final EventManager eventManager;
    public static HackerDetector hackerDetector;
    private final RotationManager rotationManager;
    public final SlotSpoofManager slotSpoofManager;
    public static ModuleManager moduleManager;
    public static ConfigManager configManager;
    public static CommandManager commandManager;
    public static NotificationManager notificationManager;
    public TaskManager taskManager;
    public static boolean isInitializing = true;
    public static int delta;
    public static long lastFrame;
    public final int astolfo;

    public FaithD1ck() {
        INSTANCE = this;
       // Wrapper._debug_addDefaultCloudConstant("Beta", "1857748011");
      //  Wrapper._debug_addDefaultCloudConstant("Stable", "-1521957196");
        this.eventManager = new EventManager();
        commandManager = new CommandManager();
        commandManager.registerCommands();
        hackerDetector = new HackerDetector();
        FontManager.init();
        rotationManager = new RotationManager();
        notificationManager = new NotificationManager();
        slotSpoofManager = new SlotSpoofManager();
        taskManager = new TaskManager();
        astolfo = RandomUtils.nextInt(0, 4);
        VERSION += " (" + GitVersion.VERSION + ")";
    }

    public static void onLoaded() {
        try {
            ViaMCP.create();
            ViaMCP.INSTANCE.initAsyncSlider();
            ViaLoadingBase.getInstance().reload(ProtocolVersion.v1_12_2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        moduleManager = new ModuleManager();
        configManager = new ConfigManager();

        try {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getPackageName().startsWith("sb.faithd1ck") || info.getPackageName().startsWith("dev.jnic")) {
                    final Class<?> clazzs = info.load();
                    if (CheatModule.class.isAssignableFrom(clazzs) && clazzs != CheatModule.class) {
                        try {
                            FaithD1ck.moduleManager.modules.add((CheatModule) clazzs.newInstance());
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        FaithD1ck.moduleManager.modules.sort(Comparator.comparing(CheatModule::getName));
        FaithD1ck.moduleManager.modules.forEach(FaithD1ck.INSTANCE.getEventManager()::registerEvent);
        FaithD1ck.moduleManager.modules.forEach(module -> {
            if (!module.getValues().isEmpty())
                FaithD1ck.commandManager.registerCommand(new ModuleCommand(module, module.getValues()));
        });
        FaithD1ck.moduleManager.copiedModules = new ArrayList<>(FaithD1ck.moduleManager.modules);
        FaithD1ck.configManager.loadConfigs();
        Runtime.getRuntime().addShutdownHook(new Thread(FaithD1ck.configManager::saveConfigs));
        FaithD1ck.INSTANCE.getEventManager().registerEvent(FaithD1ck.INSTANCE.getRotationManager());
        FaithD1ck.INSTANCE.getEventManager().registerEvent(new SmoothCameraComponent());
        FaithD1ck.isInitializing = false;
    }

    public RotationManager getRotationManager() {
        return rotationManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public boolean isInitializing() {
        return isInitializing;
    }

    public static boolean getIsBeta() { return true; }

    public static boolean isDev() {
        try {
            Class.forName("sb.faithd1ck.Fai" + new StringBuilder("ht").reverse() + "d1ck").getClass();
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
