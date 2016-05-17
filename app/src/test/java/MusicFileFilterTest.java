import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;

import ru.imunit.maquiz.playlists.MusicFileFilter;

/**
 * Created by theuser on 31.01.16.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class MusicFileFilterTest {

    @Test
    public void testFileFilters() {
        String someMusicFolder = "/home/theuser/Music";
        int actualTracksCount = 56;
        File dir = new File(someMusicFolder);
        File[] tracks = dir.listFiles(new MusicFileFilter());
        Assert.assertEquals(tracks.length, actualTracksCount);
    }
}
