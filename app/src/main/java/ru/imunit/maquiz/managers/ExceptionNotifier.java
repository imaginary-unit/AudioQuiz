package ru.imunit.maquiz.managers;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import ru.imunit.maquiz.R;


public class ExceptionNotifier {

    private ActionListener mActionListener;
    private View mHost;
    private CharSequence mMessage;

    public interface ActionListener {
        void onClick();
    }

    private ExceptionNotifier(View host) {
        mHost = host;
    }

    public static ExceptionNotifier make(View host, CharSequence message) {
        ExceptionNotifier en = new ExceptionNotifier(host);
        en.setMessage(message);
        return en;
    }

    public ExceptionNotifier setActionListener(ActionListener listener) {
        mActionListener = listener;
        return this;
    }

    public void show() {
        Snackbar snackbar = Snackbar.make(mHost, mMessage, Snackbar.LENGTH_INDEFINITE);

        TextView tv = (TextView)snackbar.getView().
                findViewById(android.support.design.R.id.snackbar_text);
        tv.setMaxLines(6);

        if (mActionListener != null)
            snackbar.setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActionListener.onClick();
                }
            });
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);

            }

            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
            }
        });
        snackbar.show();
    }

    public void setMessage(CharSequence message) {
        mMessage = message;
    }

}
