package com.soya.launcher.ui.fragment;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;

import com.soya.launcher.R;
import com.soya.launcher.adapter.LanguageAdapter;
import com.soya.launcher.bean.Language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public abstract class AbsLanguageFragment extends AbsFragment implements View.OnClickListener {

    private VerticalGridView mContentGrid;
    private View mNextView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_set_language;
    }

    @Override
    protected void init(View view, LayoutInflater inflater) {
        super.init(view, inflater);
        mContentGrid = view.findViewById(R.id.content);
        mNextView = view.findViewById(R.id.next);
        setContent(view, inflater);
    }

    @Override
    protected void initBefore(View view, LayoutInflater inflater) {
        super.initBefore(view, inflater);
        mNextView.setOnClickListener(this);
    }

    private void setContent(View view, LayoutInflater inflater){
        List<Language> list = new ArrayList<>();
        LanguageAdapter adapter = new LanguageAdapter(getActivity(), inflater, list);
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(adapter);
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter(arrayObjectAdapter);
        mContentGrid.setAdapter(itemBridgeAdapter);
        mContentGrid.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        String[] locales = Resources.getSystem().getAssets().getLocales();
        Set<String> strings = new HashSet<>();
        for (int i = 0; i < locales.length; i++) {
            Locale locale = Locale.forLanguageTag(locales[i]);
            String language = locale.getLanguage();
            String name = locale.getDisplayName(locale);
            if (!strings.contains(language) || language.equals("en") || language.equals("zh")) list.add(new Language(name, locale.getDisplayLanguage(), locale));
            strings.add(language);
        }


        /*List<MyLocaleInfo> localeInfos = SystemUtils.getSystemLocaleInfos(getActivity(), false);
        for (int i = 0; i < localeInfos.size(); i++){
            Locale locale = localeInfos.get(i).getLocale();
            String name = locale.getDisplayName(locale);
            list.add(new Language(name, locale.getDisplayLanguage(), locale));
        }*/

        Collections.sort(list, new Comparator<Language>() {
            @Override
            public int compare(Language o1, Language o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        int selectLauncher = 0;
        int selectCountry = 0;
        Locale locale = Locale.getDefault();
        for (int i = 0; i < list.size(); i++){
            Locale item = list.get(i).getLanguage();
            if (locale.getLanguage().equals(item.getLanguage())){
                selectLauncher = i;
                if (locale.getCountry().equals(item.getCountry())){
                    selectCountry = i;
                }
            }
        }
        int select = selectCountry != 0 ? selectCountry : selectLauncher;
        adapter.setSelect(select);

        adapter.setCallback(newCallback(arrayObjectAdapter));

        arrayObjectAdapter.addAll(0, list);
        mContentGrid.setSelectedPosition(select);
    }

    public LanguageAdapter.Callback newCallback(ArrayObjectAdapter adapter){
        return new LanguageAdapter.Callback() {
            @Override
            public void onClick(Language bean) {
                onSelectLanguage(bean);
                adapter.notifyArrayItemRangeChanged(0, adapter.size());
            }
        };
    }

    @Override
    protected int getWallpaperView() {
        return R.id.wallpaper;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mNextView)){
            onNext();
        }
    }

    protected void showNext(boolean show){
        mNextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    protected void onNext(){}

    protected void onSelectLanguage(Language bean){}
}
