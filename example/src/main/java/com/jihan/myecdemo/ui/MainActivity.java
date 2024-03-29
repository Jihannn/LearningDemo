package com.jihan.myecdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.jihan.mini_core.activities.ProxyActivity;
import com.jihan.mini_core.app.Mini;
import com.jihan.mini_core.delegates.MiniDelegate;
import com.jihan.mini_core.ui.launcher.ILauncherFinish;
import com.jihan.mini_core.ui.launcher.LauncherFlags;
import com.jihan.moni_ec.launcher.SplashFragment;
import com.jihan.moni_ec.main.EcBottomFragment;
import com.jihan.moni_ec.sign.ISignListener;
import com.jihan.moni_ec.sign.SignInFragment;

import qiu.niorgai.StatusBarCompat;

public class MainActivity extends ProxyActivity implements ISignListener, ILauncherFinish {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        StatusBarCompat.translucentStatusBar(this,true);
    }

    @Override
    public MiniDelegate setRootDelegate() {
        return new SplashFragment();
    }

    @Override
    public void onSignInSuccess() {
        Mini.showToast("登陆成功！");
        getSupportDelegate().replaceFragment(new EcBottomFragment(),false);
    }

    @Override
    public void onSignUpSuccess() {
        Mini.showToast("注冊成功！");
        getSupportDelegate().replaceFragment(new SignInFragment(),false);
    }

    @Override
    public void launcherFinish(LauncherFlags flags) {
        switch (flags) {
            case FINISH_SIGN:
                getSupportDelegate().replaceFragment(new EcBottomFragment(),false);
                break;
            case FINISH_NOT_SIGN:
                getSupportDelegate().replaceFragment(new SignInFragment(),false);
                break;
            default:
                break;
        }
    }
}
