/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
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

package org.mongodb.session;

import org.mongodb.MongoFuture;
import org.mongodb.connection.Channel;
import org.mongodb.connection.ServerDescription;

/**
 * A provider of channels to a single server.
 */
public interface ServerChannelProvider {
    /**
     *
     * @return  the description of the server that this provider is providing channels to.
     */
    ServerDescription getServerDescription();

    /**
     * Provide a channel to the server.  A provider may choose to return the same Channel
     *
     * @return a channel to the server
     */
    Channel getChannel();

    MongoFuture<Channel> getChannelAsync();
}