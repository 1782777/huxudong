package com.example.teachapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TeamAdapter extends ArrayAdapter<Team> {
    private int resourceId;
    public TeamAdapter(Context context, int textViewResourceId,
                       List<Team> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Team team = getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        ImageView teamImg = (ImageView) view.findViewById(R.id.team_img);
        TextView team_name = (TextView) view.findViewById(R.id.team_name);
        TextView team_score = (TextView) view.findViewById(R.id.team_score);
        teamImg.setImageResource(team.getImageId());
        team_name.setText(team.getName());
        team_score.setText(""+team.getScore());
        return view;
    }
}
