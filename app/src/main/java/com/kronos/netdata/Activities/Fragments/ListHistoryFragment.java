package com.kronos.netdata.Activities.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kronos.netdata.Activities.Adapters.HistoryExpandableListViewAdapter;
import com.kronos.netdata.Activities.MainActivityDrawer;
import com.kronos.netdata.DB.Connection;
import com.kronos.netdata.Domain.Historial;
import com.kronos.netdata.Domain.MonthHistoryData;
import com.kronos.netdata.R;
import com.kronos.netdata.Util.GeneralUtility;
import java.sql.SQLException;
import java.util.ArrayList;

public class ListHistoryFragment extends Fragment {

    private Context context;
    private ExpandableListView expandableListView;
    private ArrayList<Historial> historials=new ArrayList<>();
    private ArrayList<MonthHistoryData> monthHistoryDatas=new ArrayList<>();

    public ListHistoryFragment() {
        // Required empty public constructor
        this.monthHistoryDatas = new ArrayList<>();
    }

    public static ListHistoryFragment newInstance(){
        return new ListHistoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        try {
            /*probar*/
            QueryBuilder<Historial,Integer> queryBuilder= Connection.getConnection(context).getHistorialDao().queryBuilder();
            queryBuilder.orderBy(Historial.DATE_FIELD_NAME, false);
            PreparedQuery<Historial> preparedQuery = queryBuilder.prepare();
            historials= (ArrayList<Historial>) Connection.getConnection(context).getHistorialDao().query(preparedQuery);
            /*ORDER BY updated_on DESC*/
            if(historials.size()>0){
                String currentmonth=historials.get(0).getMonth_name();
                ArrayList<String> months=new ArrayList<>();
                months.add(currentmonth);
                ArrayList<Historial> currentMonthHistorial=new ArrayList<>();
                for (Historial historial : historials){
                    if(!currentmonth.toLowerCase().equals(historial.getMonth_name().toLowerCase())){
                        currentmonth=historial.getMonth_name();
                        months.add(currentmonth);
                    }
                }
                for(String month:months){
                    currentMonthHistorial= GeneralUtility.getHistorialsByMonth(month,historials);
                    MonthHistoryData monthHistoryData= new MonthHistoryData(month,currentMonthHistorial);
                    monthHistoryDatas.add(monthHistoryData);
                }
            }

        } catch (SQLException e) {
            Toast.makeText(context, R.string.error_db, Toast.LENGTH_SHORT).show();
            GeneralUtility.navigate(context, MainActivityDrawer.class);
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_history,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initComponent(view);
    }


    private void initComponent(View view){
        expandableListView = view.findViewById(R.id.expandableListHistorial);
        HistoryExpandableListViewAdapter historyExpandableListViewAdapter=new HistoryExpandableListViewAdapter(context,monthHistoryDatas,monthHistoryDatas);
        expandableListView.setAdapter(historyExpandableListViewAdapter);
    }



}
