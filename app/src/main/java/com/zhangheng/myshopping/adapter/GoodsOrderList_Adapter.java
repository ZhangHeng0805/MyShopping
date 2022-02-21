package com.zhangheng.myshopping.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhangheng.myshopping.R;
import com.zhangheng.myshopping.bean.shopping.submitgoods.goods;

import java.util.List;

public class GoodsOrderList_Adapter extends BaseAdapter {
    private Context context;
    private List<goods> goodsList;

    private MyOnClick myOnClick;

    public GoodsOrderList_Adapter(Context context, List<goods> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
    }

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public Object getItem(int i) {
        return goodsList.size();
    }

    @Override
    public long getItemId(int i) {
        return goodsList.size();
    }
    public void removegoods(int i){
        if (goodsList.get(i).getNum()>1) {
            goodsList.remove(i);
            notifyDataSetChanged();
        }

    }
    public void isSimlir(int i){
        if (i>0){
            if (goodsList.get(i).getGoods_id().equals(goodsList.get(i-1).getGoods_id())){
                removegoods(i);
            }
        }
    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Holder holder;
        final goods g = goodsList.get(i);
//        isSimlir(i);
        if (view==null){
            holder=new Holder();
            view = View.inflate(context, R.layout.item_list_goods_order,null);
            holder.btn_OK=view.findViewById(R.id.LL_Order_btn_OK);
            holder.btn_NO=view.findViewById(R.id.LL_Order_btn_No);
            holder.name=view.findViewById(R.id.item_goodsmeunlist_txt_goodsname);
            holder.catalog=view.findViewById(R.id.item_goodsmeunlist_txt_goodsintro);
            holder.price=view.findViewById(R.id.item_goodsmeunlist_txt_price);
            holder.num=view.findViewById(R.id.item_goodsmeunlist_txt_num);
            holder.LL_Order_result=view.findViewById(R.id.LL_Order_result);
            view.setTag(holder);
        }else {
            holder= (Holder) view.getTag();
        }

        holder.LL_Order_result.setVisibility(View.GONE);
        String goods_name = g.getGoods_name();
        holder.name.setText(goods_name);
        int state = g.getState();
        String str_state="";

        switch (state){
            case 2:
                holder.catalog.setTextColor(context.getResources().getColor(R.color.green));
                holder.LL_Order_result.setVisibility(View.VISIBLE);
                str_state="待收货";
                break;
            case 1:
                holder.catalog.setTextColor(context.getResources().getColor(R.color.red));
                holder.LL_Order_result.setVisibility(View.VISIBLE);
                holder.btn_OK.setVisibility(View.GONE);
                str_state="拒绝发货";
                break;
            case 0:
                holder.catalog.setTextColor(context.getResources().getColor(R.color.yellow));
                str_state="未处理";
                break;
            case 3:
                holder.catalog.setTextColor(context.getResources().getColor(R.color.blue));
                str_state="已收货";
                break;
            case 4:
                holder.catalog.setTextColor(context.getResources().getColor(R.color.red));
                str_state="退货";
                break;
        }
        holder.catalog.setText(str_state);
        double goods_price = g.getGoods_price();
        holder.price.setText(String.valueOf(goods_price)+"元");

        holder.num.setText(String.valueOf(g.getNum())+"件");
        if (!holder.num.getText().toString().equals("0")){
            holder.num.setTextColor(context.getResources().getColor(R.color.yellow));
        }else {
            holder.num.setTextColor(context.getResources().getColor(R.color.black));
        }
        holder.btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myOnClick.myClick(1,g.getNum(),g.getList_id(),g.getGoods_id());
            }
        });
        holder.btn_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myOnClick.myClick(2,g.getNum(),g.getList_id(),g.getGoods_id());
            }
        });

        return view;
    }
    class Holder{
        TextView name,catalog
                ,num,price;
        LinearLayout LL_Order_result;
        Button btn_OK,btn_NO;

    }
    public interface MyOnClick{
        void myClick(int position, int num, String submit_id, int goods_id);//position 1.确认收货；2.退货
    }
    public void setMyOnClick(MyOnClick onClick){
        this.myOnClick = onClick;
    }
}
