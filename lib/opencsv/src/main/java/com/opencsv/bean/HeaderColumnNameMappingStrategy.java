package com.opencsv.bean;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * Copyright 2007 Kyle Miller.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Maps data to objects using the column names in the first row of the csv
 * file as reference.  This way the column order does not matter.
 *
 * @param <T>
 */

public class HeaderColumnNameMappingStrategy<T> implements MappingStrategy<T> {
   protected String[] header;
   protected Map<String, Integer> indexLookup = new HashMap<>();
   protected Map<String, PropertyDescriptor> descriptorMap = null;
   protected Class<T> type;

   public void captureHeader(CSVReader reader) throws IOException {
      header = reader.readNext();
   }

   protected void createIndexLookup(String[] values) {
      if (indexLookup.isEmpty()) {
         for (int i = 0; i < values.length; i++) {
            indexLookup.put(values[i], i);
         }
      }
   }

   protected void resetIndexMap() {
      indexLookup.clear();
   }

   /**
    * Gets the column index that corresponds to a specific colum name.
    * If the CSV file doesn't have a header row, this method will always return
    * null.
    *
    * @param name the column name
    * @return the column index, or null if the name doesn't exist
    * @throws IllegalStateException if the CSV file has a header row and it
    *                               hasn't been read yet.
    */
   public Integer getColumnIndex(String name) {
      if (null == header) {
         throw new IllegalStateException("The header row hasn't been read yet.");
      }

      createIndexLookup(header);

      return indexLookup.get(name);
   }

   public PropertyDescriptor findDescriptor(int col) throws IntrospectionException {
      String columnName = getColumnName(col);
      return (StringUtils.isNotBlank(columnName)) ? findDescriptor(columnName) : null;
   }

   protected String getColumnName(int col) {
      return (null != header && col < header.length) ? header[col] : null;
   }

   protected PropertyDescriptor findDescriptor(String name) throws IntrospectionException {
      if (null == descriptorMap) {
         descriptorMap = loadDescriptorMap(); //lazy load descriptors
      }
      return descriptorMap.get(name.toUpperCase().trim());
   }

   protected boolean matches(String name, PropertyDescriptor desc) {
      return desc.getName().equals(name.trim());
   }

   protected Map<String, PropertyDescriptor> loadDescriptorMap() throws IntrospectionException {
      Map<String, PropertyDescriptor> map = new HashMap<>();

      PropertyDescriptor[] descriptors;
      descriptors = loadDescriptors(getType());
      for (PropertyDescriptor descriptor : descriptors) {
         map.put(descriptor.getName().toUpperCase().trim(), descriptor);
      }

      return map;
   }

   private PropertyDescriptor[] loadDescriptors(Class<T> cls) throws IntrospectionException {
      BeanInfo beanInfo = Introspector.getBeanInfo(cls);
      return beanInfo.getPropertyDescriptors();
   }

   public T createBean() throws InstantiationException, IllegalAccessException {
      return type.newInstance();
   }

   public Class<T> getType() {
      return type;
   }

   public void setType(Class<T> type) {
      this.type = type;
   }
}
