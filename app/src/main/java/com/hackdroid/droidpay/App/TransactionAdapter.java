package com.hackdroid.droidpay.App;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.hackdroid.droidpay.Model.Transaction;
import com.hackdroid.droidpay.R;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.Holder> {
  Context context   ;
  List<Transaction> list;

    public TransactionAdapter(Context context, List<Transaction> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_transaction , parent ,false);
        return new TransactionAdapter.Holder(layout);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String type = list.get(position).getType();
        if (type.equals("1")){
            holder.amount.setText("+"+list.get(position).getAmount());
            holder.amount.setTextColor(context.getColor(R.color.success_green_back));
            holder.imageView.setImageDrawable(context.getDrawable(R.drawable.rupees_add));
        }else{
            holder.amount.setText("-"+list.get(position).getAmount());
            holder.amount.setTextColor(context.getColor(R.color.danger_grad_end));
            holder.imageView.setImageDrawable(context.getDrawable(R.drawable.ruppes_sub));
        }

        holder.date.setText(list.get(position).getDate());
        holder.src.setText(list.get(position).getSrc());
        holder.uid.setText("Ref:"+list.get(position).getUid());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView date , amount , src , uid ;
        ImageView imageView ;
        public Holder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.trans_date);
            amount = itemView.findViewById(R.id.trans_amount);
            src  = itemView.findViewById(R.id.src);
            uid = itemView.findViewById(R.id.uid);
            imageView = itemView.findViewById(R.id.trns_type_img);
        }
    }
}
