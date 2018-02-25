package net.comboro.server.command.defaults;

import net.comboro.server.command.Command;

public abstract class DefaultCommand extends Command {

    DefaultCommand(String name, String description, String syntax, String example) {
        super(name, description, syntax, example, true);
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
