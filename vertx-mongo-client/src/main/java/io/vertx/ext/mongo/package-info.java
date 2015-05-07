/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

/**
 * = Vert.x MongoDB Client
 *
 * A Vert.x client allowing applications to interact with a MongoDB instance, whether that's
 * saving, retrieving, searching, or deleting documents. Mongo is a great match for persisting data in a Vert.x application
 * as it natively handles JSON (BSON) documents.
 *
 * *Features*
 *
 * * Completely non-blocking
 * * Custom codec to support fast serialization to/from Vert.x JSON
 * * Supports a majority of the configuration options from the MongoDB Java Driver
 *
 * == Creating a client
 *
 * You can create a client in several ways:
 *
 * === Using the default shared pool
 *
 * In most cases you will want to share a pool between different client instances.
 *
 * E.g. you scale your application by deploying multiple instances of your verticle and you want each verticle instance
 * to share the same pool so you don't end up with multiple pools
 *
 * The simplest way to do this is as follows:
 *
 * [source,java]
 * ----
 * {@link examples.Examples#exampleCreateDefault}
 * ----
 *
 * The first call to {@link io.vertx.ext.mongo.MongoClient#createShared(io.vertx.core.Vertx, io.vertx.core.json.JsonObject)}
 * will actually create the pool, and the specified config will be used.
 *
 * Subsequent calls will return a new client instance that uses the same pool, so the configuration won't be used.
 *
 * === Specifying a pool source name
 *
 * You can create a client specifying a pool source name as follows
 *
 * [source,java]
 * ----
 * {@link examples.Examples#exampleCreatePoolName}
 * ----
 *
 * If different clients are created using the same Vert.x instance and specifying the same pool name, they will
 * share the same pool.
 *
 * The first call to {@link io.vertx.ext.mongo.MongoClient#createShared(io.vertx.core.Vertx, io.vertx.core.json.JsonObject)}
 * will actually create the pool, and the specified config will be used.
 *
 * Subsequent calls will return a new client instance that uses the same pool, so the configuration won't be used.
 *
 * Use this way of creating if you wish different groups of clients to have different pools, e.g. they're
 * interacting with different databases.
 *
 * === Creating a client with a non shared data pool
 *
 * In most cases you will want to share a pool between different client instances.
 * However, it's possible you want to create a client instance that doesn't share its pool with any other client.
 *
 * In that case you can use {@link io.vertx.ext.mongo.MongoClient#createNonShared(io.vertx.core.Vertx, io.vertx.core.json.JsonObject)}.
 *
 * [source,java]
 * ----
 * {@link examples.Examples#exampleCreateNonShared}
 * ----
 *
 * This is equivalent to calling {@link io.vertx.ext.mongo.MongoClient#createShared(io.vertx.core.Vertx, io.vertx.core.json.JsonObject, String)}
 * with a unique pool name each time.
 *
 *
 * == Using the API
 *
 * The client API is represented by {@link io.vertx.ext.mongo.MongoClient}.
 *
 * === Saving documents
 *
 * To save a document you use {@link io.vertx.ext.mongo.MongoClient#save}.
 *
 * If the document has no `\_id` field, it is inserted, otherwise, it is _upserted_. Upserted means it is inserted
 * if it doesn't already exist, otherwise it is updated.
 *
 * If the document is inserted and has no id, then the id field generated will be returned to the result handler.
 *
 * Here's an example of saving a document and getting the id back
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example1}
 * ----
 *
 * And here's an example of saving a document which already has an id.
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example2}
 * ----
 *
 * === Inserting documents
 *
 * To insert a document you use {@link io.vertx.ext.mongo.MongoClient#insert}.
 *
 * If the document is inserted and has no id, then the id field generated will be returned to the result handler.
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example3}
 * ----
 *
 * If a document is inserted with an id, and a document with that id already eists, the insert will fail:
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example4}
 * ----
 *
 * === Updating documents
 *
 * To update a documents you use {@link io.vertx.ext.mongo.MongoClient#update}.
 *
 * This updates one or multiple documents in a collection. The json object that is passed in the `update`
 * parameter must contain http://docs.mongodb.org/manual/reference/operator/update-field/[Update Operators] and determines
 * how the object is updated.
 *
 * The json object specified in the query parameter determines which documents in the collection will be updated.
 *
 * Here's an example of updating a document in the books collection:
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example5}
 * ----
 *
 * To specify if the update should upsert or update multiple documents, use {@link io.vertx.ext.mongo.MongoClient#updateWithOptions}
 * and pass in an instance of {@link io.vertx.ext.mongo.UpdateOptions}.
 *
 * This has the following fields:
 *
 * `multi`:: set to true to update multiple documents
 * `upsert`:: set to true to insert the document if the query doesn't match
 * `writeConcern`:: the write concern for this operation
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example6}
 * ----
 *
 * === Replacing documents
 *
 * To replace documents you use {@link io.vertx.ext.mongo.MongoClient#replace}.
 *
 * This is similar to the update operation, however it does not take any update operators like `update`.
 * Instead it replaces the entire document with the one provided.
 *
 * Here's an example of replacing a document in the books collection
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example7}
 * ----
 *
 * === Finding documents
 *
 * To find documents you use {@link io.vertx.ext.mongo.MongoClient#find}.
 *
 * The `query` parameter is used to match the documents in the collection.
 *
 * Here's a simple example with an empty query that will match all books:
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example8}
 * ----
 *
 * Here's another example that will match all books by Tolkien:
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example9}
 * ----
 *
 * The matching documents are returned as a list of json objects in the result handler.
 *
 * To specify things like what fields to return, how many results to return, etc use {@link io.vertx.ext.mongo.MongoClient#findWithOptions}
 * and pass in the an instance of {@link io.vertx.ext.mongo.FindOptions}.
 *
 * This has the following fields:
 *
 * `fields`:: The fields to return in the results. Defaults to `null`, meaning all fields will be returned
 * `sort`:: The fields to sort by. Defaults to `null`.
 * `limit`:: The limit of the number of results to return. Default to `-1`, meaning all results will be returned.
 * `skip`:: The number of documents to skip before returning the results. Defaults to `0`.
 *
 * === Finding a single document
 *
 * To find a single document you use {@link io.vertx.ext.mongo.MongoClient#findOne}.
 *
 * This works just like {@link io.vertx.ext.mongo.MongoClient#find} but it returns just the first matching document.
 *
 * === Removing documents
 *
 * To remove documents use {@link io.vertx.ext.mongo.MongoClient#remove}.
 *
 * The `query` parameter is used to match the documents in the collection to determine which ones to remove.
 *
 * Here's an example of removing all Tolkien books:
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example10}
 * ----
 *
 * === Removing a single document
 *
 * To remove a single document you use {@link io.vertx.ext.mongo.MongoClient#removeOne}.
 *
 * This works just like {@link io.vertx.ext.mongo.MongoClient#remove} but it removes just the first matching document.
 *
 * === Counting documents
 *
 * To count documents use {@link io.vertx.ext.mongo.MongoClient#count}.
 *
 * Here's an example that counts the number of Tolkien books. The number is passed to the result handler.
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example11}
 * ----
 *
 * === Managing MongoDB collections
 *
 * All MongoDB documents are stored in collections.
 *
 * To get a list of all collections you can use {@link io.vertx.ext.mongo.MongoClient#getCollections}
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example11_1}
 * ----
 *
 * To create a new collection you can use {@link io.vertx.ext.mongo.MongoClient#createCollection}
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example11_2}
 * ----
 *
 * To drop a collection you can use {@link io.vertx.ext.mongo.MongoClient#dropCollection}
 *
 * NOTE: Dropping a collection will delete all documents within it!
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example11_3}
 * ----
 *
 *
 * === Running other MongoDB commands
 *
 * You can run arbitrary MongoDB commands with {@link io.vertx.ext.mongo.MongoClient#runCommand}.
 *
 * Commands can be used to run more advanced mongoDB features, such as using MapReduce.
 * For more information see the mongo docs for supported http://docs.mongodb.org/manual/reference/command[Commands].
 *
 * Here's an example of running a ping command
 *
 * [source,$lang]
 * ----
 * {@link examples.Examples#example12}
 * ----
 *
 * == Configuring the client
 *
 * The client is configured with a json object.
 *
 * The following configuration is supported by the mongo client:
 *
 *
 * `db_name`:: Name of the database in the mongoDB instance to use. Defaults to `default_db`
 * `useObjectId`:: Toggle this option to support persisting and retrieving ObjectId's as strings. Defaults to `false`.
 *
 * The mongo client tries to support most options that are allowed by the driver. There are two ways to configure mongo
 * for use by the driver, either by a connection string or by separate configuration options.
 *
 * NOTE: If the connection string is used the mongo client will ignore any driver configuration options.
 *
 * `connection_string`:: The connection string the driver uses to create the client. E.g. `mongodb://localhost:27017`.
 * For more information on the format of the connection string please consult the driver documentation.
 *
 * *Specific driver configuration options*
 *
 * ----
 * {
 *   // Single Cluster Settings
 *   "host" : "17.0.0.1", // string
 *   "port" : 27017,      // int
 *
 *   // Multiple Cluster Settings
 *   "hosts" : [
 *     {
 *       "host" : "cluster1", // string
 *       "port" : 27000       // int
 *     },
 *     {
 *       "host" : "cluster2", // string
 *       "port" : 28000       // int
 *     },
 *     ...
 *   ],
 *   "replicaSet" :  "foo"    // string
 *
 *   // Connection Pool Settings
 *   "maxPoolSize" : 50,                // int
 *   "minPoolSize" : 25,                // int
 *   "maxIdleTimeMS" : 300000,          // long
 *   "maxLifeTimeMS" : 3600000,         // long
 *   "waitQueueMultiple"  : 10,         // int
 *   "waitQueueTimeoutMS" : 10000,      // long
 *   "maintenanceFrequencyMS" : 2000,   // long
 *   "maintenanceInitialDelayMS" : 500, // long
 *
 *   // Credentials / Auth
 *   "username"   : "john",     // string
 *   "password"   : "passw0rd", // string
 *   "authSource" : "some.db"   // string
 *   // Auth mechanism
 *   "authMechanism"     : "GSSAPI",        // string
 *   "gssapiServiceName" : "myservicename", // string
 *
 *   // Socket Settings
 *   "connectTimeoutMS" : 300000, // int
 *   "socketTimeoutMS"  : 100000, // int
 *   "sendBufferSize"    : 8192,  // int
 *   "receiveBufferSize" : 8192,  // int
 *   "keepAlive" : true           // boolean
 *
 *   // Heartbeat socket settings
 *   "heartbeat.socket" : {
 *   "connectTimeoutMS" : 300000, // int
 *   "socketTimeoutMS"  : 100000, // int
 *   "sendBufferSize"    : 8192,  // int
 *   "receiveBufferSize" : 8192,  // int
 *   "keepAlive" : true           // boolean
 *   }
 *
 *   // Server Settings
 *   "heartbeatFrequencyMS" :    1000 // long
 *   "minHeartbeatFrequencyMS" : 500 // long
 * }
 * ----
 *
 * *Driver option descriptions*
 *
 * `host`:: The host the mongoDB instance is running. Defaults to `127.0.0.1`. This is ignored if `hosts` is specified
 * `port`:: The port the mongoDB instance is listening on. Defaults to `27017`. This is ignored if `hosts` is specified
 * `hosts`:: An array representing the hosts and ports to support a mongoDB cluster (sharding / replication)
 * `host`:: A host in the cluster
 * `port`:: The port a host in the cluster is listening on
 * `replicaSet`:: The name of the replica set, if the mongoDB instance is a member of a replica set
 * `maxPoolSize`:: The maximum number of connections in the connection pool. The default value is `100`
 * `minPoolSize`:: The minimum number of connections in the connection pool. The default value is `0`
 * `maxIdleTimeMS`:: The maximum idle time of a pooled connection. The default value is `0` which means there is no limit
 * `maxLifeTimeMS`:: The maximum time a pooled connection can live for. The default value is `0` which means there is no limit
 * `waitQueueMultiple`:: The maximum number of waiters for a connection to become available from the pool. Default value is `500`
 * `waitQueueTimeoutMS`:: The maximum time that a thread may wait for a connection to become available. Default value is `120000` (2 minutes)
 * `maintenanceFrequencyMS`:: The time period between runs of the maintenance job. Default is `0`.
 * `maintenanceInitialDelayMS`:: The period of time to wait before running the first maintenance job on the connection pool. Default is `0`.
 * `username`:: The username to authenticate. Default is `null` (meaning no authentication required)
 * `password`:: The password to use to authenticate.
 * `authSource`:: The database name associated with the user's credentials. Default value is `admin`
 * `authMechanism`:: The authentication mechanism to use. See [Authentication](http://docs.mongodb.org/manual/core/authentication/) for more details.
 * `gssapiServiceName`:: The Kerberos service name if `GSSAPI` is specified as the `authMechanism`.
 * `connectTimeoutMS`:: The time in milliseconds to attempt a connection before timing out. Default is `10000` (10 seconds)
 * `socketTimeoutMS`:: The time in milliseconds to attempt a send or receive on a socket before the attempt times out. Default is `0` meaning there is no timeout
 * `sendBufferSize`:: Sets the send buffer size (SO_SNDBUF) for the socket. Default is `0`, meaning it will use the OS default for this option.
 * `receiveBufferSize`:: Sets the receive buffer size (SO_RCVBUF) for the socket. Default is `0`, meaning it will use the OS default for this option.
 * `keepAlive`:: Sets the keep alive (SO_KEEPALIVE) for the socket. Default is `false`
 * `heartbeat.socket`:: Configures the socket settings for the cluster monitor of the MongoDB java driver.
 * `heartbeatFrequencyMS`:: The frequency that the cluster monitor attempts to reach each server. Default is `5000` (5 seconds)
 * `minHeartbeatFrequencyMS`:: The minimum heartbeat frequency. The default value is `1000` (1 second)
 *
 * NOTE: Most of the default values listed above use the default values of the MongoDB Java Driver.
 * Please consult the driver documentation for up to date information.
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@Document(fileName = "index.adoc")
@GenModule(name = "vertx-mongo")
package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.GenModule;
import io.vertx.docgen.Document;