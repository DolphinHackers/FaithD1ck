package sb.faithd1ck.module.movement;

import sb.faithd1ck.event.Handler;
import sb.faithd1ck.event.impl.*;
import sb.faithd1ck.event.impl.*;
import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.ui.clickgui.AstolfoGui;
import sb.faithd1ck.utils.player.PlayerUtils;
import sb.faithd1ck.value.ValueBoolean;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static sb.faithd1ck.utils.IMinecraft.mc;

@SuppressWarnings("unused")
public class ModuleGuiMove extends CheatModule {
    ValueBoolean nomove = new ValueBoolean("NoMoveClick", false);

    public ModuleGuiMove() {
        super("GuiMove", Category.MOVEMENT);
    }

    public static KeyBinding[] keyBindings = new KeyBinding[] {
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump
    };

    private final Handler<ClickWindowEvent> clickWindowEventHandler = event -> {
        if(nomove.getValue() && PlayerUtils.isMoving()){
            event.setCancelled(true);
        }
    };

    private final Handler<Render2DEvent> render2DEventHandler = event -> {
        if (mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof AstolfoGui || mc.currentScreen == null) {
            for (KeyBinding keyBinding : keyBindings) {
                keyBinding.setPressed(Keyboard.isKeyDown(keyBinding.getKeyCode()));
            }
        }
    };
}
