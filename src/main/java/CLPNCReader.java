

/**
 * Created by Administrator on 2017/2/20.
 */
public class CLPNCReader extends H8NCReader{

    public static void main(String[] args) {
//        L2产品
        String filename="D:\\himawari\\NC_H08_20170107_0600_L2CLPbet_FLDK.02401_02401.nc";
        try {
            CLPNCReader reader=new CLPNCReader();
            reader.setFilename(filename);
            float[][]clot=reader.getClot();
            float[][]cltt=reader.getCltt();
            float[][]cltype=reader.getCltype();
            reader.close();
            System.out.println(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float[][]cltype;
    public float[][]clot;
    public float[][]cltt;

    public float[][] getCltype() {
        return getValue("CLTYPE");
    }

    public float[][] getClot() {
        return getValue("CLOT");
    }

    public float[][] getCltt() {
        return getValue("CLTT");
    }


}
