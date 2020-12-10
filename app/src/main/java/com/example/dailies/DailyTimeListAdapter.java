package com.example.dailies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DailyTimeListAdapter extends RecyclerView.Adapter<DailyTimeListAdapter.DailyTimeViewHolder>
{
    private static final String LOG  = "-> DailyTimeListAdapter";

    private final LayoutInflater inflater;
    private List<DailyTime> dailyTimes;

    private Context con;
    private DailyTime dailyTime = null;

    public DailyTimeListAdapter(Context con)
    {
        this.con = con;
        inflater = LayoutInflater.from(con);
    }
    /* VIEW HOLDER START */
    public class DailyTimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private String LOG = "------> DailyTimeViewHolder";

        private final CheckBox dailyTimeCheckbox;

        // in the middle part holder
        private final TextView dailyIntervalTitle;
        private final TextView dailyIntervalDesc;
        private final TextView dailyTimeTextView;
        // in the middle part holder

        private final Switch dailyTimeSwitch;

        public DailyTimeViewHolder(@NonNull View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            dailyTimeCheckbox = itemView.findViewById(R.id.delete_checkbox);

            // in the middle part holder
            dailyIntervalTitle = itemView.findViewById(R.id.interval_list_item_title);
            dailyIntervalDesc = itemView.findViewById(R.id.interval_list_item_desc);
            dailyTimeTextView = itemView.findViewById(R.id.time_list_item_time);
            // in the middle part holder

            dailyTimeSwitch = itemView.findViewById(R.id.time_list_item_switch);
        }

        @Override
        public void onClick(View v)
        {
            Log.d(LOG, "you tapped a single time");
        }
    }
    /* VIEW HOLDER END */

    //////////////////////////////////
    @NonNull
    @Override
    public DailyTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = inflater.inflate(R.layout.time_list_item, parent, false);
        return new DailyTimeViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull DailyTimeViewHolder holder, final int position)
    {
        if(dailyTimes != null)
        {
            dailyTime = dailyTimes.get(position);

            if(dailyTime.getIsTime())
            {
                holder.dailyIntervalTitle.setVisibility(View.GONE);
                holder.dailyIntervalDesc.setVisibility(View.GONE);

                holder.dailyTimeTextView.setVisibility(View.VISIBLE);
                holder.dailyTimeTextView.setText(dailyTime.toString());
            }
            else
            {
                holder.dailyTimeTextView.setVisibility(View.GONE);
                holder.dailyTimeTextView.setText("");

                holder.dailyIntervalTitle.setVisibility(View.VISIBLE);
                holder.dailyIntervalDesc.setVisibility(View.VISIBLE);

                switch(dailyTime.getIntervalType())
                {
                    case ONE_HR:
                        holder.dailyIntervalTitle.setText(R.string.intervalone);
                        holder.dailyIntervalDesc.setText(R.string.intervalonedesc);
                        break;
                    case THREE_HR:
                        holder.dailyIntervalTitle.setText(R.string.intervalthree);
                        holder.dailyIntervalDesc.setText(R.string.intervalthreedesc);
                        break;
                    case SIX_HR:
                        holder.dailyIntervalTitle.setText(R.string.intervalsix);
                        holder.dailyIntervalDesc.setText(R.string.intervalsizdesc);
                        break;
                }
                // L T R B
                // does work but really not necessary
                // holder.dailyIntervalDesc.setPadding(0, 0, 0, 100);
            }
            holder.dailyTimeCheckbox.setVisibility(determineIfCheckBoxIsVisible(dailyTime.isCheckBoxVisible()));
            holder.dailyTimeCheckbox.setChecked(dailyTime.isCheckBoxChecked());
            holder.dailyTimeSwitch.setChecked(dailyTime.isSwitchActivated());
        }
        else
        {
            Log.e(LOG, "SOMETHING WENT WRONG!");
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                ((DailyActivity)con).deleteTime();
                return true;
            }
        });
        holder.dailyTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                dailyTimes.get(position).setSwitchActivated(isChecked);
            }
        });
        holder.dailyTimeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                dailyTimes.get(position).setCheckBoxChecked(isChecked);
            }
        });
    }
    public int determineIfCheckBoxIsVisible(boolean visible)
    {
        if(visible)
        {
            return View.VISIBLE;
        }
        else return View.GONE;
    }

    @Override
    public int getItemCount()
    {
        if(dailyTimes != null)
        {
            return dailyTimes.size();
        }
        else return 0;
    }
    public List<DailyTime> getDailyTimes()
    {
        return this.dailyTimes;
    }
    void setDailyTimes(List<DailyTime> dailyTimes)
    {
        this.dailyTimes = dailyTimes;
        notifyDataSetChanged();
    }
    public DailyTime getDailyAtPosition(int position)
    {
        return dailyTimes.get(position);
    }
}
