package sb.faithd1ck.command;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.utils.ClientUtils;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static sb.faithd1ck.utils.IMinecraft.mc;

public abstract class AbstractCommand {
    protected final String command;
    protected final String[] alias;

    public AbstractCommand(final String command, final String... alias) {
        this.command = command;
        this.alias = alias;
    }

    public String getCommand() {
        return command;
    }

    public String[] getAlias() {
        return alias;
    }

    public abstract void execute(final String[] args);

    public List<String> tabComplete(final String[] args) {
        return new ArrayList<>();
    }

    protected void chat(final String msg) {
        ClientUtils.displayChatMessage("§8[§c" + FaithD1ck.NAME + "§8] §b" + msg);
    }

    protected void chatSyntax(final String syntax) {
        ClientUtils.displayChatMessage("§8[§c" + FaithD1ck.NAME + "§8] §bSyntax: §7" + FaithD1ck.commandManager.getPrefix() + syntax);
    }

    protected void chatSyntax(final String[] syntaxArray) {
        ClientUtils.displayChatMessage("§8[§c" + FaithD1ck.NAME + "§8] §bSyntax:");

        for (final String syntax : syntaxArray) {
            ClientUtils.displayChatMessage("§8> §7" + FaithD1ck.commandManager.getPrefix() + command + " " + syntax.toLowerCase());
        }
    }

    protected void chatSyntaxError() {
        ClientUtils.displayChatMessage("§8[§c" + FaithD1ck.NAME + "§8] §bSyntax error");
    }

    protected void playEdit() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.anvil_use"), 1.0f));
    }
}