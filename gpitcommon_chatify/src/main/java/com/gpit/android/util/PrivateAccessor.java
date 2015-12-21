package com.gpit.android.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.util.Log;

import junit.framework.Assert;

/**
 * Provides access to private members in classes.
 */
public class PrivateAccessor {
	private final static String TAG = "private access";

	public static Object getPrivateField(Object o, Class cls, String fieldName) {
		// Check we have valid arguments...
		Assert.assertNotNull(o);
		Assert.assertNotNull(fieldName);

		// Go and find the private field...
		final Field fields[] = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; ++i) {
			if (fieldName.equals(fields[i].getName())) {
				try {
					fields[i].setAccessible(true);
					return fields[i].get(o);
				} catch (IllegalAccessException ex) {
					return null;
				}
			}
		}
		Log.d(TAG, "Field '" + fieldName + "' not found");
		
		return null;
	}
	
	public static void setPrivateField(Object o, Class cls, String fieldName, Object value) {
		// Check we have valid arguments...
		Assert.assertNotNull(o);
		Assert.assertNotNull(fieldName);

		// Go and find the private field...
		final Field fields[] = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; ++i) {
			if (fieldName.equals(fields[i].getName())) {
				try {
					fields[i].setAccessible(true);
					fields[i].set(o, value);
					return;
				} catch (IllegalAccessException ex) {
					return;
				}
			}
		}
		Log.d(TAG, "Field '" + fieldName + "' not found");
		
		return;
	}

	public static Object invokePrivateMethod(Object o, Class cls, String methodName,
			Object[] params) {
		// Check we have valid arguments...
		Assert.assertNotNull(o);
		Assert.assertNotNull(methodName);
		// Assert.assertNotNull(params);

		// Go and find the private method...
		final Method methods[] = cls.getDeclaredMethods();
		for (int i = 0; i < methods.length; ++i) {
			if (methodName.equals(methods[i].getName())) {
				try {
					methods[i].setAccessible(true);
					if (params == null) {
						return methods[i].invoke(o);
					} else {
						return methods[i].invoke(o, params);
					}
				} catch (IllegalAccessException ex) {
					return null;
				} catch (InvocationTargetException ite) {
					return null;
				}
			}
		}
		Log.d(TAG, "Method '" + methodName + "' not found");
		
		return null;
	}
}