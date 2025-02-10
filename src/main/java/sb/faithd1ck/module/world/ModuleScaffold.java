package sb.faithd1ck.module.world;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.component.SmoothCameraComponent;
import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.*;
import sb.faithd1ck.event.impl.*;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.ui.font.FontManager;
import sb.faithd1ck.utils.BlockUtil;
import sb.faithd1ck.utils.ScaffoldUtils;
import sb.faithd1ck.utils.math.MathUtils;
import sb.faithd1ck.utils.player.PlayerUtils;
import sb.faithd1ck.utils.player.Rotation;
import sb.faithd1ck.utils.player.RotationUtils;
import sb.faithd1ck.utils.render.RenderUtils;
import sb.faithd1ck.value.ValueBoolean;
import sb.faithd1ck.value.ValueMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;

import java.awt.*;
import java.util.List;
import java.util.*;

import static sb.faithd1ck.utils.IMinecraft.mc;
import static sb.faithd1ck.utils.player.PlayerUtils.getSpeed;
import static sb.faithd1ck.utils.player.PlayerUtils.isMoving;

@SuppressWarnings("unused")
public class ModuleScaffold extends CheatModule {
    private final ValueMode modeValue = new ValueMode("Mode", new String[]{"Normal", "WatchdogJump", "BlocksMCJump"}, "Normal");
    private final ValueBoolean swing = new ValueBoolean("Swing", true);
    private final ValueBoolean towerMove = new ValueBoolean("Tower", false)/*.visible(() -> false)*/;
    private final ValueBoolean eagle = new ValueBoolean("Eagle", false);
    private final ValueBoolean telly = new ValueBoolean("Telly", true);
    private final ValueBoolean moveFix = new ValueBoolean("MoveFix", false);
    private final ValueBoolean bugFlyValue = new ValueBoolean("BugFly", false).visible(FaithD1ck::getIsBeta);
    private final ValueBoolean swap = new ValueBoolean("Swap", true);
    private final ValueBoolean keepYValue = new ValueBoolean("Keep Y", false);
    private final ValueBoolean upValue = new ValueBoolean("Up", false).visible(() -> (telly.getValue() && !keepYValue.getValue()));
    private final ValueBoolean smoothCamera = new ValueBoolean("SmoothCamera", false);
    private final ValueBoolean test = new ValueBoolean("Test", false).visible(FaithD1ck::getIsBeta);
    private float y;
    private int idkTick = 0, towerTick = 0, slot = 0;
    private boolean onGround = false;
    private BlockPos data;
    private EnumFacing enumFacing;
    private boolean up, keepY, canTellyPlace;
    private static final List<Block> invalidBlocks = Arrays.asList(Blocks.wall_banner, Blocks.waterlily, Blocks.ender_chest, Blocks.standing_banner, Blocks.dropper, Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer, Blocks.torch, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web, Blocks.tnt);
    private double keepYCoord;
    private double lastOnGroundPosY;
    private int lastSlot;
    private boolean flyFlag = false;
    private LinkedList<List<Packet<?>>> packets = new LinkedList<>();
    private int c08PacketSize = 0;
    private boolean packetHandlerFlag = true;
    private float[] lastRotation = null;
    private boolean placeFlag = false;
    private boolean lastKeepYMode = false;
    private boolean lastJumpMode = false;
    private int placedAfterTower = 0;
    private boolean wasTowering;
    private int slowTicks;
    private int ticks;
    private int tickCounter;
    private float angle;
    private double targetZ;
    private boolean targetCalculated;
    private int ticks2;
    private int lastY;
    private float keepYaw;
    private double HI;
    private boolean firstJump;
    private double jumpStart;

    public ModuleScaffold() {
        super("Scaffold", Category.WORLD);
        /*towerMove.setValue(false);*/
    }

    public void sendPacketHook(Packet packet) {
        if (packet instanceof C09PacketHeldItemChange) {
            if (((C09PacketHeldItemChange)packet).getSlotId() == lastSlot) {
                return;
            }
            mc.getNetHandler().addToSendQueue(packet);
            lastSlot = ((C09PacketHeldItemChange)packet).getSlotId();
        }
    }

    public double getYLevel() {
        if (!keepY) {
            return mc.thePlayer.posY - 1.0;
        }

        return !isMoving() ? mc.thePlayer.posY - 1.0 : keepYCoord;
    }

    private Rotation getRotations(BlockPos blockPos, EnumFacing enumFacing) {
        double d = (double) blockPos.getX() + 0.5 - mc.thePlayer.posX + (double) enumFacing.getFrontOffsetX() * 0.25;
        double d2 = (double) blockPos.getZ() + 0.5 - mc.thePlayer.posZ + (double) enumFacing.getFrontOffsetZ() * 0.25;
        double d3 = mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight() - blockPos.getY() - (double) enumFacing.getFrontOffsetY() * 0.25;
        double d4 = MathHelper.sqrt_double(d * d + d2 * d2);
        float f = (float) (Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float) (Math.atan2(d3, d4) * 180.0 / Math.PI);
        return new Rotation(MathHelper.wrapAngleTo180_float(f), f2);
    }

    public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
        double x = (double) pos.getX() + 0.5;
        double y = (double) pos.getY() + 0.5;
        double z = (double) pos.getZ() + 0.5;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += MathUtils.getRandomInRange(0.3, -0.3);
            z += MathUtils.getRandomInRange(0.3, -0.3);
        } else {
            y += 0.08;
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtils.getRandomInRange(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += MathUtils.getRandomInRange(0.3, -0.3);
        }
        return new Vec3(x, y, z);
    }

    private void sendTick(List<Packet<?>> tick) {
        if (mc.getNetHandler() != null) {
            tick.forEach(packet -> {
                if (packet instanceof C08PacketPlayerBlockPlacement) {
                    c08PacketSize -= 1;
                }
                mc.getNetHandler().addToSendQueue(packet, true);
            });
        }
    }

    @Override
    public void onEnable() {
        idkTick = 5;
        placedAfterTower = 0;

        if (mc.thePlayer == null) return;

        canTellyPlace = telly.getValue();
        mc.thePlayer.setSprinting(!canTellyPlace);
        mc.gameSettings.keyBindSprint.pressed = !canTellyPlace;
        canTellyPlace = false;
        this.data = null;
        this.slot = -1;
        keepY = keepYValue.getValue();
        up = upValue.getValue();
        lastSlot = mc.thePlayer.inventory.currentItem;
        flyFlag = false;
        c08PacketSize = 0;
        packetHandlerFlag = true;
        lastOnGroundPosY = mc.thePlayer.posY;
        targetCalculated = false;
        keepYaw = PlayerUtils.getMoveYaw(mc.thePlayer.rotationYaw) - 180f;
        firstJump = true;
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) return;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        if (slot != mc.thePlayer.inventory.currentItem)
            sendPacketHook(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        if (bugFlyValue.getValue()) {
            packets.forEach(this::sendTick);
            packets.clear();
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem + 1), true);
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem), true);
        }
    }

    private final Handler<MotionEvent> motionEventHandler = event -> {
        if (this.idkTick > 0) {
            --this.idkTick;
        }

        if (event.isPre()) {
            if (mc.thePlayer.onGround) {
                lastOnGroundPosY = mc.thePlayer.posY;
            }
            if ((mc.thePlayer.posY < this.HI || mc.thePlayer.onGround) || !PlayerUtils.isMoving()) {
                this.HI = Math.floor(mc.thePlayer.posY);
            }
            if (smoothCamera.getValue() && !mc.gameSettings.keyBindJump.pressed) {
                SmoothCameraComponent.setY(lastOnGroundPosY + ((telly.getValue() || modeValue.getValue().contains("KeepY")) ? 1.0 : 0.0));
            }
            if (eagle.getValue()) {
                if (getBlockUnderPlayer(mc.thePlayer) instanceof BlockAir) {
                    if (mc.thePlayer.onGround) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                    }
                } else if (mc.thePlayer.onGround) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                }
            }

            if (modeValue.is("BlocksMCJump")) {
                mc.gameSettings.keyBindSprint.pressed = true;
                mc.thePlayer.setSprinting(true);
            }

            boolean bl = mc.thePlayer.posY == this.HI;

            if (this.getBlockCount() <= 5 && getAllBlockCount() > 5) {
                int spoofSlot = this.getBestSpoofSlot();
                this.getBlock(spoofSlot);
            }
            final ItemStack itemStack = switchToBlock();
            if (itemStack == null) return;

            if (mc.gameSettings.keyBindJump.pressed && isMoving()) {
                if (mc.thePlayer.onGround) {
                    onGround = true;
                }
            } else {
                onGround = false;
            }
        }

    };

    private final Handler<JumpEvent> jumpEventHandler = event -> {
        if (mc.gameSettings.keyBindJump.pressed && towerMove.getValue() && isMoving()) {
            event.setCancelled(true);
        }
        jumpStart = mc.thePlayer.posY;
    };

    private final Handler<StrafeEvent> strafeEventHandler = event -> {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if ((up || keepY) && mc.thePlayer.onGround && isMoving() && !mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.jump();
            PlayerUtils.strafe(getSpeed() * .99999);
        }
    };

    private final Handler<TickUpdateEvent> tickUpdateEventHandler = event -> {
        if (mc.thePlayer == null) return;
        if (getBlockSlot() < 0) return;
        if (!telly.getValue()) {
            canTellyPlace = true;
        }
        if (bugFlyValue.getValue()) {
            packets.add(new ArrayList<>());

            if (c08PacketSize >= 12 && !flyFlag) {
                flyFlag = true;
                while (c08PacketSize > 2) {
                    poll();
                }
            }

            while (flyFlag && c08PacketSize > 2) {
                poll();
            }
        }

        if (towerMove.getValue()) {
            if (!mc.gameSettings.keyBindJump.isKeyDown() || mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() instanceof BlockAir || isMoving()) {
                return;
            }
            if (mc.thePlayer.onGround)
                mc.thePlayer.motionY = 0.42F;
            this.data = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
            enumFacing = EnumFacing.UP;
        }
    };

    private void poll() {
        if (packets.isEmpty()) return;
        this.sendTick(packets.getFirst());
        packets.removeFirst();
    }

    private ItemStack switchToBlock() {
        int blockSlot;
        ItemStack itemStack;
        blockSlot = getBlockSlot();

        if (blockSlot < 35 || blockSlot > 44)
            return null;

        sendPacketHook(new C09PacketHeldItemChange(blockSlot - 36));
        itemStack = mc.thePlayer.inventoryContainer.getSlot(blockSlot).getStack();
        return itemStack;
    }

    private final Handler<BlockPlaceEvent> blockPlaceEventHandler = event -> {
        if (!telly.getValue()) {
            mc.gameSettings.keyBindSprint.pressed = false;
        }
        if (mc.thePlayer == null) return;

        final ItemStack itemStack = switchToBlock();
        if (itemStack == null) return;

        place(itemStack);
        mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);
    };

    private final Handler<PacketEvent> packetHandler = event -> {
        if (event.getType() == PacketEvent.Type.SEND) {
            final Packet<?> packet = event.getPacket();
            if (packet instanceof C09PacketHeldItemChange) {
                final C09PacketHeldItemChange packetHeldItemChange = (C09PacketHeldItemChange) packet;
                slot = packetHeldItemChange.getSlotId();
            }
            
            if (towerMove.getValue()) {
                if (!mc.gameSettings.keyBindJump.isKeyDown() || isMoving()) return;
                if (mc.thePlayer.motionY > -0.0784000015258789 && event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                    final C08PacketPlayerBlockPlacement wrapper = ((C08PacketPlayerBlockPlacement) event.getPacket());

                    if (wrapper.getPosition().equals(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.4, mc.thePlayer.posZ))) {
                        mc.thePlayer.motionY = -0.0784000015258789;
                    }
                }
            }

            if (bugFlyValue.getValue()) {
                if (packet instanceof C08PacketPlayerBlockPlacement) {
                    c08PacketSize += 1;
                }
                mc.addScheduledTask(() -> {
                    if (packets.isEmpty()) {
                        packets.add(new LinkedList<Packet<?>>());
                    }
                    packets.getLast().add(packet);
                });
                event.setCancelled(true);
            }
        }
    };
    public static boolean canPlaceBlock(Block block) {
        return block.isFullCube() && !invalidBlocks.contains(block);
    }

    public static int findAutoBlockBlock() {
        for(int i = 36; i < 45; i++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if(itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                final ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                final Block block = itemBlock.getBlock();

                if (block.isFullCube() && !invalidBlocks.contains(block))
                    return i;
            }
        }

        for(int i = 36; i < 45; i++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if(itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                final ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                final Block block = itemBlock.getBlock();

                if (!invalidBlocks.contains(block))
                    return i;
            }
        }

        return -1;
    }
    private ItemStack barrier = new ItemStack(Item.getItemById(166), 0, 0);
    private final Handler<Render2DEvent> render2DEventHandler = event -> {
        GlStateManager.pushMatrix();
        final String info = "Blocks: " + getBlockCount();
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        float width = scaledResolution.getScaledWidth();
        float height = scaledResolution.getScaledHeight();
        int slot = getBlockSlot();
        ItemStack stack = barrier;
        if (slot != -1) {
            if (mc.thePlayer.inventory.getCurrentItem() != null) {
                Item handItem = mc.thePlayer.inventory.getCurrentItem().getItem();
                if (handItem instanceof ItemBlock && canPlaceBlock(((ItemBlock) handItem).getBlock())) {
                    stack = mc.thePlayer.inventory.getCurrentItem();
                }
            }
            if (stack == barrier) {
                stack = mc.thePlayer.inventory.getStackInSlot(findAutoBlockBlock() - 36);
                if (stack == null) {
                    stack = barrier;
                }
            }
        }
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemIntoGUI(stack, (int) ((int) width / 2 - FontManager.sf20.getStringWidth(info)) + 17
                , (int) (height * 0.6 - FontManager.sf20.getHeight() * 0.5));
        RenderHelper.disableStandardItemLighting();
        FontManager.sf20.drawCenteredString(info, width / 2f + 15, height * 0.6f + 0.5f, Color.WHITE.getRGB());
        GlStateManager.popMatrix();
    };

    private final Handler<Render3DEvent> render3DEventHandler = event -> {
        if (data == null) return;
        RenderUtils.drawBlockBox(data, new Color(255, 0, 0, 50), false);
    };

    private float yaw;
    private float pitch;
    private final Handler<UpdateEvent> updateEventHandler = event -> {
        final ItemStack itemStack = switchToBlock();
        if (itemStack == null) return;

        if (telly.getValue()) {
            up = mc.gameSettings.keyBindJump.pressed;
            keepY = !up;
        } else {
            up = upValue.getValue();
            keepY = keepYValue.getValue() || modeValue.is("BlocksMCJump");
        }
        if (mc.thePlayer.onGround || mc.gameSettings.keyBindJump.pressed || mc.thePlayer.posY < keepYCoord + 1) {
            keepYCoord = Math.floor(mc.thePlayer.posY - 1.0);
        }

        if (getBlockSlot() < 0) {
            return;
        }

        this.findBlock();

        if (telly.getValue()) {
            mc.gameSettings.keyBindSprint.pressed = true;
            if (canTellyPlace && isMoving()) {
                mc.thePlayer.setSprinting(false);
            } else {
                mc.thePlayer.setSprinting(true);
            }
            canTellyPlace = mc.thePlayer.offGroundTicks >= (up ? (mc.thePlayer.ticksExisted % 16 == 0 ? 2 : 1) : 2.9);
        }
        if (data != null) enumFacing = getPlaceSide(data);
        if (test.getValue()) mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement());
        if (!modeValue.is("Normal") && data != null) {
            try {
                Rotation _rot = ScaffoldUtils.faceBlock(data, enumFacing);
                float[] rotations = lastRotation = new float[]{_rot.getYaw(), _rot.getPitch()};
                FaithD1ck.INSTANCE.getRotationManager().setRotation(new Rotation(rotations[0], rotations[1]), 180, moveFix.getValue());
            } catch (Exception e) {
                if (lastRotation != null) {
                    FaithD1ck.INSTANCE.getRotationManager().setRotation(new Rotation(lastRotation[0], lastRotation[1]), 180, moveFix.getValue());
                } else {
                    e.printStackTrace();
                }
            }
        }
        if (!canTellyPlace && modeValue.is("Normal") && test.getValue()) {
            FaithD1ck.INSTANCE.getRotationManager().setRotation(new Rotation(yaw + 5f, pitch + 20f), 180, moveFix.getValue());
            return;
        }
        if (canTellyPlace && data != null && modeValue.is("Normal")) {
            float[] rot = RotationUtils.getRotationBlock(data);
            yaw = rot[0];
            pitch = rot[1];
            FaithD1ck.INSTANCE.getRotationManager().setRotation(new Rotation(yaw, pitch), 180, moveFix.getValue());
        }
    };

    private final Handler<SafeWalkEvent> safewalkHandler = event -> event.setSafe(true);

    private void place(final ItemStack block) {
        if (!canTellyPlace) return;
        if (data != null) {
            if (enumFacing == null) return;
            MovingObjectPosition movingObjectPosition = mc.objectMouseOver;
            if (movingObjectPosition == null || !movingObjectPosition.getBlockPos().equals(this.data)) return;
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, block, this.data, enumFacing, movingObjectPosition.hitVec)) {
                if (swing.getValue()) {
                    mc.thePlayer.swingItem();
                } else {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                }
            }

        }

    }


    private void findBlock() {
        if (isMoving() && keepY) {
            boolean shouldGoDown = false;
            final BlockPos blockPosition = new BlockPos(mc.thePlayer.posX, getYLevel(), mc.thePlayer.posZ);

            if ((BlockUtil.isValidBock(blockPosition) || search(blockPosition, !shouldGoDown))) return;

            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    if (search(blockPosition.add(x, 0, z), !shouldGoDown)) return;
        } else {
            this.data = getBlockPos();
        }

    }

    private double calcStepSize(double range) {
        double accuracy = 6;
        accuracy += accuracy % 2; // If it is set to uneven it changes it to even. Fixes a bug
        return Math.max(range / accuracy, 0.01);
    }

    private boolean search(final BlockPos blockPosition, final boolean checks) {
        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        ScaffoldUtils.PlaceRotation placeRotation = null;

        double xzRV = 0.5;
        double yRV = 0.5;
        double xzSSV = calcStepSize(xzRV);
        double ySSV = calcStepSize(xzRV);

        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = blockPosition.offset(side);

            if (!BlockUtil.isValidBock(neighbor)) continue;

            final Vec3 dirVec = new Vec3(side.getDirectionVec());
            for (double xSearch = 0.5 - xzRV / 2; xSearch <= 0.5 + xzRV / 2; xSearch += xzSSV) {
                for (double ySearch = 0.5 - yRV / 2; ySearch <= 0.5 + yRV / 2; ySearch += ySSV) {
                    for (double zSearch = 0.5 - xzRV / 2; zSearch <= 0.5 + xzRV / 2; zSearch += xzSSV) {
                        final Vec3 posVec = new Vec3(blockPosition).addVector(xSearch, ySearch, zSearch);
                        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        final Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));

                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18.0 || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null))
                            continue;

                        // face block
                        final Rotation rotation = getRotation(hitVec, eyesPos);

                        final Vec3 vecRot = RotationUtils.getVectorForRotation(rotation);
                        final Vec3 rotationVector = new Vec3(vecRot.xCoord, vecRot.yCoord, vecRot.zCoord);
                        final Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4, rotationVector.yCoord * 4, rotationVector.zCoord * 4);
                        final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);

                        if (!(obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && obj.getBlockPos().equals(neighbor)))
                            continue;

                        if (placeRotation == null || FaithD1ck.INSTANCE.getRotationManager().getRotationDifference(rotation) < FaithD1ck.INSTANCE.getRotationManager().getRotationDifference(placeRotation.getRotation()))
                            placeRotation = new ScaffoldUtils.PlaceRotation(new ScaffoldUtils.PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                    }
                }
            }
        }

        if (placeRotation == null) return false;

        data = placeRotation.getPlaceInfo().getBlockPos();
        enumFacing = getPlaceSide(data);

        return true;
    }

    private Rotation getRotation(Vec3 hitVec, Vec3 eyesPos) {
        final double diffX = hitVec.xCoord - eyesPos.xCoord;
        final double diffY = hitVec.yCoord - eyesPos.yCoord;
        final double diffZ = hitVec.zCoord - eyesPos.zCoord;

        final double diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

        return new Rotation(MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F), MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ))));
    }


    private EnumFacing getPlaceSide(BlockPos blockPos) {
        ArrayList<Vec3> positions = new ArrayList<>();
        HashMap<Vec3, EnumFacing> hashMap = new HashMap<>();
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        BlockPos bp;
        Vec3 vec3;
        if (BlockUtil.isAirBlock(blockPos.add(0, 1, 0)) && !blockPos.add(0, 1, 0).equals(playerPos) && !mc.thePlayer.onGround &&
                (!keepY || (mc.gameSettings.keyBindJump.pressed && playerPos.getX() == blockPos.getX() && playerPos.getZ() == blockPos.getZ()))) {
            bp = blockPos.add(0, 1, 0);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.UP);
        }

        if (BlockUtil.isAirBlock(blockPos.add(1, 0, 0)) && !blockPos.add(1, 0, 0).equals(playerPos)) {
            bp = blockPos.add(1, 0, 0);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.EAST);
        }

        if (BlockUtil.isAirBlock(blockPos.add(-1, 0, 0)) && !blockPos.add(-1, 0, 0).equals(playerPos)) {
            bp = blockPos.add(-1, 0, 0);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.WEST);
        }

        if (BlockUtil.isAirBlock(blockPos.add(0, 0, 1)) && !blockPos.add(0, 0, 1).equals(playerPos)) {
            bp = blockPos.add(0, 0, 1);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.SOUTH);
        }

        if (BlockUtil.isAirBlock(blockPos.add(0, 0, -1)) && !blockPos.add(0, 0, -1).equals(playerPos)) {
            bp = blockPos.add(0, 0, -1);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.NORTH);
        }

        positions.sort(Comparator.comparingDouble((vec3x) -> mc.thePlayer.getDistance(vec3x.xCoord, vec3x.yCoord, vec3x.zCoord)));
        if (!positions.isEmpty()) {
            vec3 = this.getBestHitFeet(this.data);
            if (mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord) >= mc.thePlayer.getDistance(positions.get(0).xCoord, positions.get(0).yCoord, positions.get(0).zCoord)) {
                return hashMap.get(positions.get(0));
            }
        }

        return null;
    }

    private Vec3 getBestHitFeet(BlockPos blockPos) {
        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        double ex = MathHelper.clamp_double(mc.thePlayer.posX, blockPos.getX(), (double) blockPos.getX() + block.getBlockBoundsMaxX());
        double ey = MathHelper.clamp_double(keepY ? getYLevel() : mc.thePlayer.posY, blockPos.getY(), (double) blockPos.getY() + block.getBlockBoundsMaxY());
        double ez = MathHelper.clamp_double(mc.thePlayer.posZ, blockPos.getZ(), (double) blockPos.getZ() + block.getBlockBoundsMaxZ());
        return new Vec3(ex, ey, ez);
    }

    private BlockPos getBlockPos() {
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, getYLevel(), mc.thePlayer.posZ);
        ArrayList<Vec3> positions = new ArrayList<>();
        HashMap<Vec3, BlockPos> hashMap = new HashMap<>();

        for (int x = playerPos.getX() - 5; x <= playerPos.getX() + 5; ++x) {
            for (int y = playerPos.getY() - 5; y <= playerPos.getY(); ++y) {
                for (int z = playerPos.getZ() - 5; z <= playerPos.getZ() + 5; ++z) {
                    if (BlockUtil.isValidBock(new BlockPos(x, y, z))) {
                        BlockPos blockPos = new BlockPos(x, y, z);
                        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                        Vec3 vec3 = getVec3(blockPos, block);
                        positions.add(vec3);
                        hashMap.put(vec3, blockPos);
                    }
                }
            }
        }

        if (!positions.isEmpty()) {
            positions.sort(Comparator.comparingDouble(this::getBestBlock));
            return hashMap.get(positions.get(0));
        } else {
            return null;
        }
    }

    private Vec3 getVec3(BlockPos blockPos, Block block) {
        double ex = MathHelper.clamp_double(mc.thePlayer.posX, blockPos.getX(), (double) blockPos.getX() + block.getBlockBoundsMaxX());
        double ey = MathHelper.clamp_double(keepY ? getYLevel() : mc.thePlayer.posY, blockPos.getY(), (double) blockPos.getY() + block.getBlockBoundsMaxY());
        double ez = MathHelper.clamp_double(mc.thePlayer.posZ, blockPos.getZ(), (double) blockPos.getZ() + block.getBlockBoundsMaxZ());
        return new Vec3(ex, ey, ez);
    }

    private double getBestBlock(Vec3 vec3) {
        MovingObjectPosition movingObjectPosition = mc.theWorld.rayTraceBlocks(new Vec3(mc.thePlayer.posX, getYLevel(), mc.thePlayer.posZ), vec3, false, false, false);
        BlockPos blockPos = new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord);
        if (movingObjectPosition != null && (movingObjectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || !movingObjectPosition.getBlockPos().equals(blockPos))) {
            return 100000;
        }
        double cost = mc.thePlayer.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord);
        if (cost > 4.5) return 100000;
        return cost;
    }

    public int getBlockSlot() {
        int maxStackSlot = -1;
        int maxStack = -1;
        for (int i = 36; i < 45; i++) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                if (itemStack.stackSize > maxStack) {
                    final ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                    final Block block = itemBlock.getBlock();

                    if (block.isFullCube() && !invalidBlocks.contains(block)) {
                        maxStackSlot = i;
                        maxStack = itemStack.stackSize;
                    }
                }
            }
        }
        
        if (maxStackSlot != -1) return maxStackSlot;

        for (int i = 36; i < 45; i++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                if (itemStack.stackSize > maxStack) {
                    final ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                    final Block block = itemBlock.getBlock();

                    if (!invalidBlocks.contains(block)) {
                        maxStackSlot = i;
                        maxStack = itemStack.stackSize;
                    }
                }
            }
        }

        return maxStackSlot;
    }

    public int getAllBlockCount() {
        int count = 0;
        int i = 0;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = stack.getItem();
                if (stack.getItem() instanceof ItemBlock && this.isValid(item) && stack.stackSize >= 3) {
                    count += stack.stackSize - 2;
                }
            }
            ++i;
        }
        return count;
    }

    public int getBlockCount() {
        int count = 0;
        int i = 36;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = stack.getItem();
                if (stack.getItem() instanceof ItemBlock && this.isValid(item)) {
                    count += stack.stackSize;
                }
            }
            ++i;
        }
        return count;
    }

    private boolean isValid(final Item item) {
        return item instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock) (item)).getBlock());
    }

    private void getBlock(int switchSlot) {
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
                ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (itemStack.getItem() instanceof ItemBlock) {
                    final ItemBlock block = (ItemBlock) itemStack.getItem();
                    if (isValid(block) && swap.getValue()) {
                        if (36 + switchSlot != i) {
                            mc.thePlayer.swap(i, switchSlot);
                        }
                        break;
                    }
                }
            }
        }

    }

    int getBestSpoofSlot() {
        int spoofSlot = 5;
        for (int i = 36; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                spoofSlot = i - 36;
                break;
            }
        }

        return spoofSlot;
    }

    public Block getBlockUnderPlayer(final EntityPlayer player) {
        return getBlock(new BlockPos(player.posX, player.posY - 1.0, player.posZ));
    }

    public Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }
}