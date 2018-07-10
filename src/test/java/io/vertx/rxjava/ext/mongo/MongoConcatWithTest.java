package io.vertx.rxjava.ext.mongo;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.Before;
import org.junit.Test;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClientBulkWriteResult;
import io.vertx.ext.mongo.MongoTestBase;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;
import rx.Single;
import rx.exceptions.Exceptions;

public class MongoConcatWithTest extends MongoTestBase {

  private MongoClient mongoClient;

  private int messageCount = 55;
  private int bulkWriteSize = 10;

  private OffsetDateTime now = OffsetDateTime.of( 2018, 1, 4, 10, 25, 0, 0, ZoneOffset.UTC );

  @Before
  public void setup() {
    Vertx vertx = Vertx.vertx();
    mongoClient = MongoClient.createShared( vertx, getConfig() );

  }

  @Test
  public void shouldNotGenerateTooManyProducedErrorWithWorkaround() {
    insertTestData( "MockCollectionName1", "123", messageCount );

    // WHEN
    Integer updateCount = Observable
      .<JsonObject> empty() // just to simplify the test... this does not need to be empty for the concatWith() to fail!
      .concatWith( selectItemsOccurringAfter( "MockCollectionName1", "123", now.minusMinutes( 80 ).toInstant() )
        // IMPORTANT: DO NOT EVER REMOVE THIS or you will get `java.lang.IllegalStateException: more produced than requested`
        .doOnError( t -> { // when using this with concatWith()
          System.out.println( "Error while selecting items: " + t.getMessage() );
          throw Exceptions.propagate( t );
        } )
      )
      .buffer( bulkWriteSize )
      .concatMap( list -> Observable.just( list.size() ) ) // normally this is a bulk write to mongo
      .reduce( ( a, b ) -> a + b )
      .toSingle()
      .toBlocking()
      .value();

    // THEN
    assertEquals( messageCount, updateCount.intValue() );
  }

  @Test
  public void shouldNotGenerateTooManyProducedErrorWithoutWorkaround() {
    insertTestData( "MockCollectionName2", "123", messageCount );

    // WHEN
    Integer updateCount = Observable
      .<JsonObject> empty() // just to simplify the test... this does not need to be empty for the concatWith() to fail!
      .concatWith( selectItemsOccurringAfter( "MockCollectionName2", "123", now.minusMinutes( 80 ).toInstant() ) )
      .buffer( bulkWriteSize )
      .compose( this::fakeBulkUpdateReturningUpdateCount )
      .toBlocking()
      .first();

    // THEN
    assertEquals( messageCount, updateCount.intValue() );
  }

  private Observable<Integer> fakeBulkUpdateReturningUpdateCount( Observable<List<JsonObject>> input ) {
    return input
      .concatMap( list -> Observable.just( list.size() ) ) // normally this is a bulk write to mongo
      .reduce( ( a, b ) -> a + b );
  }

  public Observable<JsonObject> selectItemsOccurringAfter( String collectionName, String id, Instant afterInclusive ) {

    JsonObject query = new JsonObject()
      .put( "deviceId", new JsonObject().put( "$eq", id ) )
      .put( "timestamp", new JsonObject().put( "$gte", afterInclusive.toEpochMilli() ) );

    FindOptions options = new FindOptions()
      .setSort( new JsonObject().put( "timestamp", 1 ) )
      .setBatchSize( 50 );

    return mongoClient.findBatchWithOptions( collectionName, query, options )
      .toObservable();
  }

  private void insertTestData( String collectionName, String deviceId, long amount ) {
    Optional<Long> inserted = LongStream.rangeClosed( 1, amount )
      .mapToObj( testDataIndex -> createTestDocument( deviceId, now.plusSeconds( testDataIndex ) ) )
      .map( document -> insertDocuments( collectionName, Collections.singletonList( document ) )
        .toBlocking()
        .value()
        .getInsertedCount()
      )
      .reduce( Long::sum );
    assertEquals( "Testsetup failed - not enough data inserted", amount, inserted.orElse( 0L ).longValue() );
  }

  private JsonObject createTestDocument( String deviceId, OffsetDateTime timeStamp ) {
    JsonObject document = new JsonObject()
      .put( "deviceId", deviceId )
      .put( "timestamp", timeStamp.toInstant().toEpochMilli() );
    return document;
  }

  public Single<MongoClientBulkWriteResult> insertDocuments( final String collection, List<JsonObject> documents ) {
    List<BulkOperation> bulkOperations = documents.stream()
      .map( BulkOperation::createInsert )
      .collect( Collectors.toList() );
    return this.mongoClient.rxBulkWrite( collection, bulkOperations );
  }
}
