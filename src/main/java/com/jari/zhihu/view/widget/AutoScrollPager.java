package com.jari.zhihu.view.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jari.zhihu.R;
import com.jari.zhihu.entity.LatestNews;
import com.jari.zhihu.util.DensityUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hero on 2016/1/19 0019.
 */
public class AutoScrollPager extends FrameLayout implements ViewPager.OnPageChangeListener {

    /**
     * 监听当前页面点击
     */
    public interface OnPageClickedListener{
        void pageClicked(int newsId) ;
    }

    private ViewPager viewPager ;

    private List<ImageView> dotViews ;

//    private List<String> imageUrls;
    private List<LatestNews.TopStoriesEntity> storiesEntities;

    private Context context ;

    private MyPagerAdapter pagerAdapter ;

    private LinearLayout dotViewContainer;

    private Handler handler ;

    private int currentImageIndex ;

    private OnPageClickedListener pageClickedListener ;

    public void setOnPageClickedListener(OnPageClickedListener pageClickedListener){
        this.pageClickedListener = pageClickedListener ;
    }

    public AutoScrollPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        viewPager = new ViewPager(context) ;
        viewPager.addOnPageChangeListener(this);
        this.context = context ;

        handler = new Handler() ;

        currentImageIndex = 0 ;
    }


    public void setTopStoriesEntity(List<LatestNews.TopStoriesEntity> entities){
        storiesEntities = entities ;

        clearAllViews() ;

//        fetchImages() ;  //fetch的优先级低

        addViewPager() ;

        addDotView() ;

        setupFocusDot() ;

        autoScrollImages() ;

    }

    private void clearAllViews() {
        handler.removeCallbacksAndMessages(null);
        this.removeAllViews();
    }


   /* private void addNewsTitles() {
        TextView textView = new TextView(this.getContext()) ;
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        textView.setMaxLines(2);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM) ;
        layoutParams.setMargins(DensityUtil.px2dip(context, 10),DensityUtil.px2dip(context, 10),DensityUtil.px2dip(context, 10),DensityUtil.px2dip(context, 10));

        for (LatestNews.TopStoriesEntity entity : storiesEntities) {
            textView.setText(entity.getTitle());
            addView(textView, layoutParams);
        }
    }*/


    private void setupFocusDot() {
        for (int i = 0; i < dotViewContainer.getChildCount(); i++) {
            ImageView imageView = (ImageView)dotViewContainer.getChildAt(i) ;
            if(i == currentImageIndex){
                imageView.setImageResource(R.drawable.dot_focus);
            }else {
                imageView.setImageResource(R.drawable.dot_blur);
            }
        }
    }

    private void autoScrollImages() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentImageIndex++;
                if (currentImageIndex >= storiesEntities.size())
                    currentImageIndex = 0;

                viewPager.setCurrentItem(currentImageIndex, true);

                setupFocusDot() ;

                handler.postDelayed(this, 3000);
            }
        }, 3000) ;
    }


    private void addViewPager(){
        pagerAdapter = new MyPagerAdapter(context, pageClickedListener) ;
        viewPager.setAdapter(pagerAdapter);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT) ;
        this.addView(viewPager, layoutParams);
    }

    private void addDotView() {
        dotViewContainer = new LinearLayout(context);
        dotViewContainer.setOrientation(LinearLayout.HORIZONTAL);
        dotViewContainer.setPadding(DensityUtil.px2dip(context, 10), DensityUtil.px2dip(context, 10), DensityUtil.px2dip(context, 10), DensityUtil.px2dip(context, 10));

        for (int i = 0; i < storiesEntities.size(); i++) {
            ImageView imageView = new ImageView(context) ;
            imageView.setImageResource(R.drawable.dot_blur);
            imageView.setPadding(0, 0, DensityUtil.px2dip(context, 10), DensityUtil.px2dip(context, 10));
            imageView.setTag(i);
            dotViewContainer.addView(imageView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT) ;
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT | Gravity.END ;
        this.addView(dotViewContainer, layoutParams);
    }


    public void setImageRes(List<Integer> imageRes){

    }



    /*
    预先获取图片
     */
    private void fetchImages() {
        for (LatestNews.TopStoriesEntity entity : storiesEntities)
            Picasso.with(context).load(entity.getImage()).fetch();
    }




    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //do nothing
    }

    @Override
    public void onPageSelected(int position) {
        currentImageIndex = position ;
        setupFocusDot();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //do nothing
    }


    class MyPagerAdapter extends PagerAdapter{

        private Context context ;
        private OnPageClickedListener listener ;

        public MyPagerAdapter(Context context, OnPageClickedListener listener) {
            this.context = context ;
            this.listener = listener ;
        }
        @Override
        public int getCount() {
            return storiesEntities.size() ;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            FrameLayout frameLayout = new FrameLayout(context) ;
            ImageView imageView = new ImageView(context) ;
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                        listener.pageClicked(storiesEntities.get(currentImageIndex).getId());
                }
            });
            Picasso.with(context)
                    .load(storiesEntities.get(position).getImage())
                    .fit()
                    .into(imageView);
            frameLayout.addView(imageView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


            TextView textView = new TextView(context) ;
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            textView.setMaxLines(2);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setText(storiesEntities.get(position).getTitle());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM) ;
            layoutParams.setMargins(DensityUtil.px2dip(context, 40), DensityUtil.px2dip(context, 40), DensityUtil.px2dip(context, 40), DensityUtil.px2dip(context, 40));

            frameLayout.addView(textView, layoutParams);

            container.addView(frameLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return frameLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }
}
