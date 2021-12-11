package mc.obliviate.blokduels.utils.timer;

public class TimerUtils {

	public static String convertTimer(long timer) {
		final long now = System.currentTimeMillis();

		if (now > timer) timer = now;

		final long diff = (timer - now)/1000;

		final long minute = Math.floorDiv(diff, 60);
		final long second = Math.floorMod(diff,60);

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

}
