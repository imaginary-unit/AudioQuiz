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
public class MAQDbHelperTest {

    @Test
    public void checkTablesExistance() {
        DummyActivity activity = Robolectric.setupActivity(DummyActivity.class);
        Assert.assertTrue(activity.checkTablesExistance());
    }
}