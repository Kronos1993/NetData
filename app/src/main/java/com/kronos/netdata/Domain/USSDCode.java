package com.kronos.netdata.Domain;

/**
 * Created by farah on 03/dic/2019.
 */
public class USSDCode {

    private static final String root_code="*133*";

    private static final String data_code="1*";

    private static final String mail_code="2*";
    private static final String daily_code="3*";
    private static final String tarifa_code="1*";

    private static final String tarifa_activar="1#";
    private static final String tarifa_desactivar="2#";

    private static final String mail_activar="1#";

    private static final String daily_activar="1#";

    private static final String code_3G="4*";
    private static final String pqt_3G_code_400="1*";
    private static final String pqt_3G_code_600="2*";
    private static final String pqt_3G_code_1GB="3*";
    private static final String pqt_3G_code_2_5GB="4*";
    private static final String pqt_3G_code_4GB="5*";

    private static final String code_4G="5*";
    private static final String pqt_4G_code_1_GB="1*";
    private static final String pqt_4G_code_2_5GB="2*";
    //private static final String pqt_4G_code_6_5GB="1*";
    private static final String pqt_4G_code_14_GB="3*";

    private static final String pqt_3G_code_400_confirm="1#";
    private static final String pqt_3G_code_600_confirm="2#";
    private static final String pqt_3G_code_1GB_confirm="3#";
    private static final String pqt_3G_code_2_5GB_confirm="4#";
    private static final String pqt_3G_code_4GB_confirm="5#";

    private static final String pqt_4G_code_1_GB_confirm="1#";
    private static final String pqt_4G_code_2_5GB_confirm="2#";
    //private static final String pqt_4G_code_6_5GB_confirm="1#";
    private static final String pqt_4G_code_14_GB_confirm="3#";

    private static final String code_accept="1#";


    public static String buy(int pqt){
        String code="";
        switch(pqt){
            //3G
            case 5:code=root_code+data_code+code_3G+pqt_3G_code_400+code_accept; break;
            case 7:code=root_code+data_code+code_3G+pqt_3G_code_600+code_accept;break;
            case 10:code=root_code+data_code+code_3G+pqt_3G_code_1GB+code_accept;break;
            case 20:code=root_code+data_code+code_3G+pqt_3G_code_2_5GB+code_accept;break;
            case 30:code=root_code+data_code+code_3G+pqt_3G_code_4GB+code_accept;break;
            //4G LTE
            case 4:code=root_code+data_code+code_4G+pqt_4G_code_1_GB+code_accept;break;
            case 8:code=root_code+data_code+code_4G+pqt_4G_code_2_5GB+code_accept;break;
            //case 35:code=root_code+data_code+code_4G+pqt_4G_code_6_5GB+code_accept;break;
            case 45:code=root_code+data_code+code_4G+pqt_4G_code_14_GB+code_accept;break;
            //bolsa nauta
            case 0:code=root_code+data_code+mail_code+mail_activar;break;
            //tarifa por consumo
            case 1:code=root_code+data_code+daily_code+daily_activar;break;
        }
        return code;
    }

    public static String confitmToBuy(int pqt){
        String code="";
        switch(pqt){
            //3G
            case 5:code=root_code+data_code+code_3G+pqt_3G_code_400_confirm; break;
            case 7:code=root_code+data_code+code_3G+pqt_3G_code_600_confirm;break;
            case 10:code=root_code+data_code+code_3G+pqt_3G_code_1GB_confirm;break;
            case 20:code=root_code+data_code+code_3G+pqt_3G_code_2_5GB_confirm;break;
            case 30:code=root_code+data_code+code_3G+pqt_3G_code_4GB_confirm;break;
            //4G LTE
            case 4:code=root_code+data_code+code_4G+pqt_4G_code_1_GB_confirm;break;
            case 8:code=root_code+data_code+code_4G+pqt_4G_code_2_5GB_confirm;break;
            //case 35:code=root_code+data_code+code_4G+pqt_4G_code_6_5GB+code_accept;break;
            case 45:code=root_code+data_code+code_4G+pqt_4G_code_14_GB_confirm;break;
            //bolsa nauta
            case 0:code=root_code+data_code+mail_code;break;
            //tarifa por consumo
            case 1:code=root_code+data_code+daily_code;break;
        }
        return code;
    }

    public static String switchTarifa(boolean activar){
        String code="";
        if(activar){
            code=root_code+data_code+tarifa_code+tarifa_activar;
        }else{
            code=root_code+data_code+tarifa_code+tarifa_desactivar;
        }
        return code;
    }




}
