package sb.faithd1ck.command.impl;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.command.AbstractCommand;
import sb.faithd1ck.ui.notifiction.NotificationType;

public class ReloadCommand extends AbstractCommand {
    public ReloadCommand() {
        super("reload","re");
    }

    @Override
    public void execute(final String[] args) {
        FaithD1ck.configManager.reloadConfigs();
        FaithD1ck.notificationManager.pop("Config",
                "Successfully reload config", NotificationType.SUCCESS);
    }

}
