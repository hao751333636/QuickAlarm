package com.base.basemodule.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.base.basemodule.http.AsyncHttp;
import com.base.basemodule.presenter.AbstractMvpPersenter;
import com.base.basemodule.presenter.interfaces.IMvpBaseView;

/**
 * <pre>
 *      author : shihaoyu
 *      e-mail : hao751333636@qq.com
 *      time   : 2018/01/24
 *      desc   :
 *      version: 1.0
 *  </pre>
 */

public abstract class AbstractMvpActivity<V extends IMvpBaseView, P extends AbstractMvpPersenter<V>>
        extends BaseActivity implements IMvpBaseView {

    private P presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建Presenter
        if (presenter == null) {
            presenter = createPresenter();
        }
        if (presenter == null) {
            throw new NullPointerException("presenter 不能为空!");
        }
        //绑定view
        presenter.attachMvpView((V) this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AsyncHttp.stopHttpBySign(100);
        //解除绑定
        if (presenter != null) {
            presenter.detachMvpView();
        }
    }

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
}
