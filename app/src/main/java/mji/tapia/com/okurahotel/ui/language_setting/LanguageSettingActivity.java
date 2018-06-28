package mji.tapia.com.okurahotel.ui.language_setting;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import mji.tapia.com.okurahotel.widget.PickerView;
import mji.tapia.com.service.language.LanguageManager;

/**
 * Created by Sami on 1/25/2018.
 */

public class LanguageSettingActivity extends BaseActivity implements LanguageSettingContract.View {

    @Inject
    LanguageSettingPresenter presenter;

    @BindView(R.id.ok)
    TextView ok_tv;

    @BindView(R.id.picker_view)
    PickerView pickerView;

    @BindView(R.id.language_setting_label)
    TextView title_tv;

    HashMap<Integer, LanguageManager.Language> languageIntegerHashMap = new HashMap<>();

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected ScopedPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        ButterKnife.bind(this);
        languageIntegerHashMap.put(0, LanguageManager.Language.ENGLISH);
        languageIntegerHashMap.put(1, LanguageManager.Language.JAPANESE);
        languageIntegerHashMap.put(2, LanguageManager.Language.CHINESE_TW);
        languageIntegerHashMap.put(3, LanguageManager.Language.CHINESE_ZH);
        languageIntegerHashMap.put(4, LanguageManager.Language.KOREAN);
        languageIntegerHashMap.put(5, LanguageManager.Language.FRENCH);

        ok_tv.setOnClickListener(v -> presenter.back());
        pickerView.setSelectedItem(1,false);
        pickerView.setOnSelectedItemChangeListener(new PickerView.OnSelectedItemChangeListener() {
            @Override
            public void onSelectedItemChange(int itemIndex, boolean fromUser) {
                if(itemIndex == 1) {
                    ok_tv.setEnabled(true);
                    ok_tv.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    ok_tv.setEnabled(false);
                    ok_tv.setTextColor(Color.parseColor("#aaFFFFFF"));
                }

                presenter.onLanguageChange(languageIntegerHashMap.get(itemIndex));
                title_tv.setText(R.string.home_language_setting_label);
            }

            @Override
            public void onSelectedItemFinalChange(int itemIndex, boolean fromUser) {

            }
        });
    }
}
