package com.opencsv;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CSVParserBuilderTest {

   private CSVParserBuilder builder;

   @Before
   public void setUp() throws Exception {
      builder = new CSVParserBuilder();
   }

   @Test
   public void testDefaultBuilder() {
      assertEquals(
            CSVParser.DEFAULT_SEPARATOR,
              builder.getSeparator());
      assertEquals(
            CSVParser.DEFAULT_QUOTE_CHARACTER,
              builder.getQuoteChar());
      assertEquals(
            CSVParser.DEFAULT_ESCAPE_CHARACTER,
              builder.getEscapeChar());
      assertEquals(
            CSVParser.DEFAULT_STRICT_QUOTES,
              builder.isStrictQuotes());
      assertEquals(
            CSVParser.DEFAULT_IGNORE_LEADING_WHITESPACE,
              builder.isIgnoreLeadingWhiteSpace());
      assertEquals(
            CSVParser.DEFAULT_IGNORE_QUOTATIONS,
              builder.isIgnoreQuotations());

      CSVParser parser = builder.build();
      assertEquals(
            CSVParser.DEFAULT_SEPARATOR,
              parser.getSeparator());
      assertEquals(
            CSVParser.DEFAULT_QUOTE_CHARACTER,
              parser.getQuotechar());
      assertEquals(
            CSVParser.DEFAULT_ESCAPE_CHARACTER,
              parser.getEscape());
      assertEquals(
            CSVParser.DEFAULT_STRICT_QUOTES,
              parser.isStrictQuotes());
      assertEquals(
            CSVParser.DEFAULT_IGNORE_LEADING_WHITESPACE,
              parser.isIgnoreLeadingWhiteSpace());
      assertEquals(
            CSVParser.DEFAULT_IGNORE_QUOTATIONS,
              parser.isIgnoreQuotations());
   }

   @Test
   public void testWithSeparator() {
      final char expected = '1';
      builder.withSeparator(expected);
       assertEquals(expected, builder.getSeparator());
       assertEquals(expected, builder.build().getSeparator());
   }

   @Test
   public void testWithQuoteChar() {
      final char expected = '2';
      builder.withQuoteChar(expected);
       assertEquals(expected, builder.getQuoteChar());
       assertEquals(expected, builder.build().getQuotechar());
   }

   @Test
   public void testWithEscapeChar() {
      final char expected = '3';
      builder.withEscapeChar(expected);
       assertEquals(expected, builder.getEscapeChar());
       assertEquals(expected, builder.build().getEscape());
   }

   @Test
   public void testWithStrictQuotes() {
      final boolean expected = true;
      builder.withStrictQuotes(expected);
       assertEquals(expected, builder.isStrictQuotes());
       assertEquals(expected, builder.build().isStrictQuotes());
   }

   @Test
   public void testWithIgnoreLeadingWhiteSpace() {
      final boolean expected = true;
      builder.withIgnoreLeadingWhiteSpace(expected);
       assertEquals(expected, builder.isIgnoreLeadingWhiteSpace());
       assertEquals(expected, builder.build().isIgnoreLeadingWhiteSpace());
   }

   @Test
   public void testWithIgnoreQuotations() {
      final boolean expected = true;
      builder.withIgnoreQuotations(expected);
       assertEquals(expected, builder.isIgnoreQuotations());
       assertEquals(expected, builder.build().isIgnoreQuotations());
   }
}
