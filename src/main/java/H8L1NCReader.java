

/**
 * Created by Administrator on 2017/2/21.
 */
public class H8L1NCReader extends H8NCReader{
    float[][] vis;
    float[][] IRWV;
    float[][] IR;

    public float[][] getVis() {
        return getValue("albedo_03");
    }

    public float[][] getIRWV() {
        return getValue("tbb_09");
    }

    public float[][] getIR() {
        return getValue("tbb_14");
    }
}