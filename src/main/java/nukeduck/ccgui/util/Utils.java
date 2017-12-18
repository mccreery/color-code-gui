package nukeduck.ccgui.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.ScaledResolution;
import nukeduck.ccgui.ColorCodeGUI;

public final class Utils {
	private Utils() {}

	/** As {@link #getFields(List, Object, Class, boolean)},
	 * but creates and returns the list.
	 * @param obj The object to retrieve values from
	 * @param cls The valid type for fields
	 * @param many {@code true} to continue after finding a match
	 * @return The new populated list. */
	public static final <T> List<T> getFields(Object obj, Class<? extends T> cls, boolean many)
			throws IllegalArgumentException, IllegalAccessException {
		List<T> fields = new ArrayList<T>();
		getFields(fields, obj, cls, many);
		return fields;
	}

	/** Populates the given list with values of one or all fields of the
	 * correct type in the given object.<br>
	 * The class is tested using {@link Class#isAssignableFrom(Class)}.
	 * @param fields The list to populate
	 * @param obj The object to retrieve values from
	 * @param cls The valid type for fields
	 * @param many {@code true} to continue after finding a match */
	public static final <T> void getFields(List<T> fields, Object obj, Class<? extends T> cls, boolean many)
			throws IllegalArgumentException, IllegalAccessException {
		for(Field f : obj.getClass().getDeclaredFields()) {
			if(cls.isAssignableFrom(f.getType())) {
				f.setAccessible(true);

				// TODO wot
				@SuppressWarnings("unchecked")
				T found = (T)f.get(obj);

				if(found != null) {
					fields.add(found);
					if(!many) break;
				}
			}
		}
	}

	/** @return The text version of the formatting code */
	public static final String getFormatString(ChatFormatting format) {
		final char[] chars = new char[] {
			ColorCodeGUI.INSTANCE.config.prefix, format.getChar()
		};
		return new String(chars);
	}

	/** @return The custom mod-wide GUI scale factor */
	public static final int getScaleFactor() {
		if(ColorCodeGUI.INSTANCE.config.scale == 0) {
			return new ScaledResolution(Constants.MINECRAFT).getScaleFactor();
		}
		return ColorCodeGUI.INSTANCE.config.scale;
	}

	/** Generates a nice-looking display name from the codename
	 * @param format The format to generate a name from
	 * @param space The separator between words
	 * @return The generated string */
	public static final String getDisplayName(String name, char space) {
		if(name == null) return null;

		char[] chars = name.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);

		for(int i = 1; i < chars.length; i++) {
			if(chars[i] == space) {
				chars[i++] = ' '; // Replace with real space
				if(i < chars.length) // Capitalise first letter of word
					chars[i] = Character.toUpperCase(chars[i]);
			} else { // Convert tail of word to lowercase
				chars[i] = Character.toLowerCase(chars[i]);
			}
		}
		return new String(chars);
	}
}
