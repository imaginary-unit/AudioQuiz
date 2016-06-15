package ru.imunit.maquiz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import ru.imunit.maquiz.models.GameModel;

/**
 * Created by lemoist on 15.06.16.
 */

public class ModelRetainFragment extends Fragment {

    private GameModel mModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setModel(GameModel model) {
        mModel = model;
    }

    public GameModel getModel() {
        return mModel;
    }
}
