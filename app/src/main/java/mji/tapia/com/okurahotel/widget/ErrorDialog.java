package mji.tapia.com.okurahotel.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.subjects.BehaviorSubject;
import mji.tapia.com.okurahotel.R;

/**
 * Created by Sami on 2/2/2018.
 */

public class ErrorDialog extends AlertDialog{


    @BindView(R.id.back)
    TextView back_tv;

    @BindView(R.id.message)
    TextView message_tv;

    private BehaviorSubject<String> messageBehaviorSubject = BehaviorSubject.create();

    public ErrorDialog(Context context) {
        super(context);
    }

    public ErrorDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public ErrorDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void setMessage(CharSequence message) {
        messageBehaviorSubject.onNext(message.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_error);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
        ButterKnife.bind(this);
        back_tv.setOnClickListener(view -> {
            if(isShowing()) {
                dismiss();
            }
        });
        messageBehaviorSubject.subscribe(msg -> {
            message_tv.setText(msg);
        });
    }

}
