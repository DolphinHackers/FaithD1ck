package sb.faithd1ck.module.render;

import sb.faithd1ck.module.Category;
import sb.faithd1ck.module.CheatModule;
import sb.faithd1ck.ui.clickgui.AstolfoGui;

import static sb.faithd1ck.utils.IMinecraft.mc;

import org.lwjgl.input.Keyboard;

@SuppressWarnings("unused")
public class ModuleClickGui extends CheatModule {
    private final AstolfoGui astolfoGui = new AstolfoGui();
    public ModuleClickGui() {
        super("ClickGui", Category.RENDER, Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(astolfoGui);
        toggle();
    }

}
