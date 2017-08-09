package org.mocraft.command.executor.argument;

import org.bukkit.command.CommandSender;
import org.mocraft.PerMession;
import org.mocraft.tasks.GMTask;
import org.mocraft.tasks.PexTask;
import org.mocraft.tasks.VanillaTask;

public class PmReflectArgs {

    private PerMession instance;

    /*              [0]                                         [1]         [2]         [3]
        /<command>  dur|duration                                <period>    <cmd>               - After <period> sec execute <cmd>.
        /<command>  dur-reflect|duratio-reflect                 <period>    <cmd>               - Execute <cmd> right now and execute <reflect-cmd> after <period> sec.
        /<command>  dur-delay-reflect|duration-delay-reflect    <period>    <delay>     <cmd>   - After <delay> sec execute <cmd>, and after <period> sec execute <reflect-cmd>.
    */

    public PmReflectArgs(PerMession instance, CommandSender sender, String[] args) {
        this.instance = instance;
        boolean reflect = args[0].contains("reflect");
        int period = Integer.valueOf(args[1]);
        int delay = args[0].contains("delay") ? Integer.valueOf(args[2]) : 0;
        int startIndex = delay != 0 ? 3 : 2;
        assignTask(sender, reflect, delay, period, startIndex, args);
    }


    public void assignTask(CommandSender sender, boolean reflect, int delay, int period, int argsIndex, String[] args) {
        String command = "";
        VanillaTask task = null;
        for (int i = argsIndex; i < args.length; ++i)
            command += args[i] + " ";
        if(instance.permissionsEx && command.startsWith("pex")) {
            task = new PexTask(instance, sender, reflect, delay, period, command.trim());
        } else if(instance.groupManager && instance.gmReflector.reflect(command) != null) {
            task = new GMTask(instance, sender, reflect, delay, period, command.trim());
        } else {
            task = new VanillaTask(instance, sender, reflect, delay, period, command.trim());
        }
        task.setId(instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, task, delay * 20L, 20L));
        instance.tasks.add(task);
        return;
    }

}
