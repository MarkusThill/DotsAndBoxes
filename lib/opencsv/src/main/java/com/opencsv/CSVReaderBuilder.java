/**
 Copyright 2005 Bytecode Pty Ltd.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.opencsv;


import java.io.Reader;

/**
 * Builder for creating a CSVReader.
 *
 * <code>
 * final CSVParser parser =
 * new CSVParserBuilder()
 * .withSeparator('\t')
 * .withIgnoreQuotations(true)
 * .build();
 * final CSVReader reader =
 * new CSVReaderBuilder(new StringReader(csv))
 * .withSkipLines(1)
 * .withCSVParser(parser)
 * .build();
 * </code>
 *
 * @see CSVReader
 */
public class CSVReaderBuilder {

    private final Reader reader;
    private int skipLines = CSVReader.DEFAULT_SKIP_LINES;
    /*@Nullable*/private CSVParser csvParser = null;

   /**
    * Sets the reader to an underlying CSV source.
    *
    * @param reader the reader to an underlying CSV source.
    */
   CSVReaderBuilder(
         final Reader reader) {
      if (reader == null) {
         throw new IllegalArgumentException("Reader may not be null");
      }
      this.reader = reader;
   }

    /**
     * Used by unit tests.
     *
     * @return the reader.
     */
    protected Reader getReader() {
        return reader;
    }

    /**
     * used by unit tests.
     *
     * @return The set number of lines to skip
     */
    protected int getSkipLines() {
        return skipLines;
    }

    /**
     * used by unit tests.
     *
     * @return the csvParser used by the builder.
     */
    protected CSVParser getCsvParser() {
        return csvParser;
    }

    /**
    * Sets the line number to skip for start reading.
     *
     * @param skipLines the line number to skip for start reading.
     * @return the CSVReaderBuilder with skipLines set.
    */
   CSVReaderBuilder withSkipLines(
         final int skipLines) {
      this.skipLines = (skipLines <= 0 ? 0 : skipLines);
      return this;
   }


    /**
     * Sets the parser to use to parse the input.
     *
     * @param csvParser the parser to use to parse the input.
     * @return the CSVReaderBuilder with the CSVParser set.
    */
   CSVReaderBuilder withCSVParser(
         final /*@Nullable*/ CSVParser csvParser) {
      this.csvParser = csvParser;
       return this;
   }


    /**
     * Creates the CSVParser.
     * @return the CSVParser based on the set criteria.
     */
   CSVReader build() {
      final CSVParser parser =
            (csvParser != null ? csvParser : new CSVParser());
      return new CSVReader(reader, skipLines, parser);
   }
}
