package com.base.basemodule.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import com.base.basemodule.R;
import com.base.basemodule.presenter.AbstractMvpPersenter;
import com.base.basemodule.presenter.interfaces.IMvpBaseView;
import com.base.basemodule.wedget.MyItemDecoration;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListActivity<V extends IMvpBaseView, P extends AbstractMvpPersenter<V>, D>
        extends BaseActivity<V, P> {

    protected RecyclerView recyclerView;
    protected SmartRefreshLayout refreshLayout;
    protected BaseListAdapter mAdapter;
    protected LinearLayout llMain;

    private int mSkipCount = 0;
    private int mPageSize = 10;

    private boolean isOpenRefresh = false;

    private boolean isOpenLoadMore = false;
    protected int refresh = 0;
    protected List<D> mDatas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = findViewById(R.id.recyclerView);
        llMain = findViewById(R.id.ll_main);
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyItemDecoration());
        mDatas = new ArrayList<>();
        mAdapter = new BaseListAdapter(initItemLayout(), mDatas);
        recyclerView.setAdapter(mAdapter);
        initView();
    }

    public void setOpenRefresh(boolean openRefresh) {
        isOpenRefresh = openRefresh;
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshListener();
                refresh = 1;
            }
        });

    }

    public void setOpenLoadMore(boolean openLoadMore) {
        isOpenLoadMore = openLoadMore;
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                loadMoreListener();
                refresh = 2;
            }
        });
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.layout_list);
    }


    public int getmPage() {
        return mSkipCount;
    }

    public void setmPage(int mPage) {
        this.mSkipCount = mPage;
    }

    public int getmPageSize() {
        return mPageSize;
    }

    public void setmPageSize(int mPageSize) {
        this.mPageSize = mPageSize;
    }


    protected void requestEnd(List<D> list){
        if (list != null) {
            if (refresh == 1) {
                setDatas(list);
                refreshOK();
            } else {
                mDatas.addAll(list);
                loadMoreOK();
                mAdapter.notifyDataSetChanged();
            }
        } else {
            if (refresh == 1) {
                refreshFail();
            } else {
                loadMoreFail();
            }
        }
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    protected void refreshOK() {
        mSkipCount = mDatas.size();
        mPageSize = 10;
    }

    protected void refreshFail() {
    }

    protected void loadMoreOK() {
        mSkipCount = mDatas.size();
    }

    protected void loadMoreFail() {
    }

    public void setDatas(List<D> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        mAdapter.notifyDataSetChanged();
    }

    public LinearLayout getRootView(){
        return llMain;
    }

    /**
     * 初始化子布局
     */
    protected abstract
    @LayoutRes
    int initItemLayout();

    public class BaseListAdapter extends BaseQuickAdapter<D, BaseViewHolder> {

        public BaseListAdapter(int layoutResId, List<D> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, D t) {
            MyHolder(baseViewHolder, t);
        }
    }

    /**
     * 初始化子布局
     */
    protected abstract void refreshListener();

    /**
     * 初始化子布局
     */
    protected abstract void loadMoreListener();

    /**
     * adapter内的处理
     *
     * @param baseViewHolder BaseViewHolder
     * @param t              泛型T
     */
    protected abstract void MyHolder(BaseViewHolder baseViewHolder, D t);

    protected abstract void initView();
}
