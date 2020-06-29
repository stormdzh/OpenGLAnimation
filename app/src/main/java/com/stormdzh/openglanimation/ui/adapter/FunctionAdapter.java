package com.stormdzh.openglanimation.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stormdzh.openglanimation.R;
import com.stormdzh.openglanimation.entity.common.FunctionEntity;

import java.util.List;

/**
 * @Description: 功能适配器
 * @Author: dzh
 * @CreateDate: 2020-06-16 16:49
 */
public class FunctionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<FunctionEntity> mFunctionList;

    public FunctionAdapter(Context context, List<FunctionEntity> functionList) {
        this.mContext = context;
        this.mFunctionList = functionList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FunViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_function_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FunViewHolder viewHolder = (FunViewHolder) holder;
        final FunctionEntity functionEntity = mFunctionList.get(position);

        viewHolder.tvName.setText(functionEntity.name);
//        viewHolder.itemView.setBackgroundColor(position % 2 == 0 ? Color.parseColor("#f4f4f4") : Color.parseColor("#6C7B8B"));
        viewHolder.itemView.setBackgroundResource(position % 2 == 0?R.drawable.selector_test:R.drawable.selector_test2);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, functionEntity.target));
            }
        });


    }

    @Override
    public int getItemCount() {
        return mFunctionList == null ? 0 : mFunctionList.size();
    }

    class FunViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public FunViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
