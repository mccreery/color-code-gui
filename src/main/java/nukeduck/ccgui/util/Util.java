package nukeduck.ccgui.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

public final class Util {
	private Util() {}

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

	/** @param name lower case, snake_case or CONSTANT_CASE identifier
	 * @return {@code name} converted to Title Case */
	public static final String toTitle(String name) {
		return WordUtils.capitalizeFully(name.replace('_', ' '));
	}
}
