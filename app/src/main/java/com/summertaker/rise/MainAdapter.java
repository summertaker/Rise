package com.summertaker.rise;

import android.content.Context;
import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.summertaker.rise.common.BaseApplication;
import com.summertaker.rise.common.Config;
import com.summertaker.rise.data.Item;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ItemViewHolder> {

    private Context mContext;
    private Resources mResources;
    private ArrayList<Item> mItems;

    public MainAdapter(Context context, ArrayList<Item> items) {
        this.mContext = context;
        this.mResources = mContext.getResources();
        this.mItems = items;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView tvFavorite;
        public TextView tvId;
        public TextView tvName;
        public TextView tvPrice;
        public TextView tvRof;
        public TextView tvVot;
        public ImageView ivChart;

        private ItemViewHolder(View view) {
            super(view);

            tvFavorite = view.findViewById(R.id.tvFavorite);
            tvId = view.findViewById(R.id.tvId);
            tvName = view.findViewById(R.id.tvName);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvRof = view.findViewById(R.id.tvRof);
            tvVot = view.findViewById(R.id.tvVot);
            ivChart = view.findViewById(R.id.ivChart);

            //view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            //contextMenu.setHeaderTitle(R.string.tag);
            contextMenu.add(1, this.getAdapterPosition(), 0, mResources.getString(R.string.favorite));
        }
    }

    @Override
    public MainAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_row, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = mItems.get(position);

        // 즐겨찾기
        if (item.isFavorite()) {
            holder.tvFavorite.setText(mResources.getString(R.string.favorite_on));
            holder.tvFavorite.setTextColor(mResources.getColor(R.color.favorite_on, null));
        } else {
            holder.tvFavorite.setText(mResources.getString(R.string.favorite_off));
            holder.tvFavorite.setTextColor(mResources.getColor(R.color.favorite_off, null));
        }

        // 번호
        String id = item.getId() + ".";
        holder.tvId.setText(id);

        // 종목 이름
        String name = item.getName();
        if (item.getNor() > 0) { // 추천수
            name = name + " (" + item.getNor() + ")";
        }
        holder.tvName.setText(name);

        // 가격
        BaseApplication.getInstance().renderPrice(item, holder.tvPrice);

        // 등락률
        BaseApplication.getInstance().renderRof(item, holder.tvRof);

        // 거래량
        String vot = Config.NUMBER_FORMAT.format(item.getVot());
        holder.tvVot.setText(vot);

        // 차트
        Glide.with(mContext).load(item.getChartUrl()).apply(new RequestOptions()).into(holder.ivChart);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Item item, int position) {
        mItems.add(position, item);
        notifyItemInserted(position);
    }
}

