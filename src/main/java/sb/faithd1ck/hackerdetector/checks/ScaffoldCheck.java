package sb.faithd1ck.hackerdetector.checks;

import sb.faithd1ck.hackerdetector.data.PlayerDataSamples;
import sb.faithd1ck.hackerdetector.data.SampleListD;
import sb.faithd1ck.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ScaffoldCheck extends Check {

    @Override
    public String getCheatName() {
        return "Scaffold";
    }

    @Override
    public String getCheatDescription() {
        return "The player places blocks under their feet automatically while gaining height rapidly";
    }

    @Override
    public boolean canSendReport() {
        return true;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.scaffoldVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (player.isRiding() || data.serverPosYList.size() < 4) return false;
        if (player.isSwingInProgress && player.hurtTime == 0 && data.serverPitchList.get(0) > 50f && data.getSpeedXZSq() > 9d) {
            final ItemStack itemStack = player.getHeldItem();
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                final double angleDiff = Math.abs(data.getMoveLookAngleDiff());
                final double speedXZSq = data.getSpeedXZSq();
                if (angleDiff > 165d && speedXZSq < 100d) {
                    final double speedY = data.speedYList.get(0);
                    final double avgAccelY = avgAccel(data.serverPosYList);
                    if (isAlmostZero(avgAccelY)) return false; // fix false flag in stairs
                    if (speedY < 15d && speedY > 4d && avgAccelY > -25d) {
                        return true;
                    } else if (speedY < 4d && speedY > -1d && Math.abs(speedY) > 0.005d && speedXZSq > 25d) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static ViolationLevelTracker newVL() {
        return new ViolationLevelTracker(2, 1, 6);
    }

    private static double avgAccel(SampleListD list) {
        return 50d * (list.get(3) - list.get(2) - list.get(1) + list.get(0));
    }

    private static boolean isAlmostZero(double d) {
        return Math.abs(d) < 0.001d;
    }

}
