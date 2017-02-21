import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */
public class H8NCReader {
    protected static String SCALE="scale_factor";
    protected static String OFFSET="add_offset";
    protected  float slat=0f;
    protected  float elat=32f;
    protected  float slon=105f;
    protected  float elon=135f;
    protected NetcdfFile ncfile;
    protected int[] range;
    protected String filename;
    protected boolean  isOpen=false;

    public void close() throws IOException {
        isOpen=false;
        ncfile.close();
    }

    public void init(){
        try {
            ncfile = NetcdfDataset.open(filename);
            List<Variable> res = ncfile.getVariables();
            Variable lat = ncfile.findVariable(ncfile.getRootGroup(), "latitude");
            Variable lon = ncfile.findVariable(ncfile.getRootGroup(), "longitude");
            range = getRange(lat, lon);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  int[] getRange(Variable vlat,Variable vlon){
        int[] range=null;
        try {
            Array alat=vlat.read();
            Array alon=vlon.read();
            float[] olat=(float[])alat.copyTo1DJavaArray();
            float[] olon=(float[])alon.copyTo1DJavaArray();
            int slatidx=getIdx(slat,olat);
            int elatidx=getIdx(elat,olat);
            int slonidx=getIdx(slon,olon);
            int elonidx=getIdx(elon,olon);
            range=new int[]{Math.min(slatidx,elatidx),Math.max(slatidx,elatidx),
                    Math.min(slonidx,elonidx),Math.max(slonidx,elonidx)};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return range;
    }

    private  int getIdx(float key, float[] arrays) {
        if(arrays[0]>arrays[arrays.length-1]){
            Arrays.sort(arrays);
            return arrays.length-Arrays.binarySearch(arrays,key)-1;
        }
       return Arrays.binarySearch(arrays,key);
    }

    public  Object getData(Variable var,int[] range){
        try {
            Array arr=var.read();
            int[] dim=arr.getShape();
            DataType dt=var.getDataType();
            ByteBuffer buffer=arr.getDataAsByteBuffer();
            List<Attribute> atrs=var.getAttributes();
            float scale=1f;
            float offset=0f;
            for(Attribute attr:atrs){
                if(attr.getShortName().equals(SCALE)){
                    scale=attr.getNumericValue().floatValue();
                }else if(attr.getShortName().equals(OFFSET))
                    offset=attr.getNumericValue().floatValue();
            }
            buffer.array();
//          1维数组到底是array[lat][lon] 还是 array[lon][lat]----一般都是前者
            if(dim.length==2){
                int ki=0;int kj=0;
                if(dt.getPrimitiveClassType().equals(short.class)){
                    float[][]tmp=new float[range[1]-range[0]+1][range[3]-range[2]+1];
                    for(int i=0;i<dim[0];i++){
                        for(int j=0;j<dim[1];j++){
                            short t=buffer.getShort();
                            if(i>=range[0] && i<=range[1] && j>=range[2]&&j<=range[3]){
                                tmp[ki][kj]=t*scale+offset;
                                kj++;
                            }
                        }
                        kj=0;
                        if(i>=range[0] && i<=range[1])ki++;
                    }
                    return tmp;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected float[][] getValue(String name){
        if(isOpen){
            Variable var=ncfile.findVariable(ncfile.getRootGroup(),name);
            return (float[][])getData(var,range);
        }else{
            init();
            isOpen=true;
            Variable var=ncfile.findVariable(ncfile.getRootGroup(),name);
            return (float[][])getData(var,range);
        }
    }

    public float getSlat() {
        return slat;
    }

    public void setSlat(float slat) {
        this.slat = slat;
    }

    public float getElat() {
        return elat;
    }

    public void setElat(float elat) {
        this.elat = elat;
    }

    public float getSlon() {
        return slon;
    }

    public void setSlon(float slon) {
        this.slon = slon;
    }

    public float getElon() {
        return elon;
    }

    public void setElon(float elon) {
        this.elon = elon;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
