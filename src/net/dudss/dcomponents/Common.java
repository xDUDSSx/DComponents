package net.dudss.dcomponents;

import java.time.format.DateTimeFormatter;

public class Common {
	public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
	public static DateTimeFormatter dateOnlyFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	public static DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	public static final int DEFAULT_PARAGRAPH_WIDTH = 350;
}
