package sb.faithd1ck.module.player;

import cn.hutool.http.HttpUtil;
import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.PacketEvent;
import sb.faithd1ck.event.impl.WorldEvent;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.utils.ClientUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumChatFormatting;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import static sb.faithd1ck.utils.IMinecraft.mc;

public class ModuleStaffDetector extends CheatModule {
    public static CopyOnWriteArrayList<String> staff = new CopyOnWriteArrayList<>();
    private static Thread staffListDownloadThread;
    private CopyOnWriteArrayList<String> warnedPlayers = new CopyOnWriteArrayList<>();

    @Override
    public String getSuffix() {
        if (staff.isEmpty()) return "Loading...";
        return staff.size() + " staffs";
    }

    public ModuleStaffDetector() {
        super("StaffDetector", Category.PLAYER);
    }
    
    public final Handler<WorldEvent> worldEventHandler = event -> {
        if (staffListDownloadThread != null && staffListDownloadThread.isAlive()) return;
        staffListDownloadThread = new Thread(this::downloadStaffList);
        staffListDownloadThread.setDaemon(true);
        staffListDownloadThread.setName("StaffListDownloadThread");
        staffListDownloadThread.start();
        warnedPlayers.clear();
    };
    
    public void downloadStaffList() {
        try {
            if (!staff.isEmpty()) return;
            String staffList = HttpUtil.downloadString("https://cloud.liquidbounce.net/LiquidBounce/staffs/blocksmc.com", StandardCharsets.UTF_8);
            String[] staffArray = staffList.split("\n");
            staff.addAll(Arrays.asList(staffArray));
        } catch (Exception e) {
            e.printStackTrace();
            ClientUtils.displayChatMessage("§cFailed to download staff list, try re-downloading...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            staffListDownloadThread = new Thread(this::downloadStaffList);
            staffListDownloadThread.setDaemon(true);
            staffListDownloadThread.setName("StaffListDownloadThread");
            staffListDownloadThread.start();
        }
    }
    
    public final Handler<PacketEvent> packetEventHandler = event -> {
        if (event.getType() != PacketEvent.Type.RECEIVE) return;
        Packet packet = event.getPacket();
        if (packet instanceof S3EPacketTeams) {
            String teamName = ((S3EPacketTeams) packet).getName();
            if (teamName != null && teamName.equalsIgnoreCase("Z_Spectator")) {
                ((S3EPacketTeams) packet).getPlayers().forEach(player -> {
                    if (staff.contains(player)) {
                        ClientUtils.displayChatMessage("§c[STAFF] §d" + player + " §3is using the spectator menu §e(compass/left)");
                    } else {
                        ClientUtils.displayChatMessage("§d" + player + " §3is using the spectator menu §e(compass/left)");
                    }
                });
            } else {
                ((S3EPacketTeams) packet).getPlayers().forEach(player -> {
                    if (staff.contains(player)) {
                        ClientUtils.displayChatMessage("§c[STAFF] §d" + player + " §3set to team " + teamName);
                    }
                });
            }
        }
        if (packet instanceof S38PacketPlayerListItem) {
            if (((S38PacketPlayerListItem) packet).getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                ((S38PacketPlayerListItem) packet).getEntries().forEach(entry -> {
                    if (staff.contains(entry.getProfile().getName())) {
                        ClientUtils.displayChatMessage("§c[STAFF] §d" + entry.getProfile().getName() + " §3joined.");
                        warnedPlayers.add(entry.getProfile().getName());
                    }
                });
            }
            if (((S38PacketPlayerListItem) packet).getAction() == S38PacketPlayerListItem.Action.UPDATE_LATENCY) {
                int originPlayerSize = mc.getNetHandler().getPlayerInfoMap().size();
                if (originPlayerSize != ((S38PacketPlayerListItem) packet).getEntries().size()) {
                    ClientUtils.displayChatMessage("§cA player might be vanished.");
                    ((S38PacketPlayerListItem) packet).getEntries().forEach(entry -> {
                        if (mc.getNetHandler().getPlayerInfo(entry.getProfile().getId()) == null) {
                            if (staff.contains(entry.getProfile().getName())) {
                                ClientUtils.displayChatMessage("§c[STAFF] §d" + entry.getProfile().getName() + " §3is viewing this game.");
                                warnedPlayers.add(entry.getProfile().getName());
                            } else if (entry.getProfile().getName() != null) {
                                ClientUtils.displayChatMessage("§c" + entry.getProfile().getName() + " §3is vanished.");
                            }
                        }
                    });
                } else {
                    ClientUtils.displayChatMessage(EnumChatFormatting.GREEN + "All players are visible.");
                }
            }
        }
        int entityId = Integer.MAX_VALUE;
        Entity targetEntity = null;
        if (packet instanceof S01PacketJoinGame) {
            entityId = ((S01PacketJoinGame) packet).getEntityId();
        } else if (packet instanceof S0CPacketSpawnPlayer) {
            entityId = ((S0CPacketSpawnPlayer) packet).getEntityID();
        } else if (packet instanceof S18PacketEntityTeleport) {
            entityId = ((S18PacketEntityTeleport) packet).getEntityId();
        } else if (packet instanceof S1CPacketEntityMetadata) {
            entityId = ((S1CPacketEntityMetadata) packet).getEntityId();
        } else if (packet instanceof S1DPacketEntityEffect) {
            entityId = ((S1DPacketEntityEffect) packet).getEntityId();
        } else if (packet instanceof S1EPacketRemoveEntityEffect) {
            entityId = ((S1EPacketRemoveEntityEffect) packet).getEntityId();
        } else if (packet instanceof S19PacketEntityHeadLook) {
            targetEntity = ((S19PacketEntityHeadLook) packet).getEntity(mc.theWorld);
        } else if (packet instanceof S19PacketEntityStatus) {
            entityId = ((S19PacketEntityStatus) packet).getEntityId();
        } else if (packet instanceof S49PacketUpdateEntityNBT) {
            targetEntity = ((S49PacketUpdateEntityNBT) packet).getEntity(mc.theWorld);
        } else if (packet instanceof S1BPacketEntityAttach) {
            entityId = ((S1BPacketEntityAttach) packet).getEntityId();
        }
        if (targetEntity == null && entityId != Integer.MAX_VALUE) {
            targetEntity = mc.theWorld.getEntityByID(entityId);
        }
        if (!(targetEntity instanceof EntityPlayer)) return;
        handlePlayer((EntityPlayer) targetEntity);
    };
    
    public void handlePlayer(EntityPlayer player) {
        if (staff.contains(player.getName())) {
            if (!warnedPlayers.contains(player.getName())) {
                ClientUtils.displayChatMessage("§c[STAFF] §d" + player.getName() + " §3detected.");
                warnedPlayers.add(player.getName());
            }
        }
    }
}
