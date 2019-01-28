package com.base.basemodule.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.base.basemodule.R;
import com.base.basemodule.entity.SortModel;
import com.base.basemodule.utils.image.ImageUtil;

import java.util.List;

/**
 * @author: xp
 * @date: 2017/7/19
 */

public class SortAdapter extends RecyclerView.Adapter<SortAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<? extends SortModel> mData;
    private Context mContext;

    private boolean mIsSelect = false;

    public SortAdapter(Context context, List<SortModel> data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.mContext = context;
    }

    public SortAdapter(Context context, List<? extends SortModel> data, boolean isSelect) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        mIsSelect = isSelect;
        this.mContext = context;
    }

    @Override
    public SortAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_name, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
        viewHolder.ivHead = view.findViewById(R.id.iv_head);
        viewHolder.ivRadio = view.findViewById(R.id.iv_radio);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SortAdapter.ViewHolder holder, final int position) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });

        }
        SortModel sortModel = mData.get(position);
        if (mIsSelect) {
            holder.ivRadio.setVisibility(View.VISIBLE);
        } else {
            holder.ivRadio.setVisibility(View.INVISIBLE);
        }
        if (sortModel.isSelect()) {
            holder.ivRadio.setBackgroundResource(R.mipmap.ic_choose_sel);
        } else {
            holder.ivRadio.setBackgroundResource(R.mipmap.ic_choose_nor);
        }
        holder.tvName.setText(sortModel.getName());
        ImageUtil.loadCircleImage(mContext, R.mipmap.ic_default_head, holder.ivHead);
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    mOnItemClickListener.onItemClick(holder.itemView, position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public List<? extends SortModel> getmData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //**********************itemClick************************
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    //**************************************************************

    public void refresh(List<SortModel> data) {
        if (mData != null) {
            mData.clear();
            mData = data;
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivHead;
        ImageView ivRadio;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 提供给Activity刷新数据
     *
     * @param list
     */
    public void updateList(List<SortModel> list) {
        this.mData = list;
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return mData.get(position).getLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mData.get(i).getLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

}
