package com.kronos.netdata.Activities.Fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.kronos.netdata.Activities.MainActivityDrawer;
import com.kronos.netdata.DB.Connection;
import com.kronos.netdata.Domain.Historial;
import com.kronos.netdata.Domain.MonthHistoryData;
import com.kronos.netdata.R;
import com.kronos.netdata.Util.GeneralUtility;
import org.joda.time.DateMidnight;
import org.joda.time.Days;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class ChartHistoryFragment extends Fragment {

    private Context context;
    private TextView textViewTotalCUC,textViewTotalPaquetes,textViewPromedioPaquetes,textViewPromedioCUC,textViewDuracionPaquete;
    private ArrayList<Historial> historials=new ArrayList<>();
    private ArrayList<MonthHistoryData> monthHistoryDatas=new ArrayList<>();
    private Hashtable<Integer,Hashtable<String,Integer>> packageBuy = new Hashtable<>();
    private LineChart lineChartHistory;
    private BarChart barChartPackage;
    private PieChart pieChartPercentage;

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
                    currentMonthHistorial=GeneralUtility.getHistorialsByMonth(month,historials);
                    MonthHistoryData monthHistoryData= new MonthHistoryData(month,currentMonthHistorial);
                    monthHistoryDatas.add(monthHistoryData);
                }
            }else{
                Toast.makeText(context, R.string.db_empty, Toast.LENGTH_SHORT).show();
                GeneralUtility.navigate(context, MainActivityDrawer.class);
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
        return inflater.inflate(R.layout.fragment_chart_history,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initComponent(view);
    }

    public ChartHistoryFragment() {
        // Required empty public constructor
        this.historials = new ArrayList<>();
        this.monthHistoryDatas = new ArrayList<>();
    }


    public static ChartHistoryFragment newInstance(){
        return new ChartHistoryFragment();
    }

    private void initComponent(View view){
        textViewTotalPaquetes= (TextView) view.findViewById(R.id.textViewTotalPaquetes);
        textViewTotalCUC= (TextView) view.findViewById(R.id.textViewTotalCUC);
        textViewPromedioPaquetes = (TextView) view.findViewById(R.id.textViewPromedioPaquetesMensuales);
        textViewPromedioCUC= (TextView) view.findViewById(R.id.textViewPromedioCucMensuales);
        textViewDuracionPaquete= (TextView) view.findViewById(R.id.textViewDuracionPaquete);
        barChartPackage = view.findViewById(R.id.barChartPackage);
        pieChartPercentage = view.findViewById(R.id.pieChartPercentage);

        int total=0;
        for (int i = 0 ; i < historials.size() ; i++){
            total+= historials.get(i).getId_paquete();
        }
        textViewTotalCUC.setText(String.format(context.getString(R.string.total_cuc),total));
        textViewTotalPaquetes.setText(String.format(context.getString(R.string.total_paquetes_comprados),historials.size()));
        textViewPromedioPaquetes.setText(String.format(context.getString(R.string.promedio_paquetes_mensuales),averagePackageByMonth()));
        textViewPromedioCUC.setText(String.format(context.getString(R.string.promedio_cuc_mensuales),averageMoneyByMonth()));
        lineChartHistory= (LineChart) view.findViewById(R.id.lineChartHistory);
        int days=Math.round(Days.daysBetween(new DateMidnight(historials.get(0).getDate()),new DateMidnight(Calendar.getInstance().getTimeInMillis())).getDays());
        textViewDuracionPaquete.setText(String.format(context.getString(R.string.duracion_ultimo_paquete),days));
        createLineGraph();
        createBarGraph();
        createPieGraph();
    }

    private int averagePackageByMonth(){
        float average=0f;

        int part=0,total=monthHistoryDatas.size();
        for(int i=0;i<monthHistoryDatas.size();i++){
            part+=monthHistoryDatas.get(i).getHistorials().size();
        }
        average=part/total;
        return Math.round(average);
    }

    private int averageMoneyByMonth(){
        float average=0f;

        int part=0,total=monthHistoryDatas.size();
        for(int i=0;i<monthHistoryDatas.size();i++){
            for(int j=0;j<monthHistoryDatas.get(i).getHistorials().size();j++){
                part+=monthHistoryDatas.get(i).getHistorials().get(j).getId_paquete();
            }
        }
        average=part/total;
        return Math.round(average);
    }

    //revisar
    private void createLineGraph() {
        //creando las entry de la grafica
        List<Entry> chartEntry=new ArrayList<>();
        //llenando el año
        for(int i=0;i<12;i++){
            Entry entry0x0=new Entry(i+1,0);
            chartEntry.add(entry0x0);
        }
        String currentmonth="";
        for (MonthHistoryData historial: monthHistoryDatas){
            int month= GeneralUtility.getMonthNumbre(historial.getMonth_name());
            Entry entry=new Entry(month,historial.getHistorials().size());
            //Poniendo los datos en los meses
            chartEntry.set(month-1,entry);
            //addEntryInOrder(entry,chartEntry,month);
        }
        //creando el data set de una grafica lineal
        LineDataSet lineDataSet=new LineDataSet(chartEntry,"Total de compras por mes");
        lineDataSet.setDrawIcons(false);
        lineDataSet.enableDashedLine(10f, 5f, 0f);
        lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lineDataSet.setColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null));
            lineDataSet.setCircleColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null));
        }else{
            lineDataSet.setColor(Color.DKGRAY);
            lineDataSet.setCircleColor(Color.GRAY);
        }
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(9f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        lineDataSet.setFormSize(15.f);
        //especificando la dependencia del eje
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        //creando el set de datos
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        lineChartHistory.setData(data);
        lineChartHistory.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                String label = "";
                if((int)v<=12){
                    label = GeneralUtility.getMonthName((int)v,context);
                }
                return label;
            }
        });
        stylingLineChart(lineChartHistory);
        lineChartHistory.animateXY(2000,2000, Easing.EasingOption.Linear,Easing.EasingOption.Linear);
        lineChartHistory.invalidate(); // refresh
    }

    //revisar
    private void createBarGraph() {
        Hashtable<String,Integer> packageData = new Hashtable<>();
        //creando las entry de la grafica
        List<BarEntry> chartEntry=new ArrayList<>();
        //llenando con los paquetes comprados
        for(int i=0;i<historials.size();i++){
            if(packageBuy.containsKey(historials.get(i).getId_paquete())){
                packageData = packageBuy.get(historials.get(i).getId_paquete());
                if(packageData.containsKey(historials.get(i).getPaquete())){
                    packageData.put(historials.get(i).getPaquete(),packageData.get(historials.get(i).getPaquete())+1);
                    packageBuy.put(historials.get(i).getId_paquete(),packageData);
                }
            }else{
                packageData = new Hashtable<>();
                packageData.put(historials.get(i).getPaquete(),1);
                packageBuy.put(historials.get(i).getId_paquete(),packageData);
            }
        }

        final ArrayList labelsX = new ArrayList();
        Set<Integer> keys= packageBuy.keySet();

        int pos = 0;
        for(Integer key:keys)
        {
            Set<String> stringSet = packageBuy.get(key).keySet();
            for (String keyS : stringSet){
                labelsX.add(keyS);
                BarEntry entry=new BarEntry(pos,packageBuy.get(key).get(keyS));
                //Poniendo los datos en los paqutes
                chartEntry.add(entry);
            }
            pos ++;
        }
        //creando el data set de una grafica lineal


        BarDataSet barDataSet=new BarDataSet(chartEntry,"Total de compras por paquetes");
        barDataSet.setDrawIcons(false);
        barDataSet.setValueTextSize(9f);
        barDataSet.setFormLineWidth(1f);
        barDataSet.setFormSize(15.f);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setColor(R.color.colorPrimary);
        //especificando la dependencia del eje
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        //creando el set de datos
        List<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(barDataSet);
        BarData data = new BarData(dataSets);
        barChartPackage.setData(data);
        barChartPackage.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                String label = "";
                if((int)v<labelsX.size()){
                    label = (String) labelsX.get((int)v);
                }
                return label;
            }
        });
        stylingBarChart(barChartPackage);
        barChartPackage.animateXY(2000,2000,Easing.EasingOption.Linear,Easing.EasingOption.Linear);
        barChartPackage.invalidate(); // refresh
    }

    //revisar
    private void createPieGraph() {
        //creando las entry de la grafica
        List<PieEntry> chartEntry=new ArrayList<>();
        int total = historials.size();
        Set<Integer> keys= packageBuy.keySet();
        for(Integer key:keys)
        {
            Set<String> stringSet = packageBuy.get(key).keySet();
            for (String keyS : stringSet){
                PieEntry entry=new PieEntry((packageBuy.get(key).get(keyS)*100)/total,keyS);
                //Poniendo los datos en los paqutes
                chartEntry.add(entry);

            }
        }
        //creando el data set de una grafica lineal
        PieDataSet pieDataSet=new PieDataSet(chartEntry,"% por paquetes comprados");
        pieDataSet.setDrawIcons(false);
        pieDataSet.setValueTextSize(9f);
        pieDataSet.setSliceSpace(5);
        pieDataSet.setFormLineWidth(1f);
        pieDataSet.setFormSize(15.f);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //especificando la dependencia del eje
        pieDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        pieDataSet.setValueFormatter(new PercentFormatter());
        //creando el set de datos
        List<IPieDataSet> dataSets = new ArrayList<IPieDataSet>();
        dataSets.add(pieDataSet);
        PieData data = new PieData(pieDataSet);
        pieChartPercentage.setData(data);
        stylingPieChart(pieChartPercentage);
        pieChartPercentage.animateXY(2000,2000,Easing.EasingOption.Linear,Easing.EasingOption.Linear);
        pieChartPercentage.invalidate(); // refresh
    }


    //Utilizar metodo de ordenamineto
    private void addEntryInOrder(Entry entry, List<Entry> chartEntry, int month) {
        if(chartEntry.size()==0){
            chartEntry.add(entry);
        }else{
            boolean insert=false;
            int i=0;
            while (!insert && i<chartEntry.size()){
                if(chartEntry.get(i).getX()<month){
                    chartEntry.add(i,entry);
                    insert=true;
                }else{
                    i++;
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void stylingLineChart(LineChart chart){
        chart.getDescription().setEnabled(false);// Set a description disabled.
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.setNoDataText("No data set yet");//  Sets the text that should appear if the chart is empty.
        chart.setDrawGridBackground(true);//  If enabled, the background rectangle behind the chart drawing-area will be drawn.
        chart.setDrawBorders(true);//  Enables / disables drawing the chart borders (lines surrounding the chart).
        chart.getAxisLeft().setTextSize(10);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setTextColor(ResourcesCompat.getColor(getResources(),R.color.secondaryText,null));
        chart.getXAxis().setTextSize(10);
        chart.getXAxis().setGranularity(1f);
        chart.getAxisLeft().setTextColor(ResourcesCompat.getColor(getResources(),R.color.secondaryText,null));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        Legend legend=lineChartHistory.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);
        legend.setTextColor(ResourcesCompat.getColor(getResources(),R.color.secondaryText,null));
        legend.setTextSize(15);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void stylingBarChart(BarChart chart){
        chart.getDescription().setEnabled(false);// Set a description disabled.
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.setNoDataText("No data set yet");//  Sets the text that should appear if the chart is empty.
        chart.setDrawGridBackground(true);//  If enabled, the background rectangle behind the chart drawing-area will be drawn.
        chart.setDrawBorders(true);//  Enables / disables drawing the chart borders (lines surrounding the chart).
        chart.getAxisLeft().setTextSize(10);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setTextColor(ResourcesCompat.getColor(getResources(),R.color.secondaryText,null));
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setTextColor(ResourcesCompat.getColor(getResources(),R.color.secondaryText,null));
        chart.getXAxis().setTextSize(10);
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        Legend legend=chart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);
        legend.setTextColor(ResourcesCompat.getColor(getResources(),R.color.secondaryText,null));
        legend.setTextSize(15);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void stylingPieChart(PieChart chart){
        chart.getDescription().setEnabled(false);// Set a description disabled.
        chart.setTouchEnabled(true);
        chart.setUsePercentValues(true);
        chart.setDrawEntryLabels(true);
        chart.setDrawSlicesUnderHole(true);
        chart.setHoleRadius(10);
        chart.setTransparentCircleRadius(10);
        chart.setNoDataText("No data set yet");//  Sets the text that should appear if the chart is empty.
        Legend legend=chart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);
        legend.setTextColor(ResourcesCompat.getColor(getResources(),R.color.secondaryText,null));
        legend.setTextSize(15);
    }


}
