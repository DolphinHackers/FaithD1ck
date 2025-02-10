package sb.faithd1ck.module.movement;

import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.MotionEvent;
import sb.faithd1ck.event.impl.WorldLoadEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.utils.BlockUtil;
import sb.faithd1ck.value.ValueMode;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.Map;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleNoFluid extends CheatModule {
    private final ValueMode modeValue = new ValueMode("Mode", new String[]{"Vanilla", "Grim"}, "Grim");
    public static boolean shouldCancelWater;
    public ModuleNoFluid() {
        super("NoFluid", Category.MOVEMENT);
    }

    @Override
    public String getSuffix() {
        return modeValue.getValue();
    }

    @Override
    public void onDisable() {
        shouldCancelWater = false;
    }

    private final Handler<WorldLoadEvent> worldLoadEventHandler = event -> {
        shouldCancelWater = false;
    };

    private final Handler<MotionEvent> motionEventHandler = event -> {
        if (mc.thePlayer == null)
            return;

        if (event.isPost()) return;

        shouldCancelWater = false;

        Map<BlockPos, Block> searchBlock = BlockUtil.searchBlocks(2);

        for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
            boolean checkBlock = mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.water
                    || mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.flowing_water
                    || mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.lava
                    || mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.flowing_lava;
            if (checkBlock) {
                shouldCancelWater = true;
                if (modeValue.getValue().equals("Grim") && shouldCancelWater) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, block.getKey(), EnumFacing.DOWN));
                    mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), EnumFacing.DOWN));
                }
            }
        }
    };
}

