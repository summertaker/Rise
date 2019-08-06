package com.summertaker.rise;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.summertaker.rise.common.Config;
import com.summertaker.rise.common.DataManager;
import com.summertaker.rise.data.Item;
import com.summertaker.rise.util.RecyclerTouchListener;
import com.summertaker.rise.util.Util;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String mTitle;

    protected MenuItem mMenuItemYearChart;
    protected MenuItem mMenuItemTodayChart;
    protected MenuItem mMenuItemFavorite;

    private ArrayList<Item> mItems = new ArrayList<>();
    private ArrayList<Item> mItemsAll = new ArrayList<>();
    private MainAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private DividerItemDecoration mDividerItemDecoration;

    private DataManager mDataManager;

    private boolean mYearChartMode = false;
    private boolean mTodayChartMode = false;
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

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mFavoriteMode) {
                    toggleFavorite();
                }
                mItemsAll.clear();
                mItems.clear();
                loadData();
            }
        });

        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenuItemYearChart = menu.findItem(R.id.action_year_chart);
        mMenuItemTodayChart = menu.findItem(R.id.action_today_chart);
        mMenuItemFavorite = menu.findItem(R.id.action_favorite);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_year_chart) {
            toggleYearChart();
            return true;
        } else if (id == R.id.action_today_chart) {
            toggleTodayChart();
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

        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void toggleYearChart() {
        mYearChartMode = !mYearChartMode;
        int icon = mYearChartMode ? R.drawable.baseline_bar_chart_white_24 : R.drawable.baseline_trending_up_white_24;
        mMenuItemYearChart.setIcon(ContextCompat.getDrawable(getApplicationContext(), icon));

        mTodayChartMode = false;
        mMenuItemTodayChart.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_terrain_white_24));

        // d:일, w:주, m:월
        String url = "https://fn-chart.dunamu.com/images/kr/candle/d/A";
        if (mYearChartMode) {
            url = "https://fn-chart.dunamu.com/images/kr/candle/m/A";
        }
        long millis = System.currentTimeMillis();
        for (Item item : mItems) {
            String chartUrl = url + item.getCode() + ".png?" + millis;
            item.setChartUrl(chartUrl);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void toggleTodayChart() {
        mTodayChartMode = !mTodayChartMode;
        int icon = mTodayChartMode ? R.drawable.baseline_bar_chart_white_24 : R.drawable.baseline_terrain_white_24;
        mMenuItemTodayChart.setIcon(ContextCompat.getDrawable(getApplicationContext(), icon));

        mYearChartMode = false;
        mMenuItemYearChart.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_trending_up_white_24));

        // d:일, w:주, m:월
        String url = "https://fn-chart.dunamu.com/images/kr/candle/d/A";
        if (mTodayChartMode) {
            url = "https://t1.daumcdn.net/finance/chart/kr/daumstock/d/A";
        }
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
        goTop();
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
}
