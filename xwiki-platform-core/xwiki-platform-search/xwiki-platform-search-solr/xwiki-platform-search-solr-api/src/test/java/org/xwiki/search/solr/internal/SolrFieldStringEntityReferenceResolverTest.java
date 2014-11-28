/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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
package org.xwiki.search.solr.internal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Rule;
import org.junit.Test;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.ClassPropertyReference;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReferenceResolver;
import org.xwiki.model.reference.EntityReferenceValueProvider;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

/**
 * Unit tests for {@link SolrFieldStringEntityReferenceResolver}.
 * 
 * @version $Id$
 * @since 5.3RC1
 */
public class SolrFieldStringEntityReferenceResolverTest
{
    @Rule
    public MockitoComponentMockingRule<EntityReferenceResolver<String>> mocker =
        new MockitoComponentMockingRule<EntityReferenceResolver<String>>(SolrFieldStringEntityReferenceResolver.class);

    @Test
    public void resolve() throws Exception
    {
        EntityReferenceResolver<String> resolver = mocker.getComponentUnderTest();

        assertEquals(new ClassPropertyReference("title", new DocumentReference("wiki", "My Space", "A Class")),
            new ClassPropertyReference(resolver.resolve("wiki.My Space.A Class.title", EntityType.CLASS_PROPERTY)));

        assertEquals(
            new ClassPropertyReference("ti.tle", new DocumentReference("w.iki", "My.Space", "A.Class")),
            new ClassPropertyReference(resolver.resolve("w..iki.My..Space.A..Class.ti..tle", EntityType.CLASS_PROPERTY)));
        assertEquals(new SpaceReference("a..z", new WikiReference("from")),
            new SpaceReference(resolver.resolve("from.a....z", EntityType.SPACE)));

        // Relative reference resolved based on the given parameters.
        assertEquals(
            new ClassPropertyReference("title", new DocumentReference("foo", "My Space", "A Class")),
            new ClassPropertyReference(resolver.resolve("A Class.title", EntityType.CLASS_PROPERTY, new SpaceReference(
                "My Space", new WikiReference("foo")))));

        // Relative reference resolve based on the current entity.
        EntityReferenceValueProvider currentEntityReferenceValueProvider =
            mocker.getInstance(EntityReferenceValueProvider.class, "current");
        when(currentEntityReferenceValueProvider.getDefaultValue(EntityType.SPACE)).thenReturn("My Space");
        assertEquals(
            new ClassPropertyReference("title", new DocumentReference("bar", "My Space", "A Class")),
            new ClassPropertyReference(resolver.resolve("A Class.title", EntityType.CLASS_PROPERTY, new WikiReference(
                "bar"))));
    }
}
