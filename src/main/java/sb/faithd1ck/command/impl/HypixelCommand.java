package sb.faithd1ck.command.impl;

/*public class HypixelCommand extends AbstractCommand {
    public HypixelCommand() {
        super("hypixel");
    }


    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            if ("query".equalsIgnoreCase(args[1])) {
                if (!FaithD1ck.moduleManager.getModule(ModuleHypixelUtils.class).getState()) {
                    chat("You need to turn on module \"HypixelUtils\" first.");
                    return;
                }
                new Thread(() -> {
                    try {
                        if (!IRC.connected) {
                            return;
                        }
                        chat(IRC.getPlayerInfoByName(args[2]));
                    } catch (InterruptedException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
                        chat("Failed to fetch player info.");
                    }
                }).start();
                return;
            }
        }
        chatSyntax("hypixel query <playerName>");
    }
}*/
