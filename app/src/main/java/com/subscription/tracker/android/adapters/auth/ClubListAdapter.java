package com.subscription.tracker.android.adapters.auth;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.subscription.tracker.android.R;
import com.subscription.tracker.android.entity.response.Club;
import com.subscription.tracker.android.services.utils.ImageUtils;

import java.util.List;

public class ClubListAdapter extends BaseAdapter {

    private List<Club> clubs;
    private Activity context;

    public ClubListAdapter(Activity context, List<Club> clubs) {
        this.context = context;
        this.clubs = clubs;
    }

    public void setClubs(List<Club> clubs) {
        this.clubs = clubs;
    }

    @Override
    public int getCount() {
        return clubs.size();
    }

    @Override
    public Object getItem(int i) {
        return clubs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.row_item_club,null);
            viewHolder.title = view.findViewById(R.id.row_item_club_title);
            viewHolder.subtitle = view.findViewById(R.id.row_item_club_title_alt);
            viewHolder.logo = view.findViewById(R.id.row_item_club_logo);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Club club = clubs.get(i);
        viewHolder.title.setText(club.getClubName());
        viewHolder.subtitle.setText(club.getClubNameAlt());
        viewHolder.logo.setImageDrawable(ImageUtils.resolveImageResource(context, club.getImageName()));
        return view;
    }

    class ViewHolder {
        TextView title;
        TextView subtitle;
        ImageView logo;
    }

}
