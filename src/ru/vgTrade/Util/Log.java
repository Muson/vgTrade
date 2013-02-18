/*
 *  SpoutTrade - In game GUI trading for Bukkit Minecraft servers with Spout
 * Copyright (C) 2011 Oliver Brown (Arkel)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * /
 */

package ru.vgTrade.Util;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

    private static Logger log = Logger.getLogger("Minecraft");
    public static boolean verbose = false;
    
    public static void init(File dir) {
        try {
            FileHandler fh = new FileHandler(dir.getAbsolutePath()+"/trade.log", 1024*1024*3, 3, true);
            fh.setFormatter(new SimpleFormatter());
            log.addHandler(fh);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void info(String msg) {
        log.info(msg);
    }

    public static void warning(String msg) {
        log.warning(msg);
    }

    public static void severe(String msg) {
        log.severe(msg);
    }

    public static void trade(String msg) {
        if (verbose) {
            log.info(msg);
        }
    }
    
    public static void serLogger(Logger lg) {
        log = lg;
    }
}
