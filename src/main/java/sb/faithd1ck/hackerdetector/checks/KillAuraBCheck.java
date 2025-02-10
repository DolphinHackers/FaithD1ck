package sb.faithd1ck.hackerdetector.checks;

import sb.faithd1ck.hackerdetector.data.PlayerDataSamples;
import sb.faithd1ck.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;

public class KillAuraBCheck extends Check {

    @Override
    public String getCheatName() {
        return "KillAura";
    }

    @Override
    public String getCheatDescription() {
        return "The player can attack while eating and drinking potions";
    }

    @Override
    public String getFlagType() {
        return "B";
    }

    @Override
    public boolean canSendReport() {
        return true;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.killAuraBVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (data.hasAttacked()) {
            if (data.useItemTime > 6 && data.timeEating < 33 && data.usedItemIsConsumable && data.lastEatTime > 32) {
                return true;
            }
        }
        return false;
    }

    public static ViolationLevelTracker newVL() {
        return new ViolationLevelTracker(100, 1, 5);
    }

}
