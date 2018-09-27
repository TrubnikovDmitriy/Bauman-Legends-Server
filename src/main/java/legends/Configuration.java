package legends;

import org.springframework.lang.NonNull;

import java.util.Calendar;

public class Configuration {

	public static volatile boolean pilotStage = false; // TODO false by default
	public static volatile boolean finalStage = true; // TODO false by default

	static final Character SEPARATOR = ';';

	@NonNull public static Integer currentTimestamp() {
		return new Long(Calendar.getInstance().getTimeInMillis() / 1000).intValue();
	}
}
