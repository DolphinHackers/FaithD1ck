package sb.faithd1ck.utils;

public class TimeElapsedFormatter {
    public static String formatElapsedTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        return String.format("%d天 %02d小时 %02d分钟 %02d秒", days, hours, minutes, seconds);
    }
}
