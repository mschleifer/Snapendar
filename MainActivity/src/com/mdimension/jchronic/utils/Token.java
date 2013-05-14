/**
 * Modified by Matthew Schleifer
 * Original source code: https://github.com/samtingleff/jchronic
 */
package com.mdimension.jchronic.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mdimension.jchronic.tags.Tag;

public class Token {
  private String _word;
  private List<Tag<?>> _tags;

  public Token(String word) {
    _word = word;
    _tags = new LinkedList<Tag<?>>();
  }

  public String getWord() {
    return _word;
  }

  /**
   * Tag this token with the specified tag
   */
  public void tag(Tag<?> newTag) {
    _tags.add(newTag);
  }

  /**
   * Remove all tags of the given class
   */
  public void untag(Class<?> tagClass) {
    Iterator<Tag<?>> tagIter = _tags.iterator();
    while (tagIter.hasNext()) {
      Tag<?> tag = tagIter.next();
      if (tagClass.isInstance(tag)) {
        tagIter.remove();
      }
    }
  }

  /**
   * Return true if this token has any tags
   */
  public boolean isTagged() {
    return !_tags.isEmpty();
  }
  
  public boolean isDigit() {
	  try { 
	        Integer.parseInt(_word); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
  }
  
  /**
   * Determine if the Token can be parsed into hh:mm format
   * @return True if it can be parsed as hh:mm
   */
  public boolean isTime() {
	  DateFormat sdf = new SimpleDateFormat("hh:mm");
	  try {
		  sdf.parse(_word);
	  } catch(ParseException ex) {
		  return false;
	  }
	  return true;
  }
  
  public boolean isAMPM() {
	  return (_word.equals("am") | _word.equals("pm"));
  }
  
  /**
   * Determine if the Token represents a Gregorian month
   * @return True if it represents a month
   */
  public boolean isMonth() {
	  if(_word.equals("january") |
		 _word.equals("february") |
		 _word.equals("march") |
		 _word.equals("april") |
		 _word.equals("may") |
		 _word.equals("june") |
		 _word.equals("july") |
		 _word.equals("august") |
		 _word.equals("september") |
		 _word.equals("october") |
		 _word.equals("november") |
		 _word.equals("december")) {
		  return true;
	  }
	  return false;
  }
  
  /**
   * Get the 0-indexed number associated with Gregorian month represented by
   * the Token. Only applicable if the Token is a Gregorian month.
   * @return 0-indexed int representing the month
   */
  public int getMonthNumber() {
	  if(_word.equals("january")) {
		  return 0;
	  }
	  if(_word.equals("february")) {
		  return 1;
	  }
	  if(_word.equals("march")) {
		  return 2;
	  }
	  if(_word.equals("april")) {
		  return 3;
	  }
	  if(_word.equals("may")) {
		  return 4;
	  }
	  if(_word.equals("june")) {
		  return 5;
	  }
	  if(_word.equals("july")) {
		  return 6;
	  }
	  if(_word.equals("august")) {
		  return 7;
	  }
	  if(_word.equals("september")) {
		  return 8;
	  }
	  if(_word.equals("october")) {
		  return 9;
	  }
	  if(_word.equals("november")) {
		  return 10;
	  }
	  if(_word.equals("december")) {
		  return 11;
	  }
	  return -1;
}

  /**
   * Return the Tag that matches the given class
   */
  @SuppressWarnings("unchecked")
  public <T extends Tag> T getTag(Class<T> tagClass) {
    List<T> matches = getTags(tagClass);
    T matchingTag = null;
    if (matches.size() > 0) {
      matchingTag = matches.get(0);
    }
//    if (matches.size() >= 2) {
//      throw new IllegalStateException("Multiple identical tags found (" + matches + ")");
//    }
//    else if (matches.size() == 1) {
//      matchingTag = matches.get(0);
//    }
    return matchingTag;
  }

  public List<Tag<?>> getTags() {
    return _tags;
  }
  
  /**
   * Return the Tag that matches the given class
   */
  @SuppressWarnings("unchecked")
  public <T extends Tag<?>> List<T> getTags(Class<T> tagClass) {
    List<T> matches = new LinkedList<T>();
    Iterator<Tag<?>> tagIter = _tags.iterator();
    while (tagIter.hasNext()) {
      Tag<?> tag = tagIter.next();
      if (tagClass.isInstance(tag)) {
        matches.add((T)tag);
      }
    }
    return matches;
  }

  @Override
  public String toString() {
    return _word + " " + _tags;
  }
}
