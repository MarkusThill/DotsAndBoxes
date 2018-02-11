package com.opencsv.bean;

import com.opencsv.CSVReader;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringReader;

public class CsvToBeanTest {

   private static final String TEST_STRING = "name,orderNumber,num\n" +
         "kyle,abc123456,123\n" +
         "jimmy,def098765,456 ";

   private CSVReader createReader() {
      StringReader reader = new StringReader(TEST_STRING);
      return new CSVReader(reader);
   }

   private MappingStrategy createErrorMappingStrategy() {
      return new MappingStrategy() {

         public PropertyDescriptor findDescriptor(int col) throws IntrospectionException {
            return null;
         }

         public Object createBean() throws InstantiationException, IllegalAccessException {
            return null;
         }

         public void captureHeader(CSVReader reader) throws IOException {
            throw new IOException("This is the test exception");
         }

         public Integer getColumnIndex(String name) {
            return null;
         }
      };
   }

   @Test(expected = RuntimeException.class)
   public void throwRuntimeExceptionWhenExceptionIsThrown() {
      CsvToBean bean = new CsvToBean();
      bean.parse(createErrorMappingStrategy(), createReader());
   }
}
