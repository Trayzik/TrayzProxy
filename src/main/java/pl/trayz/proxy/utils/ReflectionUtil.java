package pl.trayz.proxy.utils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.lang.reflect.Field;

/**
 * A util for reflection and manipulation
 */
public class ReflectionUtil {

	/**
	 * Cached information for Fields
	 */
	private static final Table<Class<?>, String, Field> CACHED_FIELDS_BY_NAME = HashBasedTable.create();
	private static final Table<Class<?>, Class<?>, Field> CACHED_FIELDS_BY_CLASS = HashBasedTable.create();
//	private static Field modifiersField;


	/**
	 * Sets a final field, with a field name, inside an object
	 * @param object The object
	 * @param fieldName The field name
	 * @param value The new value
	 */
	public static void setFinalField(Object object, String fieldName, Object value){
		setFinalField(object, getPrivateField(object.getClass(), fieldName), value);
	}

	/**
	 * Sets a final field, with a field object, inside an object
	 * @param object The object
	 * @param field The field
	 * @param value The new value
	 */
	public static void setFinalField(Object object, Field field, Object value){
		field.setAccessible(true);

		// Removed due to complications with Velocity (Thanks https://github.com/AoElite)

//		if (Modifier.isFinal(field.getModifiers())) {
//			if (modifiersField == null) {
//				try {
//					modifiersField = getDeclaredField(Field.class, "modifiers");
//				} catch (ReflectionException e) { // workaround for when searching for the modifiers field on Java 12 or higher
//					try {
//						Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
//						getDeclaredFields0.setAccessible(true);
//
//						Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
//						modifiersField = Arrays.stream(fields).filter(modifier -> modifier.getName().equals("modifiers")).findFirst().orElseThrow(() -> new ReflectionException("Could not find the modifiers field"));
//					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e2) {
//						throw new ReflectionException(e2);
//					}
//				}
//
//				modifiersField.setAccessible(true);
//			}
//
//			try {
//				modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
//			} catch (IllegalAccessException e) {
//				throw new ReflectionException(e);
//			}
//		}

		setField(object, field, value);
	}

	/**
	 * Sets a field, with a field name, inside an object
	 * @param object The object
	 * @param fieldName The field name
	 * @param value The new value
	 */
	public static void setField(Object object, String fieldName, Object value){
		setField(object, getPrivateField(object.getClass(), fieldName), value);
	}

	/**
	 * Sets a field, with a field object, inside an object
	 * @param object The object
	 * @param field The field
	 * @param value The new value
	 */
	public static void setField(Object object, Field field, Object value){
		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the object inside a private field, with a field name, in an object
	 * @param object The object
	 * @param fieldName The field name
	 * @return The grabbed object
	 */
	public static Object getObjectInPrivateField(Object object, String fieldName){
		Field field = getPrivateField(object.getClass(), fieldName);
		try {
			return field.get(object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets a private field, with a field name, inside an object
	 * @param clazz The clazz
	 * @param fieldName The field name
	 * @return The grabbed field
	 */
	public static Field getPrivateField(Class<?> clazz, String fieldName) {
		Field field = getDeclaredField(clazz, fieldName);
		field.setAccessible(true);
		return field;
	}

	/**
	 * Searches for a field, with the type of the provided class, inside a class
	 * @param clazz The class to search through
	 * @param searchFor The class type to search for
	 * @return The found field
	 */
	public static Field searchFieldByClass(Class<?> clazz, Class<?> searchFor) {
		Field cachedField = CACHED_FIELDS_BY_CLASS.get(clazz, searchFor);
		if (cachedField != null) return cachedField;

		Class<?> currentClass = clazz;
		do {
			for (Field field : currentClass.getDeclaredFields()) {
				if (!searchFor.isAssignableFrom(field.getType())) continue;

				CACHED_FIELDS_BY_CLASS.put(clazz, searchFor, field);
				return field;
			}

			currentClass = currentClass.getSuperclass();
		} while (currentClass != null);

		return null;
	}

	/**
	 * Gets a declared field, with a field name, inside a class
	 * @param clazz The clazz
	 * @param fieldName The field name
	 * @return The declared field
	 */
	public static Field getDeclaredField(Class<?> clazz, String fieldName) {
		Field cachedField = CACHED_FIELDS_BY_NAME.get(clazz, fieldName);
		if (cachedField != null) return cachedField;

		Field field;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			Class<?> superclass = clazz.getSuperclass();
			if (superclass != null) {
				return getDeclaredField(superclass, fieldName);
			} else {
				e.printStackTrace();
				return null;
			}
		}

		CACHED_FIELDS_BY_NAME.put(clazz, fieldName, field);
		return field;
	}

}