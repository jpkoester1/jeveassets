/*
 * Copyright 2009-2023 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.eve.jeveasset.gui.shared.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.AllColumn;
import net.nikr.eve.jeveasset.gui.shared.filter.Filter.CompareType;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class FilterMatcherTest extends TestUtil {

	public enum TestEnum implements EnumTableColumn<Item> {
		TEXT(false, false),
		TEXT_FORMAT(false, false),
		LONG(true, false),
		INTEGER(true, false),
		DOUBLE(true, false),
		FLOAT(true, false),
		PERCENT(true, false),
		DATE(false, true),
		DATE_LAST(false, true),
		COLUMN_TEXT(false, false),
		COLUMN_NUMBER(true, false),
		COLUMN_PERCENT(true, false),
		COLUMN_DATE(false, true),
		NULL(true, true)
		;

		private final boolean number;
		private final boolean date;

		private TestEnum(final boolean number, final boolean date) {
			this.number = number;
			this.date = date;
		}

		public boolean isDate() {
			return date;
		}

		public boolean isNumber() {
			return number;
		}

		@Override
		public Class<?> getType() {
			return null;
		}

		@Override
		public Comparator<?> getComparator() {
			return null;
		}

		@Override
		public String getColumnName() {
			return null;
		}

		@Override
		public Object getColumnValue(Item from) {
			return null;
		}

		@Override
		public String toString() {
			return getColumnName();
		}

	}

	private String textColumn = null;
	private Number numberColumn = null;
	private Date dateColumn = null;
	private Percent percentColumn = null;
	private static final String TEXT = "Text";
	private static final String TEXT_FORMAT = "Text\"'-";
	private static final String TEXT_PART = "Tex";
	private static final String TEXT_NOT = "Not";

	private static final String DATE = "2005-01-02 09:00";
	private static final String DATE_BEFORE = "2005-01-03 09:00"; //DATE before this
	private static final String DATE_NOT_BEFORE = "2005-01-02 10:00";
	private static final String DATE_AFTER = "2005-01-01 9:00"; //DATE after this
	private static final String DATE_NOT_AFTER = "2005-01-02 8:00";
	private static final String DATE_PART = "2005";
	private static final String DATE_NOT = "2005-05-05";

	private static final double NUMBER_DOUBLE = 222.0d;
	private static final float NUMBER_FLOAT = 222.0f;
	private static final long NUMBER_LONG = 222L;
	private static final int NUMBER_INTEGER = 222;
	private static final Percent PERCENT = Percent.create(2.22);

	private static final String NULL = null;

	private final TestTableFormat filterControl = new TestTableFormat();
	private final Item item = new Item();

	@Test
	public void testTime() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			testMatches();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Filter time:" + (endTime - startTime) + "ms");
	}

	/**
	 * Speed test
	 * @param args
	 */
	public static void main(final String[] args) {
		Images.preload();
		FilterMatcherTest filterMatcherTest = new FilterMatcherTest();
		filterMatcherTest.testEqualsSingle();
		filterMatcherTest.testRexexSingle();
		filterMatcherTest.testEqualsAll();
		filterMatcherTest.testRexexAll();
		filterMatcherTest.testRexexAll();
		filterMatcherTest.testEqualsAll();
		filterMatcherTest.testRexexSingle();
		filterMatcherTest.testEqualsSingle();
	}

	private void testEqualsSingle() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			//Equals
			matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS, TEXT);
			matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS, TEXT_PART);
			matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS, TEXT_NOT);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Equals Single time:" + (endTime - startTime) + "ms");
	}

	private void testRexexSingle() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			//Regex
			matches(true,  TestEnum.TEXT, Filter.CompareType.REGEX, TEXT);
			matches(false, TestEnum.TEXT, Filter.CompareType.REGEX, TEXT_PART);
			matches(false, TestEnum.TEXT, Filter.CompareType.REGEX, TEXT_NOT);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Rexex Single time:" + (endTime - startTime) + "ms");
	}

	private void testEqualsAll() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			//Equals
			matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS, TEXT);
			matches(false, new AllColumn<>(), Filter.CompareType.EQUALS, TEXT_PART);
			matches(false, new AllColumn<>(), Filter.CompareType.EQUALS, TEXT_NOT);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Equals All time:" + (endTime - startTime) + "ms");
	}

	private void testRexexAll() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			//Regex
			matches(true,  TestEnum.TEXT, Filter.CompareType.REGEX, TEXT);
			matches(false, TestEnum.TEXT, Filter.CompareType.REGEX, TEXT_PART);
			matches(false, TestEnum.TEXT, Filter.CompareType.REGEX, TEXT_NOT);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Rexex All time:" + (endTime - startTime) + "ms");
	}

	/**
	 * Test of matches method, of class FilterControl.
	 */
	public void testMatches() {
	//String
		stringTest();
		stringFormatTest();
	//Numbers
		doubleTest();
		floatTest();
		longTest();
		integerTest();
	//Date
		dateTest();
	//Percent
		percentTest();
	//All
		allTest();
	//Logic
		logicTest();
	//Null
		nullTest();
	}

	private void matches(final boolean expected, final EnumTableColumn<Item> enumColumn, final CompareType compare, final String text) {
		matches(expected, enumColumn, compare, text, null, null, null, null);
	}

	private void matches(final boolean expected, final EnumTableColumn<Item> enumColumn, final CompareType compare, final String text, final String textColumn) {
		matches(expected, enumColumn, compare, text, textColumn, null, null, null);
	}

	private void matches(final boolean expected, final EnumTableColumn<Item> enumColumn, final CompareType compare, final String text, final Number numberColumn) {
		matches(expected, enumColumn, compare, text, null, numberColumn, null, null);
	}
	private void matches(final boolean expected, final EnumTableColumn<Item> enumColumn, final CompareType compare, final String text, final Date dateColumn) {
		matches(expected, enumColumn, compare, text, null, null, dateColumn, null);
	}
	private void matches(final boolean expected, final EnumTableColumn<Item> enumColumn, final CompareType compare, final String text, final Percent percentColumn) {
		matches(expected, enumColumn, compare, text, null, null, null, percentColumn);
	}

	private void matches(final boolean expected, final EnumTableColumn<Item> enumColumn, final CompareType compare, final String text, final String textColumn, final Number numberColumn, final Date dateColumn, final Percent percentColumn) {
		//Test matches
		this.textColumn = textColumn;
		this.numberColumn = numberColumn;
		this.dateColumn = dateColumn;
		this.percentColumn = percentColumn;
		FilterMatcher<Item> filterMatcher;
		filterMatcher = new FilterMatcher<>(filterControl, null, 1, Filter.LogicType.AND, enumColumn, compare, text, true);
		assertEquals("Matcher: value:" + text + " [" + compare + "]" + enumColumn.getColumnValue(item) + "(" + enumColumn.name() +  ")", expected, filterMatcher.matches(item));
		filterMatcher = new FilterMatcher<>(filterControl, null, new Filter(1, Filter.LogicType.AND, enumColumn, compare, text, true));
		assertEquals("Filter: " + enumColumn.name() + "  value:" + text, expected, filterMatcher.matches(item));
	}

	private void matches(final Object expected, String text1, String text2, String text3, String text4, String text5) {
		List<FilterMatcher<Item>> filterMatchers = new ArrayList<>();
		filterMatchers.add(new FilterMatcher<>(filterControl, null, 1, Filter.LogicType.OR, TestEnum.TEXT, CompareType.EQUALS, text1, true));
		filterMatchers.add(new FilterMatcher<>(filterControl, null, 1, Filter.LogicType.OR, TestEnum.TEXT, CompareType.EQUALS, text2, true));
		filterMatchers.add(new FilterMatcher<>(filterControl, null, 2, Filter.LogicType.OR, TestEnum.TEXT, CompareType.EQUALS, text3, true));
		filterMatchers.add(new FilterMatcher<>(filterControl, null, 2, Filter.LogicType.OR, TestEnum.TEXT, CompareType.EQUALS, text4, true));
		filterMatchers.add(new FilterMatcher<>(filterControl, null, 0, Filter.LogicType.AND, TestEnum.TEXT, CompareType.EQUALS, text5, true));
		FilterLogicalMatcher<Item> logicalMatcher = new FilterLogicalMatcher<>(filterMatchers);
		assertEquals("(" + text1 + " OR " + text2 +") AND (" + text3 + " OR " + text4 + ") AND " + text5 + " --> Matching: " + TEXT, expected, logicalMatcher.matches(item));
	}

	@Test
	public void logicTest() {
		matches(true, TEXT, TEXT_NOT, TEXT, TEXT_NOT, TEXT);          //(true OR false) AND (true OR false) AND true   = (true  + true  + true)  = true
		matches(true, TEXT_NOT, TEXT, TEXT_NOT, TEXT, TEXT);          //(false OR true) AND (false OR true) AND true   = (true  + true  + true)  = true
		matches(false, TEXT_NOT, TEXT, TEXT_NOT, TEXT, TEXT_NOT);     //(false OR true) AND (false OR true) AND false  = (true  + true  + false) = false
		matches(false, TEXT_NOT, TEXT_NOT, TEXT, TEXT_NOT, TEXT);     //(false OR false) AND (true OR false) AND true  = (false + true  + true)  = false
		matches(false, TEXT, TEXT_NOT, TEXT_NOT, TEXT_NOT, TEXT);     //(true OR false) AND (false OR false) AND true  = (true  + false + true)  = false
		matches(false, TEXT_NOT, TEXT_NOT, TEXT_NOT, TEXT_NOT, TEXT); //(false OR false) AND (false OR false) AND true = (false + false + true)  = false
	}

	@Test
	public void nullTest() {
		TestEnum[] columns = {TestEnum.TEXT, TestEnum.TEXT_FORMAT, TestEnum.LONG, TestEnum.INTEGER, TestEnum.DOUBLE, TestEnum.FLOAT, TestEnum.PERCENT, TestEnum.DATE, TestEnum.DATE_LAST, TestEnum.NULL};
		for (TestEnum column : columns) {
			matches(false, column, CompareType.EQUALS, NULL);
			matches(column != TestEnum.NULL,  column, CompareType.EQUALS_NOT, NULL);
			matches(false, column, CompareType.EQUALS_DATE, NULL);
			matches(column != TestEnum.NULL,  column, CompareType.EQUALS_NOT_DATE, NULL);
			matches(false, column, CompareType.CONTAINS, NULL);
			matches(column != TestEnum.NULL,  column, CompareType.CONTAINS_NOT, NULL);
			matches(false, column, CompareType.AFTER, NULL);
			matches(false, column, CompareType.BEFORE, NULL);
			matches(column != TestEnum.NULL,  column, CompareType.GREATER_THAN, NULL);
			matches(false, column, CompareType.LESS_THAN, NULL);
			matches(false, column, CompareType.LAST_HOURS, NULL);
			matches(false, column, CompareType.LAST_DAYS, NULL);
			matches(false,  column, CompareType.REGEX, NULL);
		}
		TestEnum[] columns2 = {TestEnum.COLUMN_DATE, TestEnum.COLUMN_NUMBER, TestEnum.COLUMN_PERCENT, TestEnum.COLUMN_TEXT};
		for (TestEnum column : columns2) {
			matches(false, column, CompareType.EQUALS_COLUMN, NULL, (String)null);
			matches(false, column, CompareType.EQUALS_NOT_COLUMN, NULL, (String)null);
			matches(false, column, CompareType.CONTAINS_COLUMN, NULL, (String)null);
			matches(false, column, CompareType.CONTAINS_NOT_COLUMN, NULL, (String)null);
			matches(false, column, CompareType.AFTER_COLUMN, NULL, (Date)null);
			matches(false, column, CompareType.BEFORE_COLUMN, NULL, (Date)null);
			matches(false, column, CompareType.GREATER_THAN_COLUMN, NULL, (Number)null);
			matches(false, column, CompareType.LESS_THAN_COLUMN, NULL, (Number)null);
		}
		for (CompareType compareType : CompareType.values()) {
			matches(true,  new AllColumn<>(), compareType, NULL);
		}
	}

	@Test
	public void dateTest() {
		//Equals
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS, DATE);
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS, DATE_NOT_AFTER);
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS, DATE_NOT_BEFORE);
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS, DATE_PART);
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS, DATE_NOT);
		//Equals not
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_NOT, DATE);
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_NOT, DATE_PART);
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_NOT, DATE_NOT);
		//Equals date
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE);
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE_NOT_AFTER);
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE_NOT_BEFORE);
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE_PART);
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_DATE, DATE_NOT);
		//Equals not date
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_NOT_DATE, DATE);
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_NOT_DATE, DATE_PART);
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_NOT_DATE, DATE_NOT);
		//Contains
		matches(true,  TestEnum.DATE, Filter.CompareType.CONTAINS, DATE);
		matches(true,  TestEnum.DATE, Filter.CompareType.CONTAINS, DATE_PART);
		matches(false, TestEnum.DATE, Filter.CompareType.CONTAINS, DATE_NOT);
		//Contains not
		matches(false, TestEnum.DATE, Filter.CompareType.CONTAINS_NOT, DATE);
		matches(false, TestEnum.DATE, Filter.CompareType.CONTAINS_NOT, DATE_PART);
		matches(true,  TestEnum.DATE, Filter.CompareType.CONTAINS_NOT, DATE_NOT);
		//Regex
		matches(true,  TestEnum.DATE, Filter.CompareType.REGEX, DATE);
		matches(true,  TestEnum.DATE, Filter.CompareType.REGEX, DATE_PART);
		matches(false, TestEnum.DATE, Filter.CompareType.REGEX, DATE_NOT);
		//Before
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE, DATE);
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE, DATE_AFTER);
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE, DATE_NOT_BEFORE);
		matches(true,  TestEnum.DATE, Filter.CompareType.BEFORE, DATE_BEFORE);
		//After
		matches(false, TestEnum.DATE, Filter.CompareType.AFTER, DATE);
		matches(true,  TestEnum.DATE, Filter.CompareType.AFTER, DATE_AFTER);
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE, DATE_NOT_AFTER);
		matches(false, TestEnum.DATE, Filter.CompareType.AFTER, DATE_BEFORE);
		//Last X Days
		matches(false, TestEnum.DATE_LAST, Filter.CompareType.LAST_DAYS, "1"); //Last 1 days
		matches(true,  TestEnum.DATE_LAST, Filter.CompareType.LAST_DAYS, "2"); //Last 2 days
		//Last X Hours
		matches(false, TestEnum.DATE_LAST, Filter.CompareType.LAST_HOURS, "24"); //Last 24 hours
		matches(true,  TestEnum.DATE_LAST, Filter.CompareType.LAST_HOURS, "48"); //Last 48 hours

		//Equals column
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE));
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE_NOT_AFTER));
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE_NOT_BEFORE));
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE_NOT));
		//Equals not column
		matches(false, TestEnum.DATE, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE));
		matches(true,  TestEnum.DATE, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE_NOT));
		//Contains column
		matches(true,  TestEnum.DATE, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE));
		matches(false, TestEnum.DATE, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE_NOT));
		//Contains not column
		matches(false, TestEnum.DATE, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE));
		matches(true,  TestEnum.DATE, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE_NOT));
		//Before column
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE));
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE_AFTER));
		matches(false, TestEnum.DATE, Filter.CompareType.BEFORE_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE_NOT_BEFORE));
		matches(true,  TestEnum.DATE, Filter.CompareType.BEFORE_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE_BEFORE));
		//After column
		matches(false, TestEnum.DATE, Filter.CompareType.AFTER_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE));
		matches(true,  TestEnum.DATE, Filter.CompareType.AFTER_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE_AFTER));
		matches(false, TestEnum.DATE, Filter.CompareType.AFTER_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE_NOT_AFTER));
		matches(false, TestEnum.DATE, Filter.CompareType.AFTER_COLUMN, TestEnum.COLUMN_DATE.name(), Formatter.columnStringToDate(DATE_BEFORE));
	}

	@Test
	public void stringTest() {
		//Equals
		matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS, TEXT);
		matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS, TEXT_PART);
		matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS, TEXT_NOT);
		//Equals not
		matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS_NOT, TEXT);
		matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS_NOT, TEXT_PART);
		matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS_NOT, TEXT_NOT);
		//Contains
		matches(true,  TestEnum.TEXT, Filter.CompareType.CONTAINS, TEXT);
		matches(true,  TestEnum.TEXT, Filter.CompareType.CONTAINS, TEXT_PART);
		matches(false, TestEnum.TEXT, Filter.CompareType.CONTAINS, TEXT_NOT);
		//Contains not
		matches(false, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT, TEXT);
		matches(false, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT, TEXT_PART);
		matches(true,  TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT, TEXT_NOT);
		//Regex
		matches(true,  TestEnum.TEXT, Filter.CompareType.REGEX, TEXT);
		matches(true,  TestEnum.TEXT, Filter.CompareType.REGEX, TEXT_PART);
		matches(false, TestEnum.TEXT, Filter.CompareType.REGEX, TEXT_NOT);
		//Equals column
		matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT);
		matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_PART);
		matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_NOT);
		//Equals not
		matches(false, TestEnum.TEXT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT);
		matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_PART);
		matches(true,  TestEnum.TEXT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_NOT);
		//Contains
		matches(true,  TestEnum.TEXT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT);
		matches(true,  TestEnum.TEXT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_PART);
		matches(false, TestEnum.TEXT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_NOT);
		//Contains not
		matches(false, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT);
		matches(false, TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_PART);
		matches(true,  TestEnum.TEXT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_TEXT.name(), TEXT_NOT);
	}

	@Test
	public void stringFormatTest() {
		//Equals
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.EQUALS, TEXT + "\"'-");
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.EQUALS, TEXT + "„‘–");
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.EQUALS, TEXT + "“’‐");
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.EQUALS, TEXT + "”`‑");
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.EQUALS, TEXT + "\"´‒");
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.EQUALS, TEXT + "\"'—");
		//Contains
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "\"");
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "'");
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "-");
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "„"); //Index
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "“"); //Set transmit state
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "”"); //Cancel character
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "‘"); //Private use one
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "’"); //Private use two
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "`"); //Grave accent
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "´"); //Acute accent
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "–"); //En dash
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "‐"); //Hyphen
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "‑"); //Non-breaking hyphen
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "‒"); //Figure dEash
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.CONTAINS, "—"); //Em dash
		//Regex
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "\"");
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "'");
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "-");
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "„"); //Index
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "“"); //Set transmit state
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "”"); //Cancel character
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "‘"); //Private use one
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "’"); //Private use two
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "`"); //Grave accent
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "´"); //Acute accent
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "–"); //En dash
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "‐"); //Hyphen
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "‑"); //Non-breaking hyphen
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "‒"); //Figure dash
		matches(true,  TestEnum.TEXT_FORMAT, Filter.CompareType.REGEX, "—"); //Em dash
		//All - Regex
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "\"");
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "'");
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "-");
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "„"); //Index
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "“"); //Set transmit state
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "”"); //Cancel character
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "‘"); //Private use one
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "’"); //Private use two
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "`"); //Grave accent
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "´"); //Acute accent
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "–"); //En dash
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "‐"); //Hyphen
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "‑"); //Non-breaking hyphen
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "‒"); //Figure dash
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "—"); //Em dash
		//All - Contains
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "\"");
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "'");
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "-");
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "„"); //Index
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "“"); //Set transmit state
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "”"); //Cancel character
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "‘"); //Private use one
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "’"); //Private use two
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "`"); //Grave accent
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "´"); //Acute accent
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "–"); //En dash
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "‐"); //Hyphen
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "‑"); //Non-breaking hyphen
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "‒"); //Figure dash
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "—"); //Em dash
		//All - Equals
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS, TEXT + "\"'-");
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS, TEXT + "„‘–");
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS, TEXT + "“’‐");
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS, TEXT + "”`‑");
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS, TEXT + "\"´‒");
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS, TEXT + "\"'—");
	}

	@Test
	public void doubleTest() {
		numberTest(TestEnum.DOUBLE);
	}

	@Test
	public void floatTest() {
		numberTest(TestEnum.FLOAT);
	}

	@Test
	public void longTest() {
		numberTest(TestEnum.LONG);
	}

	@Test
	public void integerTest() {
		numberTest(TestEnum.INTEGER);
	}

	private void numberTest(final TestEnum testEnum) {
		//Equals
		matches(true,  testEnum, Filter.CompareType.EQUALS, "222");
		matches(true,  testEnum, Filter.CompareType.EQUALS, "222.0");
		matches(false, testEnum, Filter.CompareType.EQUALS, "223");
		matches(false, testEnum, Filter.CompareType.EQUALS, "223.1");
		matches(false, testEnum, Filter.CompareType.EQUALS, "222.1");
		//Equals not
		matches(true,  testEnum, Filter.CompareType.EQUALS_NOT, "223");
		matches(true,  testEnum, Filter.CompareType.EQUALS_NOT, "223.1");
		matches(true,  testEnum, Filter.CompareType.EQUALS_NOT, "222.1");
		matches(false, testEnum, Filter.CompareType.EQUALS_NOT, "222");
		matches(false, testEnum, Filter.CompareType.EQUALS_NOT, "222.0");
		//Contains
		matches(true,  testEnum, Filter.CompareType.CONTAINS, "222");
		matches(true,  testEnum, Filter.CompareType.CONTAINS, "222.0");
		matches(false, testEnum, Filter.CompareType.CONTAINS, "223");
		matches(false, testEnum, Filter.CompareType.CONTAINS, "223.1");
		matches(false, testEnum, Filter.CompareType.CONTAINS, "222.1");
		//Contains not
		matches(true,  testEnum, Filter.CompareType.CONTAINS_NOT, "223");
		matches(true,  testEnum, Filter.CompareType.CONTAINS_NOT, "223.1");
		matches(true,  testEnum, Filter.CompareType.CONTAINS_NOT, "222.1");
		matches(false, testEnum, Filter.CompareType.CONTAINS_NOT, "222");
		matches(false, testEnum, Filter.CompareType.CONTAINS_NOT, "222.0");
		//Regex
		matches(true,  testEnum, Filter.CompareType.REGEX, "222");
		matches(false, testEnum, Filter.CompareType.REGEX, "222.0");
		matches(false, testEnum, Filter.CompareType.REGEX, "223");
		matches(false, testEnum, Filter.CompareType.REGEX, "223.1");
		matches(false, testEnum, Filter.CompareType.REGEX, "222.1");
		//Great than
		matches(false, testEnum, Filter.CompareType.GREATER_THAN, "222.0");
		matches(false, testEnum, Filter.CompareType.GREATER_THAN, "222");
		matches(false, testEnum, Filter.CompareType.GREATER_THAN, "222.1");
		matches(false, testEnum, Filter.CompareType.GREATER_THAN, "223");
		matches(true,  testEnum, Filter.CompareType.GREATER_THAN, "221.0");
		matches(true,  testEnum, Filter.CompareType.GREATER_THAN, "221.9");
		matches(true,  testEnum, Filter.CompareType.GREATER_THAN, "221");
		//Less than
		matches(false, testEnum, Filter.CompareType.LESS_THAN, "222.0");
		matches(false, testEnum, Filter.CompareType.LESS_THAN, "222");
		matches(true,  testEnum, Filter.CompareType.LESS_THAN, "222.1");
		matches(true,  testEnum, Filter.CompareType.LESS_THAN, "223");
		matches(false, testEnum, Filter.CompareType.LESS_THAN, "221.0");
		matches(false, testEnum, Filter.CompareType.LESS_THAN, "221.9");
		matches(false, testEnum, Filter.CompareType.LESS_THAN, "221");
		//Equals column
		matches(true,  testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(true,  testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		matches(false, testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(false, testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223.1);
		matches(false, testEnum, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		//Equals not column
		matches(true,  testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(true,  testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223.1);
		matches(true,  testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		matches(false, testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(false, testEnum, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		//Contains column
		matches(true,  testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(true,  testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		matches(false, testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(false, testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223.1);
		matches(false, testEnum, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		//Contains not column
		matches(true,  testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(true,  testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223.1);
		matches(true,  testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		matches(false, testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(false, testEnum, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		//Great than column
		matches(false, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		matches(false, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(false, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		matches(false, testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(true,  testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221.0);
		matches(true,  testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221.9);
		matches(true,  testEnum, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221);
		//Less than column
		matches(false, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.0);
		matches(false, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222);
		matches(true,  testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 222.1);
		matches(true,  testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 223);
		matches(false, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221.0);
		matches(false, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221.9);
		matches(false, testEnum, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_NUMBER.name(), 221);
	}

	@Test
	public void percentTest() {
		//Equals
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS, "222%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS, "222.0%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS, "223%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS, "223.1%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS, "222.1%");
		//Equals not
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "223%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "223.1%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222.1%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222.0%");
		//Contains
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222.0%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "223%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "223.1%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222.1%");
		//Contains not
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "223%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "223.1%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222.1%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222.0%");
		//Regex
		matches(true,  TestEnum.PERCENT, Filter.CompareType.REGEX, "222");
		matches(false, TestEnum.PERCENT, Filter.CompareType.REGEX, "222.0%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.REGEX, "223%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.REGEX, "223.1%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.REGEX, "222.1%");
		//Great than
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222.0%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222.1%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "223%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221.0%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221.9%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221%");
		//Less than
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222.0%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222.1%");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "223%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221.0%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221.9%");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221%");
		//Equals
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS, "222");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS, "222.0");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS, "223");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS, "223.1");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS, "222.1");
		//Equals not
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "223");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "223.1");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222.1");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222");
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT, "222.0");
		//Contains
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222.0");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "223");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "223.1");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS, "222.1");
		//Contains not
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "223");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "223.1");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222.1");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222");
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT, "222.0");
		//Great than
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222.0");
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222");
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "222.1");
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "223");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221.0");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221.9");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN, "221");
		//Less than
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222.0");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "222.1");
		matches(true,  TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "223");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221.0");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221.9");
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN, "221");
		//Equals column
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.22));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.220));
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.23));
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.231));
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.221));
		//Equals not column
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.23));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.231));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.221));
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.22));
		matches(false, TestEnum.PERCENT, Filter.CompareType.EQUALS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.220));
		//Contains column
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.22));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.220));
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.23));
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.231));
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.221));
		//Contains not column
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.23));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.231));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.221));
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.22));
		matches(false, TestEnum.PERCENT, Filter.CompareType.CONTAINS_NOT_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.220));
		//Great than column
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.220));
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.22));
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.221));
		matches(false, TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.23));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.210));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.219));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.GREATER_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.21));
		//Less than column
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.220));
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.22));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.221));
		matches(true,  TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.23));
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.210));
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.219));
		matches(false, TestEnum.PERCENT, Filter.CompareType.LESS_THAN_COLUMN, TestEnum.COLUMN_PERCENT.name(), Percent.create(2.21));
	}

	@Test
	public void allTest() {
	//Text
		//Equals
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS, TEXT);
		matches(false, new AllColumn<>(), Filter.CompareType.EQUALS, TEXT_PART);
		matches(false, new AllColumn<>(), Filter.CompareType.EQUALS, TEXT_NOT);
		//Equals not
		matches(false, new AllColumn<>(), Filter.CompareType.EQUALS_NOT, TEXT);
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS_NOT, TEXT_PART);
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS_NOT, TEXT_NOT);
		//Contains
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, TEXT);
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, TEXT_PART);
		matches(false, new AllColumn<>(), Filter.CompareType.CONTAINS, TEXT_NOT);
		//Contains not
		matches(false, new AllColumn<>(), Filter.CompareType.CONTAINS_NOT, TEXT);
		matches(false, new AllColumn<>(), Filter.CompareType.CONTAINS_NOT, TEXT_PART);
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS_NOT, TEXT_NOT);
		//Regex
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, TEXT);
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, TEXT_PART);
		matches(false, new AllColumn<>(), Filter.CompareType.REGEX, TEXT_NOT);
	//Number
		//Equals
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS, "222");
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS, "222.0");
		matches(false, new AllColumn<>(), Filter.CompareType.EQUALS, "223");
		matches(false, new AllColumn<>(), Filter.CompareType.EQUALS, "223.1");
		matches(false, new AllColumn<>(), Filter.CompareType.EQUALS, "222.1");
		//Equals not
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS_NOT, "223");
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS_NOT, "223.1");
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS_NOT, "222.1");
		matches(false, new AllColumn<>(), Filter.CompareType.EQUALS_NOT, "222");
		matches(false, new AllColumn<>(), Filter.CompareType.EQUALS_NOT, "222.0");
		//Contains
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "222");
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, "222.0");
		matches(false, new AllColumn<>(), Filter.CompareType.CONTAINS, "223");
		matches(false, new AllColumn<>(), Filter.CompareType.CONTAINS, "223.1");
		matches(false, new AllColumn<>(), Filter.CompareType.CONTAINS, "222.1");
		//Contains not
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS_NOT, "223");
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS_NOT, "223.1");
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS_NOT, "222.1");
		matches(false, new AllColumn<>(), Filter.CompareType.CONTAINS_NOT, "222");
		matches(false, new AllColumn<>(), Filter.CompareType.CONTAINS_NOT, "222.0");
		//Contains
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, "222");
		matches(false, new AllColumn<>(), Filter.CompareType.REGEX, "222\\.0");
		matches(false, new AllColumn<>(), Filter.CompareType.REGEX, "223");
		matches(false, new AllColumn<>(), Filter.CompareType.REGEX, "223\\.1");
		matches(false, new AllColumn<>(), Filter.CompareType.REGEX, "222\\.1");
	//Date
		//Equals
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS, DATE);
		matches(false, new AllColumn<>(), Filter.CompareType.EQUALS, DATE_PART);
		matches(false, new AllColumn<>(), Filter.CompareType.EQUALS, DATE_NOT);
		//Equals not
		matches(false, new AllColumn<>(), Filter.CompareType.EQUALS_NOT, DATE);
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS_NOT, DATE_PART);
		matches(true,  new AllColumn<>(), Filter.CompareType.EQUALS_NOT, DATE_NOT);
		//Contains
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, DATE);
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS, DATE_PART);
		matches(false, new AllColumn<>(), Filter.CompareType.CONTAINS, DATE_NOT);
		//Contains not
		matches(false, new AllColumn<>(), Filter.CompareType.CONTAINS_NOT, DATE);
		matches(false, new AllColumn<>(), Filter.CompareType.CONTAINS_NOT, DATE_PART);
		matches(true,  new AllColumn<>(), Filter.CompareType.CONTAINS_NOT, DATE_NOT);
		//Contains
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, DATE);
		matches(true,  new AllColumn<>(), Filter.CompareType.REGEX, DATE_PART);
		matches(false, new AllColumn<>(), Filter.CompareType.REGEX, DATE_NOT);
	}

	public static class Item { }

	public class TestTableFormat implements SimpleTableFormat<Item> {

		@Override
		public EnumTableColumn<Item> valueOf(final String column) {
			return TestEnum.valueOf(column);
		}

		@Override
		public Object getColumnValue(final Item item, final String columnString) {
			EnumTableColumn<?> column = valueOf(columnString);
			if (column instanceof TestEnum) {
				TestEnum format = (TestEnum) column;
				switch (format) {
					case TEXT:
						return TEXT;
					case TEXT_FORMAT:
						return TEXT_FORMAT;
					case DOUBLE:
						return NUMBER_DOUBLE;
					case FLOAT:
						return NUMBER_FLOAT;
					case LONG:
						return NUMBER_LONG;
					case INTEGER:
						return NUMBER_INTEGER;
					case PERCENT:
						return PERCENT;
					case DATE:
						return Formatter.columnStringToDate(DATE);
					case DATE_LAST:
						Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
						//minus 47 hours
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 0);
						calendar.add(Calendar.HOUR_OF_DAY, +1);
						calendar.add(Calendar.DAY_OF_MONTH, -2);
						return calendar.getTime();
					case COLUMN_TEXT:
						return textColumn;
					case COLUMN_NUMBER:
						return numberColumn;
					case COLUMN_PERCENT:
						return percentColumn;
					case COLUMN_DATE:
						return dateColumn;
					case NULL:
						return null;
					default:
						break;
				}
			}
			return null;
		}

		@Override
		public List<EnumTableColumn<Item>> getAllColumns() {
			return new ArrayList<>(Arrays.asList(TestEnum.values()));
		}

		@Override
		public List<EnumTableColumn<Item>> getShownColumns() {
			return null; //Only used by the GUI
		}

		@Override
		public void addColumn(EnumTableColumn<Item> column) { }
	}
}
