package com.jari.zhihu.view;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.jari.zhihu.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by hero on 2016/1/17 0017.
 */
public class SplashFragment extends android.support.v4.app.Fragment {

    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_splash, container, false) ;
        imageView = (ImageView)view.findViewById(R.id.iv_splash_image);

        System.out.println("SplashFragment.onCreateView");
        loadBackgroundImage() ;

        return  view ;
    }


    @Override
    public void onResume() {
        super.onResume();
        ScaleAnimation animation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f) ;
        animation.setDuration(3000);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                System.out.println("splash animation start");
                ((SplashListener) getActivity()).splashStarted();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                System.out.println("splash animation end");
                ((SplashListener) getActivity()).splashFinished();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animation);
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    private void loadBackgroundImage() {
        File imageFile = new File(getActivity().getFilesDir(), "splash.jpg") ;
        if(imageFile.exists()){
            /*Picasso.with(getActivity())
                    .load(imageFile)
                    .fit()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            System.out.println("load getFilesDir() splash.jpg success");
                        }

                        @Override
                        public void onError() {
                            System.out.println("load getFilesDir() splash.jpg fail");
                        }
                    });
            System.out.println("load getFilesDir() splash.jpg");*/

            imageView.setImageURI(Uri.fromFile(imageFile));
        }else {
            imageView.setImageResource(R.drawable.splash);
            System.out.println("load resource splash.jpg");
        }
    }


    public interface SplashListener{

        void splashFinished() ;
        void splashStarted() ;
    }
}
