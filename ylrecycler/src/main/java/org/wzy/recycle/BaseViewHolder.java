package org.wzy.recycle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class BaseViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mViews;
    private Context mContext;

    public BaseViewHolder(View itemView, Context context) {
        super(itemView);
        mViews = new SparseArray<>();
        mContext = context;
    }

    public BaseViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    public <TView extends View> TView getView(int id) {
        View view = mViews.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            mViews.put(id, view);
        }
        return (TView) view;
    }

    public BaseViewHolder setText(int viewId, CharSequence value) {
        TextView view = getView(viewId);
        view.setText(TextUtils.isEmpty(value) ? "" : value);
        return this;
    }

    public BaseViewHolder setText(int viewId, CharSequence value, int color) {
        TextView view = getView(viewId);
        if (color == 1) {
            view.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (color == 2) {
            view.setTextColor(Color.parseColor("#000000"));
        } else if (color == 3) {
            view.setTextColor(Color.parseColor("#1B8BFF"));
        } else if (color == 4) {
            view.setTextColor(Color.RED);
        }
        view.setText(TextUtils.isEmpty(value) ? "" : value);
        return this;
    }


    public BaseViewHolder setImageURI(int viewId, Uri uri) {
        ImageView view = getView(viewId);
        view.setImageURI(uri);
        return this;
    }

    public BaseViewHolder setImageViewByBitmap(int viewId, Bitmap bmp) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bmp);
        return this;
    }

    public BaseViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(textColor);
        return this;
    }

    public BaseViewHolder setTextColorRes(int viewId, int textColorRes) {
        TextView view = getView(viewId);
        view.setTextColor(mContext.getResources().getColor(textColorRes));
        return this;
    }

    public BaseViewHolder setImageResource(int viewId, int imageResId) {
        ImageView view = getView(viewId);
        view.setImageResource(imageResId);
        return this;
    }

    public BaseViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public BaseViewHolder setBackgroundResource(int viewId, int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public BaseViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public BaseViewHolder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = getView(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
        return this;
    }

    public BaseViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public BaseViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = getView(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    public BaseViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = getView(viewId);
        view.setOnLongClickListener(listener);
        return this;
    }

    public BaseViewHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }
}
