package com.example.crawler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.crawler.Fragment.ContentFragment;
import com.google.android.material.chip.Chip;

public class cardComponent extends ConstraintLayout {
    public String textString;
    public String WhenStr;
    public String WhereStr;
    public Context nowContext;

    public TextView title;
    public TextView deadLine;
    public Chip FavoriteButton;

    public String ContentURL;

    public cardComponent(@NonNull Context context) {
        super(context);
        nowContext = context;
        textString = "預設標題";
        initViews();
    }

    public cardComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        nowContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.cardComponent);
        getValues(typedArray);
        initViews();
    }
    public cardComponent(@NonNull Context context, AttributeSet attrs) {
        super(context,attrs);
        nowContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.cardComponent);
        getValues(typedArray);
        initViews();
    }
    private void getValues(TypedArray typedArray)
    {
        textString = typedArray.hasValue(R.styleable.cardComponent_textString)?
                typedArray.getString(R.styleable.cardComponent_textString):"預設文字";
        typedArray.recycle();
    }
    private void initViews()
    {
        inflate(nowContext,R.layout.card_component,this);
        title = (TextView) findViewById(R.id.titleText);
        deadLine = (TextView) findViewById(R.id.DeadLine);
        FavoriteButton = (Chip) findViewById(R.id.favoriteButton);
        Drawable fillFavorite = getResources().getDrawable(R.drawable.favorite, nowContext.getTheme());
        Drawable emptyFavorite = getResources().getDrawable(R.drawable.unfill_favorite, nowContext.getTheme());
        FavoriteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Chip chip = (Chip)view;
                if(chip.getChipIcon().equals(fillFavorite))
                    chip.setChipIcon(emptyFavorite);
                else
                    chip.setChipIcon(fillFavorite);
                Log.i("Test","Clicked"+chip.getChipIcon().toString());
            }
        });

        findViewById(R.id.Card).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Context nowContext = view.getContext();
                if(nowContext instanceof MainActivity) {
                    MainActivity now = (MainActivity) nowContext;
                    now.changeFragment(ContentFragment.class,ContentURL);
                }
                else if(nowContext instanceof categoryActivity){
                    categoryActivity now = (categoryActivity) nowContext;
                    now.changeFragment(ContentFragment.class,ContentURL);
                }
                //Log.i("Test","Click URL: "+nowContext.toString());
            }
        });

        //Title.setText(textString);
    }
    public void setTitleText(String str)
    {
        textString=str;
        title.setText(str);
    }
    public void setDeadLine(String str) { deadLine.setText(str); }
    public void setWhen(String str){WhenStr=str;}
    public void setWhere(String str){WhereStr=str;}
    public void setContentURL(String url){
        ContentURL=url;
    }
    public void setCard(cardComponent copyCard)
    {
        title.setText(copyCard.title.getText());
        WhenStr = copyCard.WhenStr;
        WhereStr = copyCard.WhereStr;
        deadLine.setText(copyCard.deadLine.getText());
        ContentURL = copyCard.ContentURL;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj==this)
            return true;
        if(obj instanceof cardComponent) {
            cardComponent card = (cardComponent) obj;
            //Log.i("Test",this.title.getText().toString()+"<=>"+card.title.getText().toString());
            if (!this.title.getText().toString().equals(card.title.getText().toString())) {
                //Log.i("Test","False");
                return false;
            }
            else{
                //Log.i("Test","True");
                return true;
            }
        }
        return false;
    }
}

