import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.imunit.maquizdb.BuildConfig;
import ru.imunit.maquizdb.MAQDataSource;

/**
 * Created by imunit on 08.12.15.
 */
@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class, sdk = 18, manifest = "src/main/AndroidManifest.xml")
@Config(manifest=Config.NONE)
public class MAQDbHelperTest {

    @Test
    public void checkTablesExistance() {
        DummyActivity activity = Robolectric.setupActivity(DummyActivity.class);
        Assert.assertTrue(activity.checkTablesExistance());
    }

    @Test
    public void checkTracksAdding() {
        DummyActivity activity = Robolectric.setupActivity(DummyActivity.class);
        activity.deleteTestTracks();
        activity.addTestTracks();
        Assert.assertEquals(3, activity.queryTestTracks());
    }

    @Test
    public void checkTracksDeletion() {
        DummyActivity activity = Robolectric.setupActivity(DummyActivity.class);
        activity.addTestTracks();
        activity.deleteTestTracks();
        Assert.assertEquals(0, activity.queryTestTracks());
    }
}