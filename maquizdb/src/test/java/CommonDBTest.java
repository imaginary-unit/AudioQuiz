import android.app.Activity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;

import ru.imunit.maquizdb.DBTrack;
import ru.imunit.maquizdb.MAQDataSource;

/**
 * Created by lemoist on 25.05.16.
 */
@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class, sdk = 18, manifest = "src/main/AndroidManifest.xml")
@Config(manifest=Config.NONE)
public class CommonDBTest {

    @Test
    public void testClearMusic() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        MAQDataSource dataSource = new MAQDataSource(activity);
        try {
            dataSource.openWritable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBTrack[] allTracks = dataSource.getAllTracks();
        dataSource.deleteTracks(allTracks);
        allTracks = dataSource.getAllTracks();
        Assert.assertEquals(0, allTracks.length);
    }
}
