package com.soya.launcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.soya.launcher.R;
import com.soya.launcher.bean.Language;

import java.util.List;

public class LanguageAdapter extends Presenter {
    private final Context context;
    private final LayoutInflater inflater;
    private Callback callback;
    private final List<Language> dataList;
    private int select;

    public LanguageAdapter(Context context, LayoutInflater inflater, List<Language> dataList){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new Holder(inflater.inflate(R.layout.holder_language, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Holder holder = (Holder) viewHolder;
        holder.bind((Language) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        Holder holder = (Holder) viewHolder;
        holder.unbind();
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public class Holder extends ViewHolder {
        private final TextView mTitleView;
        private final TextView mDescView;
        private final ImageView mCheckView;

        public Holder(View view) {
            super(view);
            mTitleView = view.findViewById(R.id.title);
            mDescView = view.findViewById(R.id.desc);
            mCheckView = view.findViewById(R.id.check);
        }

        public void bind(Language bean){
            boolean isSelect = dataList.indexOf(bean) == select;
            mTitleView.setText(bean.getName());
            mDescView.setText(bean.getDesc());
            mCheckView.setVisibility(isSelect ? View.VISIBLE : View.GONE);
            mDescView.setVisibility(isSelect ? View.GONE : View.VISIBLE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select = dataList.indexOf(bean);
                    if (callback != null) callback.onClick(bean);
                }
            });
        }

        public void unbind(){

        }
    }

    public interface Callback{
        void onClick(Language bean);
    }
}
