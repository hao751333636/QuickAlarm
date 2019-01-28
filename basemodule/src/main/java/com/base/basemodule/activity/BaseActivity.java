package com.base.basemodule.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.base.basemodule.R;
import com.base.basemodule.entity.BaseEventEntity;
import com.base.basemodule.http.AsyncHttp;
import com.base.basemodule.presenter.AbstractMvpPersenter;
import com.base.basemodule.presenter.interfaces.IMvpBaseView;
import com.blankj.utilcode.util.LogUtils;
import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public abstract class BaseActivity<V extends IMvpBaseView, P extends AbstractMvpPersenter<V>>
        extends AppCompatActivity implements IMvpBaseView {

    protected InputMethodManager inputMethodManager;

    public Toolbar toolbar;
    protected TextView titleView;
    protected RelativeLayout rightLayout;
    protected ImageView rightImage;
    protected TextView rightTxt;
    protected ImageView ivTitle;

    private boolean hasTitle = true;
    private boolean hasStatusBar = true;
    private P presenter;

    public int mHttpCode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        ARouter.getInstance().inject(this);
        initPresenter();
        initToolBar();
        EventBus.getDefault().register(this);
        initStatusBar();
    }

    private void initStatusBar() {
        if (hasStatusBar) {
            ImmersionBar.with(this).fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                    .statusBarColor(R.color.colorPrimary)
                    .init();
        }
    }

    protected void setHasStatusBar(boolean b) {
        hasStatusBar = b;
    }


    private void initPresenter() {
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //创建Presenter
        if (presenter == null) {
            presenter = createPresenter();
        }
        if (presenter == null) {
//            throw new NullPointerException("presenter 不能为空!");
        } else {
            //绑定view
            presenter.attachMvpView((V) this);
        }
    }

    private void initToolBar() {
        if (hasTitle) {
            toolbar = findViewById(R.id.toolbar);
            titleView = findViewById(R.id.title);
            rightLayout = findViewById(R.id.right_layout);
            rightImage = findViewById(R.id.right_image);
            rightTxt = findViewById(R.id.right_txt);
            toolbar = findViewById(R.id.toolbar);
            ivTitle = findViewById(R.id.iv_title);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    supportFinishAfterTransition();
                }
            });
        }
    }

    public void setContentView(int layoutId) {
        setContentView(layoutId, true);
    }

    /**
     * 容器模版
     *
     * @param layoutId         内容视图
     * @param isContainerTitle true 带有toolbar的布局容器 false无toolbar 你可以自定义标题实现复杂的title
     */
    protected void setContentView(int layoutId, boolean isContainerTitle) {
        if (isContainerTitle) {
            LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_toolbar, null);
            LayoutInflater.from(this).inflate(layoutId, root);
            super.setContentView(root);
        } else {
            setNoTitle();
            super.setContentView(layoutId);
        }
    }

    protected void setNoTitle() {
        hasTitle = false;
    }

    protected void setTitle(String title) {
        titleView.setText(title);
    }

    public void setTitle(CharSequence title) {
        titleView.setText(title);
    }

    public void setRightImageResource(int resId) {
        rightImage.setVisibility(View.VISIBLE);
        rightTxt.setVisibility(View.GONE);
        rightImage.setImageResource(resId);
    }

    public void setRightText(String text) {
        rightImage.setVisibility(View.GONE);
        rightTxt.setVisibility(View.VISIBLE);
        rightTxt.setText(text);
    }

    protected void showQuestion(View.OnClickListener clickListener) {
        ivTitle.setVisibility(View.VISIBLE);
        ivTitle.setOnClickListener(clickListener);
    }

    public void setRightLayoutClickListener(View.OnClickListener listener) {
        rightLayout.setOnClickListener(listener);
    }

    public RelativeLayout getRightLayout() {
        return rightLayout;
    }

    public TextView getRightTxt() {
        return rightTxt;
    }

    protected void showSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHttpCode != 0) {
            AsyncHttp.stopHttpBySign(mHttpCode);
            LogUtils.e("close http :"+mHttpCode);
        }
        ImmersionBar.with(this).destroy(); //必须调用该方法，防止内存泄漏
        //解除绑定
        if (presenter != null) {
            presenter.detachMvpView();
            presenter = null;
            System.gc();
        }
        EventBus.getDefault().unregister(this);
    }


    /**
     * 1. 设置布局
     */
    protected abstract void setContentView();

    /**
     * 创建Presenter
     *
     * @return 子类自己需要的Presenter
     */
    protected abstract P createPresenter();

    /**
     * 获取Presenter
     *
     * @return 返回子类创建的Presenter
     */
    public P getPresenter() {
        return presenter;
    }

    @SuppressLint("ResourceAsColor")
    protected void setBarColor(@ColorRes int color) {
        toolbar.setBackgroundColor(color);
    }

    @Subscribe
    public void onEventMainThread(BaseEventEntity eventEntity) {
    }

}
