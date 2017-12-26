package com.xujun.drag;

import android.content.Context;
import android.widget.TextView;

import com.xujun.drag.base.BaseRecyclerAdapter;
import com.xujun.drag.base.BaseRecyclerHolder;

import java.util.List;

public class ItemAdapter extends BaseRecyclerAdapter<String> {

    public ItemAdapter(Context context, List<String> datas) { super(context, R.layout.item_string, datas); }

    @Override  public void convert(BaseRecyclerHolder holder, String item, int position) {
        TextView tv=holder.getView(R.id.tv);
        String s = mDatas.get(position);
        tv.setText(s);
    }
}
