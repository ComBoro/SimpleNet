/*
 *   ComBoro's Network Server
 *   Copyright (C) 2018  ComBoro
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.comboro.server.command;

/**
 * Abstract class representing the basics of every command
 */
public abstract class Command {
    private final String name, description, syntax, example;

    private final boolean listable;

    protected Command(String name, String description, String syntax, String example) {
        this(name, description, syntax, example,true);
    }

    protected Command(String name, String description, String syntax, String example,
                      boolean listable) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.example = example;
        this.listable = listable;
    }

    /**
     * Called when the command executes
     *
     * @param sender The sender of the command
     * @param args   The arguments following the label of the command
     * @return if the command was executed successfully
     */
    public abstract boolean execute(CommandSender sender, String args[]);

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getExample(){return example;}

    public boolean isListable() {
        return listable;
    }

    @Override
    public String toString() {
        return getClass().getName() + "( " + name + " )";
    }

}
