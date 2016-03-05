package com.jari.zhihu.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.widget.ImageView;

/**
 * Created by hero on 2016/3/3 0003.
 */
public class AnimatorUtil {


    private static float disX;
    private static float disY;
    private static ImageView imageView;

    public static void actionFloatAnimator(ImageView imageView){
        AnimatorUtil.imageView = imageView ;
        disX = imageView.getWidth() * 0.2f;
        disY = imageView.getHeight() * 0.2f;

        enLargeImageView();

        startAnimator();
    }

    private static void enLargeImageView() {
        PropertyValuesHolder sx = PropertyValuesHolder.ofFloat("scaleX", 1.2f) ;
        PropertyValuesHolder sy = PropertyValuesHolder.ofFloat("scaleY", 1.2f) ;

        ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(imageView, sx, sy);
        imageView.setPivotX(0);
        imageView.setPivotY(0);
        scaleAnimator.setDuration(0).start();
    }

    //倒8 字型绕圈
    private static void startAnimator(){
        PropertyValuesHolder tx1 = PropertyValuesHolder.ofFloat("translationX", 0, -disX);
        PropertyValuesHolder ty1 = PropertyValuesHolder.ofFloat("translationY", 0, -disY);

        ObjectAnimator translationAnimator1 = ObjectAnimator.ofPropertyValuesHolder(imageView, tx1, ty1) ;
        translationAnimator1.setDuration(5000) ;


        ObjectAnimator translationAnimator2 = ObjectAnimator.ofFloat(imageView, "translationY", -disY, 0) ;
        translationAnimator2.setDuration(5000) ;

        PropertyValuesHolder tx3 = PropertyValuesHolder.ofFloat("translationX", -disX, 0);
        PropertyValuesHolder ty3 = PropertyValuesHolder.ofFloat("translationY", 0, -disY);

        ObjectAnimator translationAnimator3 = ObjectAnimator.ofPropertyValuesHolder(imageView, tx3, ty3) ;
        translationAnimator3.setDuration(5000) ;

        ObjectAnimator translationAnimator4 = ObjectAnimator.ofFloat(imageView, "translationY", -disY, 0) ;
        translationAnimator4.setDuration(5000) ;


        AnimatorSet animatorSet = new AnimatorSet() ;

        animatorSet.addListener(new AnimatorListenerAdapter()/*Animator.AnimatorListener()*/ {
            @Override
            public void onAnimationEnd(Animator animation) {
                System.out.println("AnimatorUtil.onAnimationEnd");
                startAnimator() ;
            }
        });
        animatorSet.play(translationAnimator1).before(translationAnimator2);
        animatorSet.play(translationAnimator2).before(translationAnimator3);
        animatorSet.play(translationAnimator3).before(translationAnimator4);
        animatorSet.start();
    }
}
