package mc.obliviate.blokduels.utils.timer;

public class TimerUtils {

	public static String MINUTE = " minute";
	public static String MINUTES = " minutes";
	public static String SECOND = " seconds";
	public static String SECONDS = " second";

	public static String formatTimerFormat(long endTime) {
		final long now = System.currentTimeMillis();

		if (now > endTime) endTime = now;

		final long diff = (endTime - now) / 1000;

		final long minute = Math.floorDiv(diff, 60);
		final long second = Math.floorMod(diff, 60);

		String min = minute + "";
		if (minute < 10) {
			min = "0" + minute;
		}

		String sec = second + "";
		if (second < 10) {
			sec = "0" + second;
		}

		return min + ":" + sec;
	}

	public static String formatTimeFormat(long endTime) {
		return getFormattedDifferentTime(System.currentTimeMillis(), endTime);
	}

	public static String getFormattedDifferentTime(long start, long end) {
		if (start > end) start = end;

		final long diff = (end - start) / 1000;

		final long minute = Math.floorDiv(diff, 60);
		final long second = Math.floorMod(diff, 60);

		final StringBuilder builder = new StringBuilder();

		if (minute > 0) {
			builder.append(minute).append(" ");
			if (minute == 1) {
				builder.append(MINUTES);
			} else {
				builder.append(MINUTE);
			}
		}

		if (second > 0) {
			if (minute > 0) builder.append(" ");

			builder.append(second).append(" ");

			if (minute == 1) {
				builder.append(SECOND);
			} else {
				builder.append(SECONDS);
			}
		}

		return builder.toString();
	}

}
