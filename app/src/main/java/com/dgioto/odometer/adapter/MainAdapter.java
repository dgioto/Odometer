package com.dgioto.odometer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dgioto.odometer.View.EditActivity;
import com.dgioto.odometer.R;
import com.dgioto.odometer.db.DbConstants;
import com.dgioto.odometer.db.DbManager;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    private final Context context;
    private final List<ListItem> mainArray;
    private final DbManager dbManager;

    public MainAdapter(Context context, DbManager dbManager) {
        this.context = context;
        mainArray = new ArrayList<>();
        this.dbManager = dbManager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_layout, parent, false);
        return new MyViewHolder(view, context, mainArray);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setDate(mainArray.get(position).getTitle(),
                mainArray.get(position).getMeters(),
                mainArray.get(position).getTimes());
    }

    @Override
    public int getItemCount() { return mainArray.size(); }

    public class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        private final TextView tvTitle;
        private final TextView tvMeters;
        private final TextView tvTimes;
        private final Context context;
        private final List<ListItem> mainArray;

        public MyViewHolder(@NonNull View itemView, Context context, List<ListItem> mainArray) {
            super(itemView);
            this.context = context;
            this.mainArray = mainArray;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMeters = itemView.findViewById(R.id.tvMeters);
            tvTimes = itemView.findViewById(R.id.tvTimes);
            itemView.setOnClickListener(this);

            ImageButton delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(v -> removeItem(getAdapterPosition(), dbManager));
        }

        public void setDate(String title, String meters, String times){
            tvTitle.setText(title);
            tvMeters.setText(meters);
            tvTimes.setText(times);
        }

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(context, EditActivity.class);
            intent.putExtra(DbConstants.LIST_ITEM_INTENT, mainArray.get(getAdapterPosition()));
            intent.putExtra(DbConstants.EDIT_STATE, false);
            context.startActivity(intent);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<ListItem> newList){
        mainArray.clear();
        mainArray.addAll(newList);
        notifyDataSetChanged();
    }

    public void removeItem(int pos, DbManager dbManager){
        dbManager.delete(mainArray.get(pos).getId());
        mainArray.remove(pos);
        notifyItemRangeChanged(0, mainArray.size());
        notifyItemRemoved(pos);
    }
}
