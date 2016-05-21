import android.app.Activity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.sql.Time;
import java.util.Random;

import ru.imunit.maquizdb.DBTrack;
import ru.imunit.maquizdb.MAQDataSource;

/**
 * Created by theuser on 21.05.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class BasicTests {

    private DBTrack[] addRandomTracks(int count) {
        DBTrack[] tracks = new DBTrack[count];
        Random rnd = new Random();
        for (int i=0; i < count; i++) {
            tracks[i] = new DBTrack(String.valueOf(rnd.nextInt()),
                    String.valueOf(rnd.nextInt()), "");
        }
        Activity activity = Robolectric.setupActivity(Activity.class);
        MAQDataSource dataSource = new MAQDataSource(activity);
        try {
            dataSource.openWritable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource.addTracks(tracks);
        dataSource.close();
        return tracks;
    }

    @Test
    public void randomTracksAddingTest() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        MAQDataSource dataSource = new MAQDataSource(activity);
        try {
            dataSource.openReadable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBTrack[] tracks0 = dataSource.getAllTracks();
        int c0 = tracks0.length;
        addRandomTracks(10);
        tracks0 = dataSource.getAllTracks();
        int c1 = tracks0.length;
        dataSource.close();
        Assert.assertEquals(10, c1-c0);
    }

    @Test
    public void tracksIntersectionTest() {

    }
}
