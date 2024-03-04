package com.soya.launcher.ui.activity;

import androidx.fragment.app.Fragment;

import com.soya.launcher.R;
import com.soya.launcher.bean.TypeItem;
import com.soya.launcher.enums.Atts;
import com.soya.launcher.enums.Types;
import com.soya.launcher.ui.fragment.MoviceListFragment;

public class MoviceListActivity extends AbsActivity{
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int getContainerId() {
        return R.id.main_browse_fragment;
    }

    @Override
    public Fragment getFragment() {
        TypeItem type = (TypeItem) getIntent().getSerializableExtra(Atts.BEAN);
        return MoviceListFragment.newInstance(type);
    }
}
