package com.summertaker.rise;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.summertaker.rise.common.Config;
import com.summertaker.rise.common.DataManager;
import com.summertaker.rise.data.Item;
import com.summertaker.rise.util.RecyclerTouchListener;
import com.summertaker.rise.util.SwipeToDeleteCallback;
import com.summertaker.rise.util.Util;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String mTitle;

    protected MenuItem mMenuItemChart;
    protected MenuItem mMenuItemFavorite;

    private ArrayList<Item> mItems = new ArrayList<>();
    private ArrayList<Item> mItemsAll = new ArrayList<>();
    private MainAdapter mAdapter;
    private RecyclerView mRecyclerView;
    //private CoordinatorLayout coordinatorLayout;
    private DividerItemDecoration mDividerItemDecoration;

    private DataManager mDataManager;
    private boolean mYearChartMode = false;
    private boolean mFavoriteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTop();
            }
        });
        mTitle = toolbar.getTitle().toString();

        mAdapter = new MainAdapter(getApplicationContext(), mItems);
        mAdapter.setHasStableIds(true);

        //coordinatorLayout = findViewById(R.id.constraintLayout);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Item item = mItems.get(position);
                //Toast.makeText(mContext, item.getName(), Toast.LENGTH_SHORT).show();

                //Intent intent = new Intent(mContext, DetailActivity.class);
                //intent.putExtra("code", item.getCode());
                //intent.putExtra("nor", item.getNor());
                //startActivityForResult(intent, Config.ACTIVITY_REQUEST_CODE);

                Util.startKakaoStockDeepLink(MainActivity.this, item.getCode());
            }

            @Override
            public void onLongClick(View view, int position) {
                Item item = mItems.get(position);
                if (item.isFavorite()) {
                    item.setFavorite(false);
                } else {
                    item.setFavorite(true);
                }
                mAdapter.notifyDataSetChanged();
                mDataManager.writeFavorites(mItems);

                //Util.startKakaoStockDeepLink(mContext, mItems.get(position).getCode());
                //mDataManager.updateMyItem(mItems.get(position).getCode(), Config.KEY_FAVORITES);
                //mEventListener.onFragmentEvent(Config.PARAM_DATA_CHANGED);
            }
        }));

        mDividerItemDecoration = new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL);

        /*
        // 스와이프 방법 1) 좌우 스와이프 (ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT)
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                Item item = mItems.get(position);
                //mAdapter.removeItem(position);

                if (direction == ItemTouchHelper.LEFT) {
                    mItems.remove(viewHolder.getLayoutPosition());
                    //mAdapter.notifyItemRemoved(viewHolder.getLayoutPosition());
                    //mAdapter.restoreItem(item, position);
                    //mRecyclerView.scrollToPosition(position);
                    item.setFavorite(true);
                    mItems.add(viewHolder.getLayoutPosition(), item);
                    mAdapter.notifyDataSetChanged();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Show edit dialog
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        */

        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenuItemChart = menu.findItem(R.id.action_chart);
        mMenuItemFavorite = menu.findItem(R.id.action_favorite);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_chart) {
            toggleChart();
            return true;
        } else if (id == R.id.action_favorite) {
            toggleFavorite();
            return true;
        } else if (id == R.id.action_clear) {
            clearFavorite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        //Toast.makeText(getApplicationContext(), "> " + menuItem.getItemId(), Toast.LENGTH_SHORT).show();
        //Log.e(">>", menuItem.getGroupId() + ", " + menuItem.getItemId());

        int itemId = menuItem.getItemId();
        Item item = mItems.get(itemId);
        if (item.isFavorite()) {
            item.setFavorite(false);
        } else {
            item.setFavorite(true);
        }
        mAdapter.notifyDataSetChanged();
        mDataManager.writeFavorites(mItems);

        //Toast.makeText(mContext, item.getCode() + " " + item.getTagIds(), Toast.LENGTH_SHORT).show();
        //Log.e(TAG, item.getCode() + " / " + item.getTagIds());

        return super.onContextItemSelected(menuItem);
    }

    private void loadData() {
        mDataManager = new DataManager(getApplicationContext());
        mDataManager.setOnFlucLoaded(new DataManager.FluctuationCallback() {
            @Override
            public void onParse(int count) {

            }

            @Override
            public void onLoad(ArrayList<Item> items) {
                //Log.e(TAG, "items.size(): " + items.size());
                mItemsAll.clear();
                mItems.clear();

                ArrayList<Item> favorites = mDataManager.readFavorites();

                long id = 1;
                long millis = System.currentTimeMillis();
                String chartPrefix = "https://fn-chart.dunamu.com/images/kr/candle/d/"; // d:일, w:주, m:월
                String chartSuffix = ".png?" + millis;
                for (Item item : items) {
                    if (item.getName().contains("ETN")
                            || item.getName().contains("ARIRANG")
                            || item.getName().contains("KINDEX")
                            || item.getName().contains("KODEX")
                            || item.getName().contains("KOSEF")
                            || item.getName().contains("TIGER")
                            || item.getName().contains("1호")
                            || item.getName().contains("2호")
                            || item.getName().contains("3호")
                            || item.getName().contains("레버리지")
                            || item.getName().contains("배당주")
                            || item.getName().contains("인버스")
                            || item.getName().contains("호스팩")
                    ) {
                        continue;
                    }
                    item.setId(id);
                    for (Item favorite : favorites) {
                        if (favorite.getCode().equals(item.getCode())) {
                            item.setFavorite(true);
                            break;
                        }
                    }
                    //String chartCode = item.getCode();
                    //if (item.getName().contains("ETN")) {
                    //    chartCode = "Q" + chartCode;
                    //    item.setCode("Q" + item.getCode());
                    //    Log.e("- ", item.getName() + " " + item.getCode() + " " + item.getChartUrl());
                    //} else {
                    //    chartCode = "A" + chartCode;
                    //}
                    item.setChartUrl(chartPrefix + "A" + item.getCode() + chartSuffix);
                    mItems.add(item);
                    id++;
                }
                mItemsAll.addAll(mItems);
                renderData();
            }
        });

        mDataManager.loadFluctuation(Config.KEY_FLUCTUATION_RISE);
    }

    private void renderData() {
        LinearLayout loLoading = findViewById(R.id.loLoading);
        loLoading.setVisibility(View.GONE);

        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.removeItemDecoration(mDividerItemDecoration);
        //if (!mChartMode) {
        //    mRecyclerView.addItemDecoration(mDividerItemDecoration); // Divider
        //}

        //hideBaseProgress();
        mAdapter.notifyDataSetChanged();

        // Activity 제목에 갯수 출력하기
        //mEventListener.onFragmentItemSizeChange(mPosition, mItems.size());
        //mEventListener.onFragmentEvent(Config.PARAM_LOAD_FINISHED);

        // 스와이프 방법 2) 좌 스와이프 + 배경 아이콘
        //enableSwipeToDeleteAndUndo();

        setTitle();
    }

    private void toggleChart() {
        mYearChartMode = !mYearChartMode;
        int icon = mYearChartMode ? R.drawable.baseline_insert_chart_white_24 : R.drawable.baseline_insert_chart_outlined_white_24;
        mMenuItemChart.setIcon(ContextCompat.getDrawable(getApplicationContext(), icon));

        // d:일, w:주, m:월
        String url = mYearChartMode ? "https://fn-chart.dunamu.com/images/kr/candle/m/A" : "https://fn-chart.dunamu.com/images/kr/candle/d/A";
        long millis = System.currentTimeMillis();
        for (Item item : mItems) {
            String chartUrl = url + item.getCode() + ".png?" + millis;
            item.setChartUrl(chartUrl);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void toggleFavorite() {
        mFavoriteMode = !mFavoriteMode;

        int icon = mFavoriteMode ? R.drawable.baseline_star_white_24 : R.drawable.baseline_star_border_white_24;
        mMenuItemFavorite.setIcon(ContextCompat.getDrawable(getApplicationContext(), icon));

        mItems.clear();
        if (mFavoriteMode) {
            for (Item item : mItemsAll) {
                if (item.isFavorite()) {
                    mItems.add(item);
                }
            }
        } else {
            mItems.addAll(mItemsAll);
        }
        mAdapter.notifyDataSetChanged();
        setTitle();
    }

    private void clearFavorite() {
        for (Item item : mItems) {
            item.setFavorite(false);
        }
        if (mFavoriteMode) {
            //Toast.makeText(getApplicationContext(), mFavoriteMode + "", Toast.LENGTH_SHORT).show();
            toggleFavorite();
        } else {
            mAdapter.notifyDataSetChanged();
        }
        mDataManager.writeFavorites(mItems);
    }

    public void goTop() {
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (manager != null) {
            manager.scrollToPositionWithOffset(0, 0);
        }
    }

    public void setTitle() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String title = mTitle + " (" + Config.NUMBER_FORMAT.format(mItems.size()) + ")";
            actionBar.setTitle(title);
        }
    }


    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                //final String item = mAdapter.getData().get(position);
                final Item item = mItems.get(position);
                mAdapter.removeItem(position);

                //Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                //snackbar.setAction("UNDO", new View.OnClickListener() {
                //    @Override
                //    public void onClick(View view) {
                        //mAdapter.restoreItem(item, position);
                        //mRecyclerView.scrollToPosition(position);
                //    }
                //});
                //snackbar.setActionTextColor(Color.YELLOW);
                //snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRecyclerView);
    }
}
