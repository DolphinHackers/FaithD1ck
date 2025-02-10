package sb.faithd1ck.ui.altmanager;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.ui.notifiction.NotificationType;
import sb.faithd1ck.utils.IMinecraft;
import sb.faithd1ck.utils.elixir.account.MicrosoftAccount;
import sb.faithd1ck.utils.elixir.account.MinecraftAccount;
import sb.faithd1ck.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class GuiList extends GuiSlot {
    private final GuiAltManager prevGui;
    private int selectedSlot = 0;
    private final CustomGuiTextField searchField;
//    private final List<MinecraftAccount> accounts = FaithD1ck.configManager.accountsConfig.getAccounts();

    public GuiList(final GuiAltManager prevGui, final CustomGuiTextField searchField) {
        super(IMinecraft.mc, prevGui.width, prevGui.height, 40, prevGui.height - 40, 40);
        this.prevGui = prevGui;
        this.searchField = searchField;
    }

    @Override
    public boolean isSelected(int id) {
        return selectedSlot == id;
    }

    public List<MinecraftAccount> getAccounts() {
        final String text = searchField.getText().toLowerCase();
        if (text.isEmpty())
            return FaithD1ck.configManager.accountsConfig.getAccounts();
        return FaithD1ck.configManager.accountsConfig.getAccounts().stream().filter(account -> account.getName().toLowerCase().contains(text)).collect(Collectors.toList());
    }

    public MinecraftAccount getSelectedAccount() {
        return getAccounts().size() > selectedSlot ? getAccounts().get(selectedSlot) : null;
    }

    @Override
    public int getSize() {
        return getAccounts().size();
    }

    @Override
    public void elementClicked(int clickedElement, boolean doubleClick, int var3, int var4) {
        selectedSlot = clickedElement;
        if (doubleClick) {
            login();
        }
    }

    public void login() {
        new Thread(() -> {
            final MinecraftAccount selectedAccount = getSelectedAccount();
            try {
                selectedAccount.update();
                mc.setSession(new Session(selectedAccount.getSession().getUsername(), selectedAccount.getSession().getUuid(), selectedAccount.getSession().getToken(), selectedAccount.getSession().getType()));
                FaithD1ck.notificationManager.pop(selectedAccount.getType(), "Logged successfully to " + selectedAccount.getName(), NotificationType.SUCCESS);
            } catch (final Exception exception) {
                FaithD1ck.notificationManager.pop("Failed to login", exception.getMessage(), NotificationType.SUCCESS);
            }
            prevGui.loginButton.enabled = true;
        }).start();

    }

    @Override
    public void drawSlot(int id, int x, int y, int var4, int var5, int var6) {
        MinecraftAccount minecraftAccount = getAccounts().get(id);
        String accountName = minecraftAccount.getName();
        if (minecraftAccount.getHeadResource() == null && minecraftAccount.getHeadImage() != null) {
            minecraftAccount.setHeadResource(new ResourceLocation(minecraftAccount.getName()));
            mc.getTextureManager().loadTexture(minecraftAccount.getHeadResource(), new DynamicTexture(minecraftAccount.getHeadImage()));
        }
        if (minecraftAccount.getHeadResource() != null) RenderUtils.drawImage(minecraftAccount.getHeadResource(), width / 2F - 105F, y + 2F, 32F, 32F);
        mc.fontRendererObj.drawString(accountName, width / 2f - 65F, y + 6F, Color.WHITE.getRGB(), true);
        if (minecraftAccount instanceof MicrosoftAccount) {
            RenderUtils.drawImage(new ResourceLocation("client/icon/microsoft.png"), width / 2F + 95F, y + 2F, 8F, 8F);
        }
    }

    @Override
    public void drawBackground() {
        RenderUtils.drawRect(0F, 0F, mc.displayWidth, mc.displayHeight, new Color(24,22,20));
    }
}