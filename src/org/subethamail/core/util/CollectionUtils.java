package org.subethamail.core.util;

import java.util.ArrayList;
import java.util.Collection;

import lombok.extern.java.Log;


/**
 * Some helper methods for dealing with collections
 * 
 * @author Jeff Schnitzer
 */
@Log
public class CollectionUtils
{
	/** default constructor prevents util class from being created. */
	private CollectionUtils() {}
	
	/**
	 * Creates a new ArrayList which if possible is sized the same as the passed in
	 * iterable (ie only if it is actually a Collection). 
	 */
	public static <T> ArrayList<T> newArrayListSized(Iterable<?> fromSize)
	{
		if (fromSize instanceof Collection<?>)
			return new ArrayList<T>(((Collection<?>)fromSize).size());
		else
			return new ArrayList<T>();
	}
	
}	