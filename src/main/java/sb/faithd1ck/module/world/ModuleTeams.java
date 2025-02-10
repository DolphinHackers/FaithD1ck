package sb.faithd1ck.module.world;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.utils.player.PlayerUtils;
import sb.faithd1ck.value.ValueBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings("unused")
public class ModuleTeams extends CheatModule {
    private static final ValueBoolean armorValue = new ValueBoolean("ArmorColor", false);
    private static final ValueBoolean colorValue = new ValueBoolean("Color", false);
    private static final ValueBoolean scoreboardValue = new ValueBoolean("ScoreboardTeam", true);


    public ModuleTeams() {
        super("Teams", Category.WORLD);
    }

    public static boolean isSameTeam(Entity entity) {
        if (!FaithD1ck.moduleManager.getModule(ModuleTeams.class).getState()) return false;
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entity;
            if (FaithD1ck.moduleManager.getModule("Teams").getState()) {
                return (armorValue.getValue() && PlayerUtils.armorTeam(entityPlayer)) ||
                        (colorValue.getValue() && PlayerUtils.colorTeam(entityPlayer)) ||
                        (scoreboardValue.getValue() && PlayerUtils.scoreTeam(entityPlayer));
            }
            return false;
        }
        return false;
    }

    public static boolean isOnSameTeam(Entity entity) {
        try {
            String self = Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText();
            String target = entity.getDisplayName().getUnformattedText();
            if (self.startsWith("\u00a7")) {
                if (!target.contains("\u00a7")) {
                    return true;
                }
                if (self.length() <= 2 || target.length() <= 2) {
                    return false;
                }
                return self.substring(0, 2).equals(target.substring(0, 2));
            }
        } catch (Throwable ignored) {}
        return false;

    }

}
