package com.base.basemodule.fragment;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.base.basemodule.http.AsyncHttp;

public class BaseDialogFragment extends DialogFragment {

    protected int mHttpCode;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHttpCode != 0) {
            AsyncHttp.stopHttpBySign(mHttpCode);
        }

    }
}
