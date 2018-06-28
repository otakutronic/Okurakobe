package mji.tapia.com.okurahotel.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;



import mji.tapia.com.okurahotel.R;

/**
 * Created by Sami on 10/25/2017.
 */

public class PickerView extends View {

    String itemArray[] = {"1"};

    OnSelectedItemChangeListener onSelectedItemChangeListener;

    boolean isScrolling=  false;
    long startTouchTime;
    double currentVelocity;
    final double accelConstant = 0.002;
    final double minVelocity = 0.1 ;
    Handler accelHandler =  new Handler();
    Runnable accelRunnable = new Runnable() {


        @Override
        public void run() {

            if(isScrolling){
                return;
            }
            if(index >0 || index<= (-(itemArray.length-1))* (space + font_size)){
                setCurrentItem(getClosestPosition(),true);
                return;
            }
            long changeTime= System.currentTimeMillis()- startTouchTime;
            Log.d("Scroll Change in Time",String.valueOf(changeTime));
            startTouchTime = System.currentTimeMillis();
            currentVelocity =  (currentVelocity - ((accelConstant* currentVelocity)*changeTime));
            Log.d("Scroll Velocity",String.valueOf(currentVelocity));

            index = index - currentVelocity * changeTime;
            updateCurrentItem(getClosestPosition(),true);
            Log.d("Scroll Index",String.valueOf(index));
            invalidate();
            if(Math.abs(currentVelocity) >= minVelocity)
                accelHandler.postDelayed(this,20);
            else
                setCurrentItem(getClosestPosition(),true);
        }
    };




    //using gaussian function for the width

//    int onScreenItems = 5;
//    int secondColor = Color.parseColor("ffffff88");
    Paint debugPaint;
    int color = Color.parseColor("#FFFFFF");
    int alpha = 128;
    int space = 40;
    float font_size = 40;
    String font_path= null;//"font/mplus_2p_light.ttf"

    double index = 0;
    TextPaint textPaint;

    int itemSelected = 0;
    float startEventY;

    public PickerView(Context context) {
        super(context);
        init(null);
    }

    public PickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    void init(@Nullable AttributeSet attrs) {
        debugPaint = new Paint();
        debugPaint.setColor(Color.parseColor("#ff0000"));

        if(attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.PickerView);
            if(ta != null) {
                color = ta.getColor(R.styleable.PickerView_pv_color, Color.parseColor("#FFFFFF"));
                alpha = ta.getInt(R.styleable.PickerView_pv_alpha, 128);
                space = ta.getDimensionPixelSize(R.styleable.PickerView_pv_space, 40);
                font_size = ta.getDimensionPixelSize(R.styleable.PickerView_pv_textSize, 40);
                font_path = ta.getString(R.styleable.PickerView_pv_font);

                CharSequence[] charSequence = ta.getTextArray(R.styleable.PickerView_pv_entries);
                if(charSequence != null){
                    String[] mEntriesString = new String[charSequence.length];
                    int i=0;
                    for(CharSequence ch: charSequence){
                        mEntriesString[i++] = ch.toString();
                    }
                    itemArray = mEntriesString;
                }


                ta.recycle();
            }
        }

        textPaint = new TextPaint();
        textPaint.setColor(color);
        textPaint.setTextSize(font_size);
        textPaint.setTextAlign(Paint.Align.CENTER);
        if(font_path != null && !font_path.equals("")) {
            textPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), font_path));
        }

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(itemArray != null){
            for (int i = 0; i < itemArray.length; i++) {
                drawItem(canvas, i);
            }
        }
    }

    double gaussFunction(double height){
        double max = 1;
        double center = 0;
        double deviation = 30;

        return max * Math.exp(- ((height - center) * (height - center)) / (2 * (deviation * deviation)));
    }

    void drawItem(Canvas canvas, int position) {
        Rect bounds = new Rect();
        Paint paint = getTextPaint(position);

        paint.getTextBounds(itemArray[position], 0, itemArray[position].length(), bounds);

        int textHeight = bounds.bottom - bounds.top;


        canvas.drawText(itemArray[position], getWidth()/2 , getPosition(position) + textHeight/2, paint);
        // canvas.drawLine(0,(float) (getPosition(position)),getWidth(),(float) (getPosition(position)),debugPaint);
    }

    public void setItemArray(String[] strings) {
        itemArray = strings;
    }

    int getPosition(int position) {
        int middleheight = getHeight()/2;
        double xPos = middleheight + index + position * space + position * font_size;
        return (int)xPos;
    }

    float getFontSize(int position) {
        int middleheight = getHeight()/2;
        double xPos = middleheight + index + position * space + position * font_size;
        double coef = gaussFunction(xPos - middleheight);
        return (float)((1 + coef) * font_size);
    }

    TextPaint getTextPaint(int position){

        TextPaint paint = new TextPaint(textPaint);
        int pos = getPosition(position);
        int itemHeight = getHeight();
        paint.setTextSize(getFontSize(position));
        Rect bounds = new Rect();
        paint.getTextBounds(itemArray[position], 0, itemArray[position].length(), bounds);

        int textHeight = bounds.bottom - bounds.top;

        if(pos - textHeight/2 + textHeight*1.1 > 0 && pos - textHeight/2< itemHeight*0.2){
            //apply low gradient
            paint.setShader(new LinearGradient(0,0,0, (int)(itemHeight*0.1),
                    Color.argb(0, Color.red(color), Color.green(color),Color.blue(color)),
                    Color.argb(alpha, Color.red(color), Color.green(color),Color.blue(color)), Shader.TileMode.CLAMP));
        } else if(pos + textHeight >itemHeight*0.8 + textHeight/2 && pos < itemHeight + textHeight/2 ) {
            paint.setShader(new LinearGradient(0,(int)(itemHeight*0.8),0, itemHeight,
                    Color.argb(alpha, Color.red(color), Color.green(color),Color.blue(color)),
                    Color.argb(0, Color.red(color), Color.green(color),Color.blue(color)), Shader.TileMode.CLAMP));
        }
        else if (pos > itemHeight * 0.4 && pos < itemHeight * 0.6) {

        }
        else {
            paint.setAlpha(alpha);
        }

        return paint;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                doMove(event);
                break;
            case MotionEvent.ACTION_UP:
                doUp(event);
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event)
    {
        isScrolling = true;
        startTouchTime = System.currentTimeMillis();
        startEventY = event.getY();

    }

    private void doMove(MotionEvent event)
    {
        long changeTime= System.currentTimeMillis()- startTouchTime;
        startTouchTime = System.currentTimeMillis();

        float dy = startEventY - event.getY();
        index-=dy;
        updateCurrentItem(getClosestPosition(),true);
        startEventY = event.getY();

        currentVelocity =  dy/changeTime;

        invalidate();
    }

    private void doUp(MotionEvent event)
    {
        isScrolling = false;
        accelHandler.post(accelRunnable);

//
//        int closestItem = 0;
//        float itemHeight = getHeight();
//
//        for(int i= 0; i < itemArray.length; i++) {
//            int position = getPosition(i);
//            if(Math.abs(position - itemHeight*0.5f) < Math.abs(getPosition(closestItem) - itemHeight*0.5f)){
//                closestItem = i;
//            }
//        }

//        xPos = middleheight + index + position * space + position * font_size;
//        setSelectedItem(closestItem, true);


    }


    public int getItemSelected(){
        return itemSelected;
    }

    public void setCurrentItem(int itemSelected, boolean animated) {
        if(animated) {
            animationToItem(itemSelected, true);
        } else {
            index = -itemSelected * (space + font_size);
            if(onSelectedItemChangeListener != null) {
                onSelectedItemChangeListener.onSelectedItemFinalChange(itemSelected, false);
            }
            updateCurrentItem(getClosestPosition(),true);
            invalidate();
        }
        this.itemSelected = itemSelected;
    }

    public void setSelectedItem(int itemSelected, boolean animated) {
        if(animated) {
            animationToItem(itemSelected, false);
        } else {
            index = -itemSelected * (space + font_size);
            if(onSelectedItemChangeListener != null) {
                onSelectedItemChangeListener.onSelectedItemFinalChange(itemSelected, false);
            }
            updateCurrentItem(getClosestPosition(),false);
            invalidate();
        }
        this.itemSelected = itemSelected;
    }

    private void animationToItem(int item, boolean fromUser) {
        ValueAnimator va = ValueAnimator.ofFloat((float)index, -item* (space + font_size));
        va.addUpdateListener(animation -> {
            index = (Float) animation.getAnimatedValue();
            updateCurrentItem(getClosestPosition(),fromUser);
            invalidate();
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(onSelectedItemChangeListener != null) {
                    onSelectedItemChangeListener.onSelectedItemFinalChange(item, fromUser);
                }
                updateCurrentItem(getClosestPosition(),fromUser);
            }
        });
        va.setDuration(250);
        va.setInterpolator(new AccelerateInterpolator());
        va.start();
    }

    int getClosestPosition(){


        int closestItem = 0;
        float itemHeight = getHeight();
        for(int i= 0; i < itemArray.length; i++) {
            int position = getPosition(i);
            if(Math.abs(position - itemHeight*0.5f) < Math.abs(getPosition(closestItem) - itemHeight*0.5f)){
                closestItem = i;
            }
        }
        return  closestItem;
    }

    private void updateCurrentItem(int currentItem, boolean fromUser) {
        if(itemSelected != currentItem) {
            itemSelected =currentItem;
            if(onSelectedItemChangeListener != null) {
                onSelectedItemChangeListener.onSelectedItemChange(currentItem, fromUser);
            }
        }
    }

    public void setOnSelectedItemChangeListener(OnSelectedItemChangeListener onSelectedItemChangeListener) {
        this.onSelectedItemChangeListener = onSelectedItemChangeListener;
    }

    public interface OnSelectedItemChangeListener {
        void onSelectedItemChange(int itemIndex, boolean fromUser);

        void onSelectedItemFinalChange(int itemIndex, boolean fromUser);
    }


}
