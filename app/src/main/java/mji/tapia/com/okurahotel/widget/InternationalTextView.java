package mji.tapia.com.okurahotel.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import mji.tapia.com.okurahotel.R;
import mji.tapia.com.service.language.LanguageManager;

/**
 * Created by Sami on 9/21/2017.
 */

public class InternationalTextView extends android.support.v7.widget.AppCompatTextView {

    static Typeface jpTypeface = null;
    static Typeface enTypeface = null;

    private final static int ENGLISH =  0;
    private final static int JAPANESE = 1;
    private final static int CHINESE_TW =  2;
    private final static int CHINESE_ZH = 3;
    private final static int FRENCH =  4;
    private final static int GERMAN = 5;
    private final static int KOREAN =  6;

    static Map<String, Integer> languageMap;
    static {
        languageMap = new HashMap<>();
        languageMap.put("en",ENGLISH);
        languageMap.put("ja",JAPANESE);
        languageMap.put("zh_TW",CHINESE_TW);
        languageMap.put("zh_CN",CHINESE_ZH);
        languageMap.put("ko",KOREAN);
        languageMap.put("fr",FRENCH);
        languageMap.put("de",GERMAN);

    }

    public InternationalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);



        TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.InternationalTextViewAttrs,0,0);

        int language = styledAttributes.getInt(R.styleable.InternationalTextViewAttrs_language, -1);
        styledAttributes.recycle();

        if(language == -1){
            language = getLocaleLanguage();
        }
        init(context, language);
    }

    public InternationalTextView(Context context) {
        super(context);
        int language = getLocaleLanguage();
        init(context, language);
    }


    void init(Context context, int language) {

        if(jpTypeface == null){
            jpTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/KozMinPro-Heavy.otf");
        }

        if(enTypeface == null){
            enTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Times_New_Roman_Bold_Italic.ttf");
        }

        Typeface myTypeface;
        switch (language){
            case ENGLISH:
                myTypeface = enTypeface;
                break;
            case JAPANESE:
                myTypeface = jpTypeface;
                break;
            case CHINESE_TW:
                myTypeface = jpTypeface;
                break;
            case CHINESE_ZH:
                myTypeface = jpTypeface;
                break;
            case KOREAN:
                myTypeface = jpTypeface;
                break;
            case FRENCH:
                myTypeface = enTypeface;
                break;
            case GERMAN:
                myTypeface = enTypeface;
                break;
            default:
                myTypeface = enTypeface;
                break;
        }
        setTypeface(myTypeface);
    }
    int getLocaleLanguage(){
        Locale current = getTextLocale();
        if(current.getLanguage().equals("zh")) {
            return languageMap.get(current.toString());
        } else {
            return languageMap.get(current.getLanguage());
        }

    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        Log.e("InternationalTextView","text locale: " + getResources().getConfiguration().locale.getLanguage() + " new text: " + text);
        String languageTag;
        if(getResources().getConfiguration().locale.getLanguage().equals("zh")) {
            languageTag = getResources().getConfiguration().locale.toString();
        } else {
            languageTag = getResources().getConfiguration().locale.getLanguage();
        }
        if(getLocaleLanguage() != languageMap.get(languageTag)) {
            setTextLocale(getResources().getConfiguration().locale);
            updateLanguage(languageMap.get(languageTag));
        }

        super.onTextChanged(text, start, lengthBefore, lengthAfter);

    }

    @Override
    public void dispatchConfigurationChanged(Configuration newConfig) {
        super.dispatchConfigurationChanged(newConfig);
    }

    private void updateLanguage(int language) {
        Typeface myTypeface;
        switch (language){
            case ENGLISH:
                myTypeface = enTypeface;
                break;
            case JAPANESE:
                myTypeface = jpTypeface;
                break;
            case CHINESE_TW:
                myTypeface = jpTypeface;
                break;
            case CHINESE_ZH:
                myTypeface = jpTypeface;
                break;
            case KOREAN:
                myTypeface = jpTypeface;
                break;
            case FRENCH:
                myTypeface = enTypeface;
                break;
            case GERMAN:
                myTypeface = enTypeface;
                break;
            default:
                myTypeface = enTypeface;
                break;
        }
        setTypeface(myTypeface);
    }
}
