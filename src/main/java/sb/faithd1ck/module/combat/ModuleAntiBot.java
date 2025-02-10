package sb.faithd1ck.module.combat;

import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.EntityHealthUpdateEvent;
import sb.faithd1ck.event.impl.WorldLoadEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.utils.Pair;
import sb.faithd1ck.utils.player.PlayerUtils;
import sb.faithd1ck.value.ValueMultiBoolean;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import java.util.concurrent.ConcurrentLinkedDeque;

@SuppressWarnings("unused")
public class ModuleAntiBot extends CheatModule {

    private final CheckPlayer botCheck;

    public ModuleAntiBot() {
        super("AntiBot", Category.COMBAT);
        this.botCheck = PlayerUtils::hasInvalidNetInfo;
    }

    private final ValueMultiBoolean mode = new ValueMultiBoolean("Mode",
            new Pair("Watchdog", true),
            new Pair("SleepingEntity", false),
            new Pair("NoArmor", false));
    private final ConcurrentLinkedDeque<Entity> validEntities = new ConcurrentLinkedDeque<>();

    private final Handler<WorldLoadEvent> worldLoadEventHandler = event -> {
        if (mode.isEnabled("Watchdog")) {
            this.validEntities.clear();
        }
    };

    private final Handler<EntityHealthUpdateEvent> entityHealthUpdateEventHandler = event -> {
        if (mode.isEnabled("Watchdog")) {
            if (event.getEntity() instanceof EntityOtherPlayerMP)
                this.validEntities.add(event.getEntity());
        }
    };

    public boolean isBot(final Entity entity) {
        if (!(entity instanceof EntityPlayer))
            return false;
        final EntityPlayer player = (EntityPlayer) entity;
        if (mode.isEnabled("Watchdog")) {
            if (botCheck.check(player))
                return false;
        }
        if (mode.isEnabled("SleepingEntity")) {
            if (player.isPlayerSleeping())
                return true;
        }
        if (mode.isEnabled("NoArmor")) {
            return player.inventory.armorInventory[0] == null
                    && (player.inventory.armorInventory[1] == null
                            && (player.inventory.armorInventory[2] == null
                                    && (player.inventory.armorInventory[3] == null)));
        }

        return false;
    }

    @FunctionalInterface
    public interface CheckPlayer {
        boolean check(EntityPlayer player);
    }
}
