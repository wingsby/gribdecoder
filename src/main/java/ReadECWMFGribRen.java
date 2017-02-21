
import org.apache.commons.io.FileUtils;
import ucar.nc2.grib.grib1.*;
import ucar.nc2.grib.grib1.tables.Grib1Customizer;
import ucar.nc2.grib.grib1.tables.Grib1ParamTables;
import ucar.unidata.io.RandomAccessFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;


/**
 * Created by Administrator on 2017/2/6.
 */
public class ReadECWMFGribRen {
    // 打开文件并将数据放入相应文件夹，每个文件都转为小文件，一般意义写一个头文件剩下都是小文件

    String rootpath;
    String outpath;


    // 打开文件夹，从文件夹中枚举文件并依次转化
    public void convertLittleGrib(){
        Collection<File> collection=FileUtils.listFiles(new File(rootpath),new String[]{"*.grib"},true);
        for(File file:collection){
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(file.getAbsolutePath(), "r");
                Grib1RecordScanner scan = new Grib1RecordScanner(raf);
                while(scan.hasNext()){
                    Grib1Record gr1=scan.next();
//                    ;获取信息有时间、层次、要素、起始经纬度、nx，ny以及分辨率
                    Grib1SectionProductDefinition pds = gr1.getPDSsection();
                    Grib1Customizer grib1Customizer=Grib1Customizer.factory(gr1,new Grib1ParamTables());
                    Grib1ParamLevel gpl=grib1Customizer.getParamLevel(pds);
                    Grib1Parameter gp=grib1Customizer.getParameter(pds.getCenter(),pds.getSubCenter(),pds.getTableVersion(),
                            pds.getParameterNumber());
                    Grib1ParamTime gpt=grib1Customizer.getParamTime(pds);
                    String name="XHGFS_A_"+getXHparaname(gp.getName())+"_"+getXHLevname(gpl.getValue1())+
                            "_"+getXHTimename("",gpt)+".dat";
                    // 获取数据
                    float[]res=gr1.readData(raf);
                    // 直接输出全球数据，否则需要从中选择输出相应范围的数据
                    // 开始转换为适合C、FORTRAN、IDL等语言读写的文件
                    java.io.RandomAccessFile out=new java.io.RandomAccessFile(outpath+"/"+name,"w");
                    ByteBuffer outdata= ByteBuffer.allocate(res.length*4);
//                    FloatBuffer outdata=FloatBuffer.allocate(res.length);
                    outdata.order(ByteOrder.LITTLE_ENDIAN);
                    for(float f:res)
                        outdata.putFloat(f);
                    out.write(outdata.array());
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: 2017/2/7
    public static String getXHparaname(String name){
        if(name.contains("Geo"))return "HH";
        return name;
    }

    // TODO: 2017/2/7
    public static String getXHLevname(float name){
        int lev=(int)Math.floor(name);
        String str=String.format("%04d",lev );
        return str;
    }

    // TODO: 2017/2/7
    public  static String getXHTimename(String time,Grib1ParamTime gpt){

        String year=time.substring(0,4);
        String month=time.substring(5,7);
        String day=time.substring(8,10);
        String hour=time.substring(11,13);
        String forcast= String.format("%03d",gpt.getForecastTime());

        return year+month+day+hour+forcast;
    }


    public static void main(String[] args) {
        String path = "D:\\data\\";
        File f = new File(path);
        String fname = path + File.separator
                + "geo201410.grib";
        {
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(fname, "r");
                Grib1RecordScanner scan = new Grib1RecordScanner(raf);
                while(scan.hasNext()){
                    Grib1Record gr1=scan.next();
//                    ;获取信息有时间、层次、要素、起始经纬度、nx，ny以及分辨率
                    Grib1SectionProductDefinition pds = gr1.getPDSsection();
                    Grib1Customizer grib1Customizer=Grib1Customizer.factory(gr1,new Grib1ParamTables());
                    Grib1ParamLevel gpl=grib1Customizer.getParamLevel(pds);
                    Grib1Parameter gp=grib1Customizer.getParameter(pds.getCenter(),pds.getSubCenter(),pds.getTableVersion(),
                            pds.getParameterNumber());
                    Grib1ParamTime gpt=grib1Customizer.getParamTime(pds);
                    String time=pds.getReferenceDate().toString();

//                    System.out.println(grib1Customizer.getLevelUnits(gpl.getLevelType()));
//                    System.out.println(grib1Customizer.getLevelDatum(gpl.getLevelType()));
//                    System.out.println(grib1Customizer.getLevelNameShort(gpl.getLevelType()));
//                    System.out.println(gpl.getValue1());
//                    System.out.println(gpl.getValue2());
//                    System.out.println(gp.getDescription()+gpl.getDescription()+gpl.getNameShort());
//                    System.out.println(gp.getDescription());

                    String name="XHGFS_A_"+ getXHparaname(gp.getDescription())+"_"+getXHLevname(gpl.getValue1())+
                            "_"+getXHTimename(time,gpt)+".dat";

                    // 获取数据
                    float[]res=gr1.readData(raf);
                    // 直接输出全球数据，否则需要从中选择输出相应范围的数据
                    // 开始转换为适合C、FORTRAN、IDL等语言读写的文件
                    java.io.RandomAccessFile out=new java.io.RandomAccessFile(path+"/"+name,"rw");
                    ByteBuffer outdata= ByteBuffer.allocate(res.length*4);
//                    FloatBuffer outdata=FloatBuffer.allocate(res.length);
                    outdata.order(ByteOrder.LITTLE_ENDIAN);
                    for(float ff:res)
                        outdata.putFloat(ff);
                    out.write(outdata.array());
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}


