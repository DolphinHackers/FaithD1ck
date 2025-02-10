package sb.faithd1ck.command.impl;

import sb.faithd1ck.FaithD1ck;
import sb.faithd1ck.command.AbstractCommand;
import sb.faithd1ck.module.CheatModule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HideCommand extends AbstractCommand {
    
    public HideCommand() {
        super("hide", "hidden");
    }

    /**
     * Execute commands with provided [args]
     */
    @Override
    public void execute(final String[] args) {
        if (args.length > 1) {
            final CheatModule module = FaithD1ck.moduleManager.getModule(args[1]);

            if (module == null) {
                chat("Module '" + args[1] + "' not found.");
                return;
            }
            
            module.setHidden(!module.isHidden());
            chat((module.isHidden() ? "Hid" : "Displayed") + " module §8" + module.getName() + "§3.");
            return;
        }

        chatSyntax("hied <module>");
    }

    @Override
    public List<String> tabComplete(final String[] args) {
        if (args.length == 0) return new ArrayList<>();
        String moduleName = args[1];
        return FaithD1ck.INSTANCE.moduleManager.getModules().stream()
                .map(CheatModule::getName)
                .filter(name -> name.toLowerCase().startsWith(moduleName.toLowerCase())).collect(Collectors.toList());
    }
}