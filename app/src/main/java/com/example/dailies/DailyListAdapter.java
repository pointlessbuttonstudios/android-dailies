package com.example.dailies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DailyListAdapter extends RecyclerView.Adapter<DailyListAdapter.DailyViewHolder>
{
    private final LayoutInflater inflater;
    private List<Daily> dailies;
    private Context con;
    private DailyViewHolder dailyViewHolder;

    private SharedPreferences sharedPreferences;

    public DailyListAdapter(Context con)
    {
        this.con = con;
        inflater = LayoutInflater.from(con);
        sharedPreferences = con.getSharedPreferences(SettingsActivity.sharePrefFile, Context.MODE_PRIVATE);
    }
    /* VIEW HOLDER START */
    public class DailyViewHolder extends RecyclerView.ViewHolder
    {
        private final CheckBox dailyItemView_checkBox;
        private final TextView dailyItemView_title;
        private final TextView dailyItemView_description;

        public DailyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            dailyItemView_checkBox = itemView.findViewById(R.id.delete_daily_checkbox);
            dailyItemView_title = itemView.findViewById(R.id.listitem_title);
            dailyItemView_description = itemView.findViewById(R.id.listitem_desc);
        }
    }
    /* VIEW HOLDER END */

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = inflater.inflate(R.layout.daily_list_item, parent, false);
        dailyViewHolder = new DailyViewHolder(itemView);

        return dailyViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder holder,final int position)
    {
        if(dailies != null)
        {
            Daily daily = dailies.get(position);
            holder.dailyItemView_checkBox.setVisibility(determineIfVisible(daily.isCheckBoxVisible()));
            holder.dailyItemView_checkBox.setChecked(daily.isCheckBoxChecked());

            boolean showDesc = sharedPreferences.getBoolean(SettingsActivity.DESC_SWITCH_TAG, true);

            if(daily.getDescription().length() > 0 && showDesc)
            {
                holder.dailyItemView_description.setVisibility(View.VISIBLE);
                holder.dailyItemView_description.setText(daily.getDescription());
            }
            else
            {
                holder.dailyItemView_description.setVisibility(View.GONE);
            }
            holder.dailyItemView_title.setText(daily.title);
        }
        else
        {
            holder.dailyItemView_checkBox.setVisibility(View.GONE);
            holder.dailyItemView_checkBox.setChecked(false);
            holder.dailyItemView_description.setText("empty desc");
            holder.dailyItemView_title.setText("empty title");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                Intent editDailyIntent = new Intent(con, DailyActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("DAILY", dailies.get(position));
                bundle.putBoolean(DailyActivity.EDIT_TAG, true);

                editDailyIntent.putExtras(bundle);
                con.startActivity(editDailyIntent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                ((MainActivity)con).deleteADaily();
                return true;
            }
        });
        holder.dailyItemView_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                dailies.get(position).setCheckBoxChecked(isChecked);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        if(dailies != null)
        {
            return dailies.size();
        }
        else return 0;
    }
    void setDailies(List<Daily> dailies)
    {
        this.dailies = dailies;
        notifyDataSetChanged();
    }
    public Daily getDailyAtPosition(int position)
    {
        return dailies.get(position);
    }
    public int determineIfVisible(boolean visible)
    {
        if(visible)
        {
            return View.VISIBLE;
        }
        else
        {
            return View.GONE;
        }
    }
    public List<Daily> getDailies()
    {
        return this.dailies;
    }
}