import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import ru.imunit.maquizdb.DirectoriesTable;
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

        Cursor cur = database.rawQuery(query, new String[] {DirectoriesTable.TABLE_NAME});
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


}
