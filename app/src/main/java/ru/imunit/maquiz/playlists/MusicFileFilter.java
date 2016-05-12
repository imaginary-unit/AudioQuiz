package ru.imunit.maquiz.playlists;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by imunit on 09.11.15.
 */
public class MusicFileFilter implements FileFilter {

    public static final String[] MUSIC_EXTENSIONS = { "mp3" };

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        else {
            String name = f.getName();
            int i = name.lastIndexOf('.');
            String ext = name.substring(i+1);
            boolean match = false;
            for (String validExt : MUSIC_EXTENSIONS) {
                if (ext.equals(validExt)) {
                    match = true;
                    break;
                }
            }
            return match;
        }
    }
}
