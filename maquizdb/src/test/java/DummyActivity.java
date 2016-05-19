import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.sql.SQLException;

import ru.imunit.maquizdb.BlackDirsTable;
import ru.imunit.maquizdb.DBTrack;
import ru.imunit.maquizdb.GamesTable;
import ru.imunit.maquizdb.MAQDataSource;
import ru.imunit.maquizdb.MAQDbHelper;
import ru.imunit.maquizdb.TracksTable;

/**
 * Created by imunit on 08.12.15.
 */
public class DummyActivity extends Activity {

    MAQDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDb();
    }

    private void initDb() {
        dataSource = new MAQDataSource(this);
    }

    public boolean checkTablesExistance() {
        MAQDbHelper dbHelper = new MAQDbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String query = "SELECT COUNT(*) FROM `sqlite_master` WHERE type = 'table' AND name = ?";

        Cursor cur = database.rawQuery(query, new String[] {BlackDirsTable.TABLE_NAME});
        if (!cur.moveToFirst()) return false;
        if (cur.getInt(0) == 0) return false;
        cur.close();

        cur = database.rawQuery(query, new String[] {GamesTable.TABLE_NAME});
        if (!cur.moveToFirst()) return false;
        if (cur.getInt(0) == 0) return false;
        cur.close();

        cur = database.rawQuery(query, new String[] {TracksTable.TABLE_NAME});
        if (!cur.moveToFirst()) return false;
        if (cur.getInt(0) == 0) return false;
        cur.close();

        return true;
    }

    public void addTestTracks() {
        try {
            dataSource.openWritable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBTrack[] tracks = new DBTrack[3];
        tracks[0] = new DBTrack("test_track_1", "test_artist_1", "/test/path1");
        tracks[1] = new DBTrack("test_track_2", "test_artist_1", "/test/path2");
        tracks[2] = new DBTrack("дорожка_3", "test_artist_2", "/test/path3");
        dataSource.addTracks(tracks);
        dataSource.close();
    }

    public int queryTestTracks() {
        try {
            dataSource.openReadable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBTrack[] tracks = new DBTrack[3];
        tracks[0] = dataSource.getTrack("test_track_1", "test_artist_1");
        tracks[1] = dataSource.getTrack("test_track_2", "test_artist_1");
        tracks[2] = dataSource.getTrack("дорожка_3", "test_artist_2");
        int matches = 0;
        for (int i=0; i < 3; i++)
            if (tracks[i] != null) matches++;
        return matches;
    }

    public void deleteTestTracks() {
        try {
            dataSource.openWritable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBTrack[] tracks = new DBTrack[3];
        tracks[0] = new DBTrack("test_track_1", "test_artist_1", "/test/path1");
        tracks[1] = new DBTrack("test_track_2", "test_artist_1", "/test/path2");
        tracks[2] = new DBTrack("дорожка_3", "test_artist_2", "/test/path3");
        dataSource.deleteTracks(tracks);
        dataSource.close();
    }
}
