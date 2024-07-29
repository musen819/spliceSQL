/**
 * 1. 都写成静态方法  方便程序调用
 */
public class FieldsCalculated {
    public static void sampleMethod() {
        System.out.println("Hello, this is a sample method.");
    }

    /**
     * 获取缴费期间类型
     *
     * @param premPeriod 缴费类型
     * @return
     */
    public static String getPremPeriodType (String premPeriod) {
        System.out.println(premPeriod);
        String temp = "'1'";
        // sql 解析有问题
        if (temp.equals(premPeriod) || "0".equals(premPeriod)) {
            return "1";
        }
        return "2";
    }

    /**
     * 获取缴费期间
     *
     * @param premPeriod
     * @return
     */
    public static String getPremPeriod (String premPeriod) {
        if ("1".equals(premPeriod)) {
            return "0";
        }
        return premPeriod;
    }

    public static String getADDITIONALPRODUCTFLAG (String planName) {
        return planName;
    }
}