/*
 * $Id$
 * $URL$
 */

package org.subethamail.web.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Models which can be paginated (take a skip and count, etc)
 * 
 * @author Jeff Schnitzer
 */
public class PaginateModel 
{
	/** */
	public static final int DEFAULT_COUNT = 20;
	
	/** Input */
	int count = DEFAULT_COUNT;
	public int getCount() { return this.count; }
	public void setCount(int value) { this.count = value; }
	
	/** Input */
	int skip = 0;
	public int getSkip() { return this.skip; }
	public void setSkip(int value) { this.skip = value; }

	/** Output */
	int totalCount;
	public int getTotalCount() { return this.totalCount; }
	
	/**
	 * Must be called during action execution
	 */
	public void setTotalCount(int value)
	{
		this.totalCount = value;
	}
	
	/** */
	public boolean isHead()
	{
		return this.skip == 0;
	}
	
	/** */
	public boolean isTail()
	{
		return this.skip >= this.getLast();
	}
	
	/** */
	public int getPrevious()
	{
		return Math.max(0, this.skip - this.count);
	}
	
	/** */
	public int getNext()
	{
		return Math.min(this.getLast(), this.skip + this.count);
	}
	
	/**
	 * @return the last page-oriented skip value
	 */
	public int getLast()
	{
		int pageCount = this.totalCount / this.count;
		
		if (this.totalCount == (pageCount * this.count))
			return (pageCount - 1) * this.count;
		else
			return pageCount * this.count;
	}
	
	/**
	 * @return true if there is only one page.
	 */
	public boolean isSinglePage()
	{
		return this.totalCount <= this.count;
	}
	
	/**
	 * @return the 1-based index of the item being displayed first
	 */
	public int getDisplayedFirst()
	{
		return this.skip + 1;
	}
	
	/**
	 * @return the 1-based index of the item being displayed last
	 */
	public int getDisplayedLast()
	{
		int theoretical = this.skip + this.count;
		
		if (theoretical > this.totalCount)
			return this.totalCount;
		else
			return theoretical;
	}
	
	/**
	 * Allows the display of pagination like this:
	 * http://developer.yahoo.net/ypatterns/pattern_searchpagination.php
	 */
	public List<Page> getPaginationPages()
	{
		List<Page> retVal = new ArrayList<Page>(10);
		
		// This is tricky.  Just remember that page indexes are 0-based,
		// they only become 1-based displayNumbers at the last moment.
		
		int pageCount = this.totalCount / this.count;
		if (totalCount % this.count != 0)
			pageCount++;
		
		int lastPage = pageCount - 1;
		
		int currentPage = this.skip / this.count;
		
		if (currentPage > lastPage)
			currentPage = lastPage;
		
		int startingPage = currentPage - 5;
		int endingPage = currentPage + 4;
		
		// Maybe need to contract the starting page part
		if (startingPage < 0)
		{
			endingPage += Math.abs(startingPage);
			startingPage = 0;
		}
		
		// Maybe need to contract the ending page part
		if (endingPage > lastPage)
		{
			startingPage -= (endingPage - lastPage);
			endingPage = lastPage;
		}
		
		// Ending page contraction might have pushed starting page back too far
		if (startingPage < 0)
			startingPage = 0;
		
		for (int i=startingPage; i<=endingPage; i++)
		{
			retVal.add(new Page(i+1, i*this.count, i==currentPage));
		}
		
		return retVal;
	}
	
	/**
	 * A simple class to allow displaying search-type pagination.
	 * Number is the 1-based page number suitable for display.
	 */
	public static class Page
	{
		int displayNumber;
		int skip;
		boolean current;
		
		public Page(int displayNumber, int skip, boolean current)
		{
			this.displayNumber = displayNumber;
			this.skip = skip;
			this.current = current;
		}

		/** */
		public boolean isCurrent()
		{
			return this.current;
		}

		/** */
		public int getDisplayNumber()
		{
			return this.displayNumber;
		}

		/** */
		public int getSkip()
		{
			return this.skip;
		}
	}
}
