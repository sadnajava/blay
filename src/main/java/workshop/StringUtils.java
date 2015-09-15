package workshop;

public class StringUtils {

    public static boolean isEmpty(String str) {
        return ((str == null) || ("".equals(str.trim())));
    }
}
