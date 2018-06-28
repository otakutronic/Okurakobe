package mji.tapia.com.okurahotel.ui.staff_mode.lock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mji.tapia.com.okurahotel.BaseActivity;
import mji.tapia.com.okurahotel.R;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;

/**
 * Created by Sami on 1/29/2018.
 */

public class LockActivity extends BaseActivity implements LockContract.View {

    @Inject
    LockPresenter presenter;

    @BindView(R.id.lock_input_0)
    View input0_bt;

    @BindView(R.id.lock_input_1)
    View input1_bt;

    @BindView(R.id.lock_input_2)
    View input2_bt;

    @BindView(R.id.lock_input_3)
    View input3_bt;

    @BindView(R.id.lock_input_4)
    View input4_bt;

    @BindView(R.id.lock_input_5)
    View input5_bt;

    @BindView(R.id.lock_input_6)
    View input6_bt;

    @BindView(R.id.lock_input_7)
    View input7_bt;

    @BindView(R.id.lock_input_8)
    View input8_bt;

    @BindView(R.id.lock_input_9)
    View input9_bt;

    @BindView(R.id.input_text)
    TextView inputText;

    @BindView(R.id.lock_back)
    View input_back;

    @BindView(R.id.back_bt)
    View back_bt;

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected ScopedPresenter getPresenter() {
        return presenter;
    }



    HashMap<View, String> inputMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_lock);
        ButterKnife.bind(this);
        inputMap = new HashMap<>();

        inputMap.put(input0_bt,"0");
        inputMap.put(input1_bt,"1");
        inputMap.put(input2_bt,"2");
        inputMap.put(input3_bt,"3");
        inputMap.put(input4_bt,"4");
        inputMap.put(input5_bt,"5");
        inputMap.put(input6_bt,"6");
        inputMap.put(input7_bt,"7");
        inputMap.put(input8_bt,"8");
        inputMap.put(input9_bt,"9");

        View.OnClickListener onInputListener = v -> {
            if(inputText.getText().length() < 4) {
                inputText.append(inputMap.get(v));
                if(inputText.getText().length() == 4) {
                    presenter.onPin(inputText.getText().toString());
                }
            }
        };
        for (View v: inputMap.keySet()) {
            v.setOnClickListener(onInputListener);
        }

        input_back.setOnClickListener(v -> {
            if(inputText.getText().length() > 0) {
                inputText.setText(inputText.getText().subSequence(0,inputText.getText().length() - 1));
            }
        });

        back_bt.setOnClickListener(v -> presenter.back());


    }
}
