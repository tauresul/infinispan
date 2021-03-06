/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.infinispan.query.impl;

import org.hibernate.search.query.engine.spi.DocumentExtractor;
import org.hibernate.search.query.engine.spi.EntityInfo;
import org.hibernate.search.query.engine.spi.HSQuery;
import org.infinispan.AdvancedCache;
import org.infinispan.query.backend.KeyTransformationHandler;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Simple test for checking the Iterator Initialization with wrong fetch size.
 *
 * @author Anna Manukyan
 */
@Test(groups = "functional", testName = "query.impl.InvalidIteratorTest")
public class InvalidIteratorTest {
   private List<EntityInfo> entityInfos = new ArrayList<EntityInfo>();
   private AdvancedCache<String, String> cache;

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testLazyIteratorInitWithInvalidFetchSize() throws IOException {
      DocumentExtractor extractor = mock(DocumentExtractor.class);
      when(extractor.extract(anyInt())).thenAnswer(new Answer<EntityInfo>() {
         @Override
         public EntityInfo answer(InvocationOnMock invocation) throws Throwable {
            int index = (Integer) invocation.getArguments()[0];
            return entityInfos.get(index);
         }
      });

      HSQuery hsQuery = mock(HSQuery.class);
      cache = mock(AdvancedCache.class);
      when(hsQuery.queryDocumentExtractor()).thenReturn(extractor);
      when(hsQuery.queryResultSize()).thenReturn(entityInfos.size());

      LazyIterator iterator = new LazyIterator(hsQuery, new EntityLoader(cache, new KeyTransformationHandler()), getFetchSize());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testEagerIteratorInitWithInvalidFetchSize() throws IOException {
      DocumentExtractor extractor = mock(DocumentExtractor.class);
      when(extractor.extract(anyInt())).thenAnswer(new Answer<EntityInfo>() {
         @Override
         public EntityInfo answer(InvocationOnMock invocation) throws Throwable {
            int index = (Integer) invocation.getArguments()[0];
            return entityInfos.get(index);
         }
      });

      HSQuery hsQuery = mock(HSQuery.class);
      cache = mock(AdvancedCache.class);
      when(hsQuery.queryDocumentExtractor()).thenReturn(extractor);
      when(hsQuery.queryResultSize()).thenReturn(entityInfos.size());

      EagerIterator iterator = new EagerIterator(entityInfos, new EntityLoader(cache, new KeyTransformationHandler()), getFetchSize());
   }

   private int getFetchSize() {
      return 0;
   }
}