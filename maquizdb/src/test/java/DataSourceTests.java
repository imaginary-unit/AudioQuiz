import android.app.Activity;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import ru.imunit.maquizdb.DataSourceFactory;
import ru.imunit.maquizdb.IDataSource;
import ru.imunit.maquizdb.entities.DBGame;
import ru.imunit.maquizdb.entities.DBTrack;

/**
 * Created by lemoist on 07.06.16.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class DataSourceTests {

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
        IDataSource dataSource = DataSourceFactory.getDataSource(activity);
        dataSource.openWritable();

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
    public void randomTracksSelection() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        IDataSource dataSource = DataSourceFactory.getDataSource(activity);
        dataSource.openWritable();

        // adding
        ArrayList<DBTrack> newTracks = getRandomTracks(10);
        dataSource.addTracks(newTracks.toArray(new DBTrack[newTracks.size()]));
        //

        for (int i=0; i < 2; i++) {
            DBTrack[] randTracks = dataSource.getRandomTracks(5);
            Assert.assertEquals(5, randTracks.length);
            for (int j=0; j < 5; j++) {
                System.out.println(String.format("%d) %s - %s",
                        j, randTracks[j].getArtist(), randTracks[j].getName()));
            }
        }
    }

    @Test
    public void updateTrackGuesses() {
        Activity activity = Robolectric.setupActivity(Activity.class);
        IDataSource dataSource = DataSourceFactory.getDataSource(activity);
        dataSource.openWritable();

        // adding
        ArrayList<DBTrack> newTracks = getRandomTracks(10);
        dataSource.addTracks(newTracks.toArray(new DBTrack[newTracks.size()]));
        //

        DBTrack[] allTracks = dataSource.getAllTracks();

        int n = 4;
        DBTrack[] guessedTracks = Arrays.copyOfRange(allTracks, 0, n);
        int[] addGuesses = new int[] {0, 1, 2, 3};
        int[] addCorrectGuesses = new int[] {0, 0, 1, 2};
        // expected:
        // g : 0, 2, 4, 6
        // cg: 0, 0, 2, 4
        for (int i=0; i < 2; i++) {
            dataSource.updateTracksGuesses(guessedTracks, addGuesses, addCorrectGuesses);
        }

        allTracks = dataSource.getAllTracks();
        // output
        for (int i=0; i < 10; i++) {
            System.out.println(String.format("%d) %s - %s, (%d / %d)",
                    i, allTracks[i].getArtist(), allTracks[i].getName(),
                    allTracks[i].getGuess(), allTracks[i].getCorrectGuess()));
        }
    }
}
