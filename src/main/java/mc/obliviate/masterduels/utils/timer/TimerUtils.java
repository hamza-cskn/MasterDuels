package mc.obliviate.masterduels.utils.timer;

public class TimerUtils {

	public static String MINUTE = " minute";
	public static String MINUTES = " minutes";
	public static String SECOND = " seconds";
	public static String SECONDS = " second";


	/**
	 *
	 * @param endTime give time out time.
	 * @return formatted TIMER text.
	 */
	public static String formatTimeUntilThenAsTimer(long endTime) {
		final long now = System.currentTimeMillis();

		if (now > endTime) endTime = now;

		final long diff = (endTime - now) / 1000;
		return formatTimeAsTimer(diff);
	}

	/**
	 *
	 * @param endTime give time out time
	 * @return formatted TIME text.
	 */
	public static String formatTimeUntilThenAsTime(long endTime) {
		return formatTimeDifferenceAsTime(System.currentTimeMillis(), endTime);
	}

	/**
	 * @param start start time
	 * @param end end time
	 * method will get difference of these.
	 * @return formatted TIME text
	 */
	public static String formatTimeDifferenceAsTime(long start, long end) {
		if (start > end) start = end;

		final long diff = (end - start) / 1000;

		return formatTimeAsTime(diff);
	}
	/**
	 * @param start start time
	 * @param end end time
	 * method will get difference of these.
	 * @return formatted TIMER text
	 */
	public static String formatTimeDifferenceAsTimer(long start, long end) {
		if (start > end) start = end;

		final long diff = (end - start) / 1000;

		return formatTimeAsTimer(diff);
	}

	/**
	 * @param diff difference between start time and end time
	 * @return formatted TIMER text
	 */
	public static String formatTimeAsTimer(long diff) {
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
	/**
	 * @param diff difference between start time and end time
	 * @return formatted TIME text
	 */
	public static String formatTimeAsTime(long diff) {

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

			if (second == 1) {
				builder.append(SECOND);
			} else {
				builder.append(SECONDS);
			}
		}

		return builder.toString();

	}

}
