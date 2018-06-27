package za.co.facegroup.zar;

/**
 * Created by Werner Pretorius on 2017/12/14.
 */
import za.co.facegroup.zar.drivingLicenseCard.drivingLicenseCard;
public class License {

    static
    {
        try
        {
            System.loadLibrary("libzarlicense");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public native void loadlicense(byte[] input);
    public native drivingLicenseCard deserialize(byte[] input);

}