/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.operation;

import category.Async;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.AsyncBlock;
import org.mongodb.DatabaseTestCase;
import org.mongodb.Document;
import org.mongodb.selector.PrimaryServerSelector;
import org.mongodb.session.ClusterSession;
import org.mongodb.session.PinnedSession;
import org.mongodb.session.ServerConnectionProvider;
import org.mongodb.session.ServerConnectionProviderOptions;
import org.mongodb.session.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeFalse;
import static org.mongodb.Fixture.getCluster;
import static org.mongodb.Fixture.getExecutor;
import static org.mongodb.Fixture.isSharded;
import static org.mongodb.operation.QueryFlag.Exhaust;

@Category(Async.class)
public class MongoAsyncQueryCursorTest extends DatabaseTestCase {

    private CountDownLatch latch;
    private List<Document> documentList;
    private List<Document> documentResultList;
    private Session session;

    @Before
    public void setUp() {
        super.setUp();
        latch = new CountDownLatch(1);
        documentResultList = new ArrayList<Document>();

        documentList = new ArrayList<Document>();
        for (int i = 0; i < 1000; i++) {
            documentList.add(new Document("_id", i));
        }

        collection.insert(documentList);
        session = new ClusterSession(getCluster(), getExecutor());
    }

    @After
    public void tearDown() {
        super.tearDown();
        session.close();
    }

    @Test
    public void testBlockRun() throws InterruptedException {
        new MongoAsyncQueryCursor<Document>(collection.getNamespace(),
                                            new Find().batchSize(2),
                                            collection.getOptions().getDocumentCodec(),
                                            collection.getCodec(),
                                            getConnectionProvider(session))
        .start(new TestBlock());
        latch.await();
        assertEquals(documentList, documentResultList);
    }

    @Test
    public void testLimit() throws InterruptedException {
        new MongoAsyncQueryCursor<Document>(collection.getNamespace(),
                                            new Find().batchSize(2).limit(100).order(new Document("_id", 1)),
                                            collection.getOptions().getDocumentCodec(),
                                            collection.getCodec(),
                                            getConnectionProvider(session))
        .start(new TestBlock());

        latch.await();
        assertThat(documentResultList, is(documentList.subList(0, 100)));
    }

    @Test
    public void testExhaust() throws InterruptedException {
        assumeFalse(isSharded());
        new MongoAsyncQueryCursor<Document>(collection.getNamespace(),
                                            new Find().batchSize(2).addFlags(EnumSet.of(Exhaust)).order(new Document("_id", 1)),
                                            collection.getOptions().getDocumentCodec(),
                                            collection.getCodec(),
                                            getConnectionProvider(session))
        .start(new TestBlock());

        latch.await();
        assertThat(documentResultList, is(documentList));
    }

    @Test
    public void testExhaustWithLimit() throws InterruptedException {
        assumeFalse(isSharded());
        new MongoAsyncQueryCursor<Document>(collection.getNamespace(),
                                            new Find().batchSize(2).limit(5).addFlags(EnumSet.of(Exhaust)).order(new Document("_id", 1)),
                                            collection.getOptions().getDocumentCodec(),
                                            collection.getCodec(),
                                            getConnectionProvider(session))
        .start(new TestBlock());

        latch.await();
        assertThat(documentResultList, is(documentList.subList(0, 5)));
    }

    @Test
    public void testExhaustWithDiscard() throws InterruptedException, ExecutionException {
        assumeFalse(isSharded());
        Session pinnedSession = new PinnedSession(getCluster(), getExecutor());

        try {
            TestBlock block = new TestBlock(1);
            new MongoAsyncQueryCursor<Document>(collection.getNamespace(),
                                                new Find().batchSize(2)
                                                          .limit(5)
                                                          .addFlags(EnumSet.of(Exhaust))
                                                          .order(new Document("_id", 1)),
                                                collection.getOptions().getDocumentCodec(),
                                                collection.getCodec(),
                                                getConnectionProvider(pinnedSession))
            .start(block);

            latch.await();
            assertThat(documentResultList, is(documentList.subList(0, 1)));

            documentResultList.clear();
            CountDownLatch nextLatch = new CountDownLatch(1);

            new MongoAsyncQueryCursor<Document>(collection.getNamespace(),
                                                new Find().limit(1).order(new Document("_id", -1)),
                                                collection.getOptions().getDocumentCodec(),
                                                collection.getCodec(),
                                                getConnectionProvider(pinnedSession))
            .start(new TestBlock(1, nextLatch));
            nextLatch.await();
            assertEquals(Arrays.asList(new Document("_id", 999)), documentResultList);
        } finally {
            pinnedSession.close();
        }
    }

    @Test
    public void testEarlyTermination() throws InterruptedException, ExecutionException {
        assumeFalse(isSharded());
        Session pinnedSession = new PinnedSession(getCluster(), getExecutor());

        try {
            TestBlock block = new TestBlock(1);
            new MongoAsyncQueryCursor<Document>(collection.getNamespace(),
                                                new Find().batchSize(2)
                                                          .limit(5)
                                                          .addFlags(EnumSet.of(Exhaust))
                                                          .order(new Document("_id", 1)),
                                                collection.getOptions().getDocumentCodec(),
                                                collection.getCodec(),
                                                getConnectionProvider(pinnedSession))
            .start(block);

            latch.await();
            assertEquals(1, block.getIterations());
        } finally {
            pinnedSession.close();
        }
    }

    private ServerConnectionProvider getConnectionProvider(final Session session) {
        return session.createServerConnectionProvider(new ServerConnectionProviderOptions(true, new PrimaryServerSelector()));
    }

    private final class TestBlock implements AsyncBlock<Document> {
        private final int count;
        private int iterations;
        private final CountDownLatch latch;

        private TestBlock() {
            this(Integer.MAX_VALUE);
        }

        private TestBlock(final int count) {
            this(count, MongoAsyncQueryCursorTest.this.latch);
        }

        private TestBlock(final int count, final CountDownLatch latch) {
            this.count = count;
            this.latch = latch;
        }

        @Override
        public void done() {
            latch.countDown();
        }

        @Override
        public void apply(final Document document) {
            if (iterations >= count) {
                throw new RuntimeException("Discard the rest");
            }
            iterations++;
            documentResultList.add(document);
        }

        public int getIterations() {
            return iterations;
        }
    }
}