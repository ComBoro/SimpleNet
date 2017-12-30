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

package net.comboro;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A file/directory loader that uses the path_bin where the .jar was ran from.
 */
public class Loader {

    private static final String path_bin = System.getProperty("user.dir");


    /**
     * Loads a directory with a specific name and if there s no directory it
     * creates new
     *
     * @param name the name of the directory
     * @return the actual {@link File}
     */
    public static File loadDirectory(String name) {
        File file = new File(path_bin, name);

        if (file.exists())
            if (file.isDirectory())
                return file;

        file.mkdir();
        return file;
    }

    /**
     * Loads a file with a specific name and if there s no file it creates new
     *
     * @param name the name of the file
     * @return the actual {@link File}
     */
    public static File loadFile(String name) {
        try {
            File file = new File(path_bin, name);

            System.out.println(file.getAbsolutePath());

            if (!file.exists())
                file.createNewFile();
            return file;
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

    public static Image loadImage(String path) throws NullPointerException {
        return Toolkit.getDefaultToolkit().getImage(path);
    }

}
