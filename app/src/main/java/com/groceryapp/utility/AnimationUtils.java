package com.groceryapp.utility;

import android.content.Context;
import android.view.animation.Animation;

import com.groceryapp.R;

public class AnimationUtils {

    public static Animation getFadeIn(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fade_in);
    }

    public static Animation getFadeOut(Context context){
        return android.view.animation.AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.fade_out);

    }
}
