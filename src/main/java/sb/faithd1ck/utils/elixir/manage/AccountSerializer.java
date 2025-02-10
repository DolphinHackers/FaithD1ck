package sb.faithd1ck.utils.elixir.manage;

import sb.faithd1ck.utils.ClientUtils;
import sb.faithd1ck.utils.elixir.account.CrackedAccount;
import sb.faithd1ck.utils.elixir.account.MicrosoftAccount;
import sb.faithd1ck.utils.elixir.account.MinecraftAccount;
import sb.faithd1ck.utils.elixir.exception.LoginException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class AccountSerializer {
    public static final AccountSerializer INSTANCE = new AccountSerializer();

    private AccountSerializer() {
    }

    public Map<String, String> toYML(MinecraftAccount account) {
        final Map<String, String> data = new HashMap<>();
        account.toRawYML(data);
        data.put("type", account instanceof CrackedAccount ? "Offline" : "Microsoft");
        return data;
    }

    public MinecraftAccount fromYML(Map<String, String> data) {
        try {
            final String type = data.get("type");
            final MinecraftAccount account = type.equalsIgnoreCase("Microsoft") ? MicrosoftAccount.class.newInstance() : CrackedAccount.class.newInstance();
            account.fromRawYML(data);
            return account;
        } catch (Exception e) {
            ClientUtils.LOGGER.error(e);
            return null;
        }
    }

    public MinecraftAccount accountInstance(String name, String password) throws LoginException, IOException {
        MinecraftAccount minecraftAccount = null;
        if (name.startsWith("ms@")) {
            String realName = name.substring(3);
            minecraftAccount = ((CharSequence)password).length() == 0 ? MicrosoftAccount.buildFromAuthCode(realName, MicrosoftAccount.AuthMethod.MICROSOFT) : MicrosoftAccount.buildFromPassword(realName, password, MicrosoftAccount.AuthMethod.AZURE_APP);
        } else if (((CharSequence)password).length() == 0) {
            CrackedAccount crackedAccount;
            CrackedAccount it = crackedAccount = new CrackedAccount();
            it.setName(name);
            minecraftAccount = crackedAccount;
        }
        return minecraftAccount;
    }
}

