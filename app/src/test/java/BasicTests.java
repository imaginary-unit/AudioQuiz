import android.app.Activity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ru.imunit.maquizdb.DBTrack;
import ru.imunit.maquizdb.MAQDataSource;

/**
 * Created by theuser on 21.05.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class BasicTests {

    private ArrayList<DBTrack> getRandomTracks(int count) {
        ArrayList<DBTrack> tracks = new ArrayList<>();
        Random rnd = new Random();
        for (int i=0; i < count; i++) {
            tracks.add(new DBTrack(String.valueOf(rnd.nextInt()),
                    String.valueOf(rnd.nextInt()), ""));
        }
        return tracks;
    }

    @Test
    public void randomTracksAddingTest() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        MAQDataSource dataSource = new MAQDataSource(activity);
        try {
            dataSource.openWritable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBTrack[] tracks0 = dataSource.getAllTracks();
        int c0 = tracks0.length;
        // adding
        ArrayList<DBTrack> newTracks = getRandomTracks(10);
        dataSource.addTracks(newTracks.toArray(new DBTrack[newTracks.size()]));
        //
        tracks0 = dataSource.getAllTracks();
        int c1 = tracks0.length;
        dataSource.close();
        Assert.assertEquals(10, c1-c0);
    }

    @Test
    public void tracksIntersectionTest() {
        // This implementation outperforms loops and ArrayLists
        long t = System.nanoTime();
        List<DBTrack> trackList = getRandomTracks(1000);
        List<DBTrack> A = trackList.subList(0, 800);
        List<DBTrack> B = trackList.subList(100, 1000);
        Set<DBTrack> sA = new HashSet<>(A);
        Set<DBTrack> sB = new HashSet<>(B);
//        Set<DBTrack> intersect = new HashSet<>(sA);
//        intersect.retainAll(sB);
//        Assert.assertEquals(700, intersect.size());
        Set<DBTrack> toAdd = new HashSet<>(sA);
        toAdd.removeAll(sB);
        Set<DBTrack> toDel = new HashSet<>(sB);
        toDel.removeAll(sA);
        DBTrack[] addArr = toAdd.toArray(new DBTrack[toAdd.size()]);
        DBTrack[] delArr = toDel.toArray(new DBTrack[toDel.size()]);
        Assert.assertEquals(100, addArr.length);
        Assert.assertEquals(200, delArr.length);
        double dt = (double)(System.nanoTime() - t) / (double)(1E9);
        System.out.println(dt);
    }

    @Test
    public void tracksSimpleLoopTest() {
        long t = System.nanoTime();
        List<DBTrack> trackList = getRandomTracks(1000);
        List<DBTrack> A = trackList.subList(0, 800);
        List<DBTrack> B = trackList.subList(100, 1000);
        List<DBTrack> toAdd = new ArrayList<>();
        List<DBTrack> toDel = new ArrayList<>();

        int an = A.size();
        int bn = B.size();
        for (int i=0; i < an; i++) {
            DBTrack tmp = A.get(i);
            if (!B.contains(tmp)) toAdd.add(tmp);
        }
        for (int i=0; i < bn; i++) {
            DBTrack tmp = B.get(i);
            if (!A.contains(tmp)) toDel.add(tmp);
        }
        DBTrack[] addArr = toAdd.toArray(new DBTrack[toAdd.size()]);
        DBTrack[] delArr = toDel.toArray(new DBTrack[toDel.size()]);
        Assert.assertEquals(100, addArr.length);
        Assert.assertEquals(200, delArr.length);
        double dt = (double)(System.nanoTime() - t) / (double)(1E9);
        System.out.println(dt);
    }
}
