package ru.imunit.maquizdb;

import android.content.Context;

/**
 * Created by theuser on 28.05.16.
 */
public class DataSourceFactory {
    static public IDataSource getDataSource(Context context) {
        return new MAQDataSource(context);
    }
}
