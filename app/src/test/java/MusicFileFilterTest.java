import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import ru.imunit.maquiz.playlists.MusicFileFilter;

/**
 * Created by theuser on 31.01.16.
 */

@RunWith(RobolectricTestRunner.class)
public class MusicFileFilterTest {

    @Test
    public void testFileFilters() {
        String someMusicFolder =
                "/media/theuser/media/music/3 days grace/(2004) Three Days Grace (Limited Edition)";
        int actualTracksCount = 12;
        File dir = new File(someMusicFolder);
        File[] tracks = dir.listFiles(new MusicFileFilter());
        Assert.assertEquals(tracks.length, actualTracksCount);
    }
}
