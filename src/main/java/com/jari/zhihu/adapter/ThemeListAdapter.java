package com.jari.zhihu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jari.zhihu.R;
import com.jari.zhihu.entity.ThemeList;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hero on 2016/3/4 0004.
 */
public class ThemeListAdapter extends ArrayAdapter<ThemeList.StoriesEntity> {

    Context context ;
    int resID ;
    private Picasso picasso ;

    public ThemeListAdapter(Context context, List<ThemeList.StoriesEntity> objects) {
        super(context, R.layout.item_news_list, objects);
        this.context = context ;
        this.resID = R.layout.item_news_list ;
        picasso = Picasso.with(context) ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null ;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(resID, parent, false) ;
            viewHolder = new ViewHolder(convertView) ;
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag() ;
        }

        viewHolder.textView.setText(getItem(position).getTitle());

        List<String> images = getItem(position).getImages() ;
        if(images == null || images.size() == 0){
            viewHolder.imageView.setVisibility(View.GONE);
        }else {
            Picasso.with(context).load(images.get(0)).tag(context)
            .fit().into(viewHolder.imageView);
        }

        return convertView ;
    }


    private class ViewHolder{
        ImageView imageView ;
        TextView textView ;
        public ViewHolder(View view){
            imageView = (ImageView)view.findViewById(R.id.iv_news_thumb) ;
            textView = (TextView)view.findViewById(R.id.tv_news_title) ;
        }
    }
}
