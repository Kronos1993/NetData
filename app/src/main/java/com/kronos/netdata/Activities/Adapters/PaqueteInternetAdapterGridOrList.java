package com.kronos.netdata.Activities.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.kronos.netdata.Domain.PaqueteInternet;
import com.kronos.netdata.R;

/**
 * Created by farah on 02/oct/2019.
 */
public class PaqueteInternetAdapterGridOrList extends ListAdapter<PaqueteInternet, PaqueteInternetAdapterGridOrList.PaqueteInternetViewHolder> {

    private Context context;
    private boolean list;

    private OnItemClickListener onItemClickListener;

    private static final DiffUtil.ItemCallback<PaqueteInternet> diffCallback = new DiffUtil.ItemCallback<PaqueteInternet>() {
        @Override
        public boolean areItemsTheSame(@NonNull PaqueteInternet oldItem, @NonNull PaqueteInternet newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull PaqueteInternet oldItem, @NonNull PaqueteInternet newItem) {
            return oldItem.equals(newItem);
        }
    };

    public PaqueteInternetAdapterGridOrList(Context context,boolean list) {
        super(diffCallback);
        this.context = context;
        this.list=list;
    }

    @NonNull
    @Override
    public PaqueteInternetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (list)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_paquete_row,parent,false);
        else
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_paquete_row_grid,parent,false);
        return new PaqueteInternetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PaqueteInternetViewHolder holder, int position) {
        PaqueteInternet paqueteInternet = getItem(position);
        holder.descripcion.setText(String.format(context.getString(R.string.pqt_descripcion1),paqueteInternet.getPaquete())+String.format(context.getString(R.string.pqt_descripcion2),paqueteInternet.getId()));
        holder.nombrePlan.setText(paqueteInternet.getPaquete());
        if(paqueteInternet.getId()==35 || paqueteInternet.getId()==45 || paqueteInternet.getId()==4 || paqueteInternet.getId()==8){
            holder.descripcion.setText(String.format(context.getString(R.string.pqt_descripcion_no_bono),paqueteInternet.getId()));
        }else if(paqueteInternet.getId()==1){
            holder.descripcion.setText(String.format(context.getString(R.string.pqt_diario_descripcion),paqueteInternet.getId()));
        }else if(paqueteInternet.getId()==5){
            holder.descripcion.setText(String.format(context.getString(R.string.pqt_descripcion1), paqueteInternet.getBono())+String.format(context.getString(R.string.pqt_descripcion2),paqueteInternet.getId()));
        }else if(paqueteInternet.getId()==7){
            holder.descripcion.setText(String.format(context.getString(R.string.pqt_descripcion1), paqueteInternet.getBono())+String.format(context.getString(R.string.pqt_descripcion2),paqueteInternet.getId()));
        }else if(paqueteInternet.getId()==10){
            holder.descripcion.setText(String.format(context.getString(R.string.pqt_descripcion1), paqueteInternet.getBono())+String.format(context.getString(R.string.pqt_descripcion2),paqueteInternet.getId()));
        }else if(paqueteInternet.getId()==20){
            holder.descripcion.setText(String.format(context.getString(R.string.pqt_descripcion1), paqueteInternet.getBono())+String.format(context.getString(R.string.pqt_descripcion2),paqueteInternet.getId()));
        }else if(paqueteInternet.getId()==30){
            holder.descripcion.setText(String.format(context.getString(R.string.pqt_descripcion1), paqueteInternet.getBono())+String.format(context.getString(R.string.pqt_descripcion2),paqueteInternet.getId()));
        }
    }

    public PaqueteInternet getPaqueteAt(int position) {
        return getItem(position);
    }

    class PaqueteInternetViewHolder extends RecyclerView.ViewHolder{
        private TextView descripcion,nombrePlan;

        public PaqueteInternetViewHolder(View view){
            super(view);
            descripcion= view.findViewById(R.id.label_description_paquete);
            nombrePlan= view.findViewById(R.id.label_paquete_precio);

            view.setOnClickListener(view1 -> {
                int pos = getAdapterPosition();
                if (onItemClickListener != null && pos != RecyclerView.NO_POSITION)
                    onItemClickListener.onItemClick(getItem(pos));
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(PaqueteInternet paqueteInternet);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }
}
