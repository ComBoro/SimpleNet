package net.comboro.command.defaults;

import net.comboro.command.Command;

public abstract class DefaultCommand extends Command {

    public DefaultCommand(String name, String description, String usageMessage) {
        super(name, description, usageMessage, true);
    }

    @Override
    public boolean isListable() {
        return true;
    }

    @Override
    public String toString() {
        return getClass().getName() + "DefaultComamnd[ " + super.getName()
                + " ]";
    }

}
