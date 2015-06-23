/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bson.codecs.configuration.mapper;

import com.fasterxml.classmate.TypeResolver;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.assertEquals;

public class ClassModelCodecTest {

    private CodecRegistry registry;
    private ClassModelCodecProvider codecProvider;

    @Test
    public void resolveEntityTypes() {
        final ClassModel model = new ClassModel(getCodecRegistry(), new TypeResolver(), Entity.class);
        model.map();
        assertEquals("Should find 4 fields", 4, model.getFields().size());
    }

    private CodecRegistry getCodecRegistry() {
        if (registry == null) {
            codecProvider = ClassModelCodecProvider
                                .builder()
                                .register(Entity.class)
                                .build();
            registry = CodecRegistries.fromProviders(codecProvider, new ValueCodecProvider());
        }
        return registry;
    }

    @Test
    public void testDecode() {
        final Entity entity = new Entity(800L, 12, "Bond", "James Bond");

        final BsonDocument document = new BsonDocument("age", new BsonInt64(800))
                                          .append("faves", new BsonInt32(12))
                                          .append("name", new BsonString("Bond"))
                                          .append("fullName", new BsonString("James Bond"));
        final CodecRegistry codecRegistry = getCodecRegistry();

        final Entity decoded = codecRegistry
                                   .get(Entity.class)
                                   .decode(new BsonDocumentReader(document), DecoderContext.builder().build());

        assertEquals(entity, decoded);
    }

    @Test
    public void testProvider() {
        final CodecRegistry codecRegistry = getCodecRegistry();

        Assert.assertTrue(codecRegistry.get(Entity.class) instanceof ClassModelCodec);
        try {
            codecRegistry.get(Color.class);
            Assert.fail("The get should throw an exception on an unknown class.");
        } catch (final CodecConfigurationException e) {
            // expected
        }
    }

    @Test
    public void testRoundTrip() {
        final Entity entity = new Entity(800L, 12, "Bond", "James Bond");

        final CodecRegistry codecRegistry = getCodecRegistry();

        final BsonDocument document = new BsonDocument();
        final BsonDocumentWriter writer = new BsonDocumentWriter(document);
        final Codec<Entity> codec = codecRegistry.get(Entity.class);
        codec.encode(writer, entity, EncoderContext.builder().build());
        final Entity decoded = codec.decode(new BsonDocumentReader(document), DecoderContext.builder().build());

        assertEquals(entity, decoded);
    }

    private static class Fooble<T> {
        private T value;
    }

}
