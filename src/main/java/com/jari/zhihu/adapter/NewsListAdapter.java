package com.jari.zhihu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jari.zhihu.R;
import com.jari.zhihu.entity.LatestNews;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Set;

/**
 * Created by hero on 2016/2/27 0027.
 */
public class NewsListAdapter extends BaseAdapter {


    private Context context ;
    private List<LatestNews.StoriesEntity> storiesEntities ;
    private Set<Integer> clickedNewsPosition ;
    private Picasso picasso ;

    public NewsListAdapter(Context context, List<LatestNews.StoriesEntity> storiesEntities, Set<Integer> clickedNewsPosition) {
        this.storiesEntities = storiesEntities ;
        this.context = context ;
        this.clickedNewsPosition = clickedNewsPosition ;
        picasso = Picasso.with(context) ;
    }


    @Override
    public int getCount() {
        return storiesEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return storiesEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(storiesEntities.get(position).isStoryItem()){
            return getNewsItemView(position, convertView, parent) ;
        }else {
            return getNewsSummaryView(position, convertView, parent) ;
        }

    }

    private View getNewsSummaryView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_news_list_summary, parent, false) ;
        }
        TextView textView = (TextView) convertView.findViewById(R.id.tv_today_news) ;
        textView.setText(storiesEntities.get(position).getTitle());
        return convertView ;
    }


    private View getNewsItemView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null ;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_news_list, parent, false) ;
            viewHolder = new ViewHolder(convertView) ;
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(storiesEntities.get(position).getTitle());
        Picasso.with(context).load(storiesEntities.get(position).getImages().get(0)).tag(context)
                .fit().into(viewHolder.imageView);

        if(clickedNewsPosition.contains(position)){
            viewHolder.textView.setTextColor(Color.GRAY);
        }else {
            viewHolder.textView.setTextColor(Color.BLACK);
        }
        return convertView ;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        LatestNews.StoriesEntity entity = storiesEntities.get(position);
        return entity.getType() ;
    }


    static class ViewHolder{
        public TextView textView ;
        public ImageView imageView ;
        public ViewHolder(View convertView){
            textView = (TextView) convertView.findViewById(R.id.tv_news_title);
            imageView = (ImageView) convertView.findViewById(R.id.iv_news_thumb);
        }
    }
}
