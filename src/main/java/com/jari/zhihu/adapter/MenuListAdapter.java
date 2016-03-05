package com.jari.zhihu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jari.zhihu.R;
import com.jari.zhihu.entity.Theme;

import java.util.List;

/**
 * Created by hero on 2016/1/17 0017.
 */
public class MenuListAdapter extends ArrayAdapter<Theme.OthersEntity> {

    LayoutInflater inflater ;
    int resID ;

    public MenuListAdapter(Context context, int resource, List<Theme.OthersEntity> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context) ;
        this.resID = resource ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null ;
        if(convertView == null){
            convertView = inflater.inflate(resID, parent, false) ;
            viewHolder = new ViewHolder() ;
            viewHolder.themeName = (TextView) convertView.findViewById(R.id.item_menu_name);
            viewHolder.themeSub = (ImageView) convertView.findViewById(R.id.item_menu_sub);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.themeName.setText(getItem(position).getName());
        viewHolder.themeSub.setImageResource(R.drawable.menu_arrow);

        return convertView ;
    }



    private static class ViewHolder {
        TextView themeName ;
        ImageView themeSub ;
    }
}
