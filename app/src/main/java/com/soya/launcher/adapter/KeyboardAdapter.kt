package com.soya.launcher.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soya.launcher.R;
import com.soya.launcher.bean.KeyItem;

import java.util.List;

public class KeyboardAdapter extends RecyclerView.Adapter<KeyboardAdapter.Holder> {
    public static final int TYPE_ENG = 0;
    public static final int TYPE_NUM = 1;
    private Context context;
    private LayoutInflater inflater;
    private List<KeyItem> dataList;

    private Callback callback;
    private int type = TYPE_ENG;
    private boolean isUPCase = false;

    public KeyboardAdapter(Context context, LayoutInflater inflater, List<KeyItem> dataList){
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        GridLayoutManager lm = (GridLayoutManager) recyclerView.getLayoutManager();
        lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return dataList.get(position).getSpanSize();
            }
        });
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.holder_keyboard, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public boolean isUPCase() {
        return isUPCase;
    }

    public void setUPCase(boolean UPCase) {
        isUPCase = UPCase;
        notifyItemRangeChanged(0, getItemCount());
    }

    public int getType() {
        return type;
    }

    public void replace(List<KeyItem> list, int type){
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
        this.type = type;
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private ImageView mIconView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text);
            mIconView = itemView.findViewById(R.id.icon);
        }

        public void bind(KeyItem bean){
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mTextView.setSelected(hasFocus);
                    mIconView.setSelected(hasFocus);
                    setIcon(bean);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onClick(bean, mTextView.getText().toString());
                }
            });

            mTextView.setText(isUPCase || bean.getType() == KeyItem.TYPE_SEARCH ? bean.getName().toUpperCase() : bean.getName().toLowerCase());
            mIconView.setVisibility(bean.isUseIcon() ? View.VISIBLE : View.GONE);
            setIcon(bean);
        }

        private void setIcon(KeyItem bean){
            if (!bean.isUseIcon() || bean.getIcon() == -1) return;
            boolean isCase = bean.getType() == KeyItem.TYPE_UPCAST;
            int icon = bean.getIcon();
            if (isCase){
                icon = isUPCase ? R.drawable.baseline_up_case_100 : R.drawable.baseline_lower_cast_100;
            }
            Drawable drawable = context.getResources().getDrawable(icon, Resources.getSystem().newTheme());
            DrawableCompat.setTint(drawable, mIconView.isSelected() ? context.getResources().getColor(R.color.ico_style_3, Resources.getSystem().newTheme()) : context.getResources().getColor(R.color.ico_style_1, Resources.getSystem().newTheme()));
            mIconView.setImageDrawable(drawable);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onClick(KeyItem bean, String text);
    }
}
