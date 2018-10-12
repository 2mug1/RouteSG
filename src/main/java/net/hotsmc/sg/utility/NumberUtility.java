package net.hotsmc.sg.utility;

public class NumberUtility {

    public static boolean isNumber(String val) {
        try {
            Integer.parseInt(val);
            return true;
        } catch (NumberFormatException nfex) {
            return false;
        }
    }
}
