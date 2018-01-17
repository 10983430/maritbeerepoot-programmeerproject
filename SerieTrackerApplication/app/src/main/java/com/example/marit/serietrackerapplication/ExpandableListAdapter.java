package com.example.marit.serietrackerapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Marit on 16-1-2018.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listData;
    private HashMap<String, List<Episode>> listHashMap;

    public ExpandableListAdapter(Context context, List<String> listData, HashMap<String, List<Episode>> listHashMap) {
        this.context = context;
        this.listData = listData;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {
        return listData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(listData.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listData.get(groupPosition);

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listData.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_layout_users, null);
        }
        TextView viewtje = convertView.findViewById(R.id.usernameHolder);
        viewtje.setText(title);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //final String childie = (String) getChild(groupPosition, childPosition);
        Log.d("Hoiii", "lol");
        Object childie = getChild(groupPosition, childPosition);
        //Episode child = getChild(groupPosition, childPosition);
        Log.d("Hoiiiii", childie.toString());
        //Episode lol = new Episode(childie);
        //Episode lol = new Episode(childie);
        Log.d("Hoiii", "lol");
        Log.d("Hoi", getChild(groupPosition, childPosition).toString());
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_layout_expandable_child, null);
        }
        TextView viewtje = convertView.findViewById(R.id.textholder);
        //viewtje.setText(childie);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }
}
