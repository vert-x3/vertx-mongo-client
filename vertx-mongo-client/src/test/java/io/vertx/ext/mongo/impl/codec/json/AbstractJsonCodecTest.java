package io.vertx.ext.mongo.impl.codec.json;

import org.bson.BsonType;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 */
public class AbstractJsonCodecTest {

  @Test
  public void getBsonType_returnsNullType_whenValueIsNull() {
    AbstractJsonCodec codec = getCodec();
    Assert.assertEquals(BsonType.NULL, codec.getBsonType(null));
  }

  private AbstractJsonCodec getCodec() {
    return new AbstractJsonCodec() {
      @Override
      protected Object newObject() {
        return null;
      }

      @Override
      protected void add(Object object, String name, Object value) {

      }

      @Override
      protected boolean isObjectInstance(Object instance) {
        return false;
      }

      @Override
      protected void forEach(Object object, BiConsumer objectConsumer) {

      }

      @Override
      protected Object newArray() {
        return null;
      }

      @Override
      protected void add(Object array, Object value) {

      }

      @Override
      protected boolean isArrayInstance(Object instance) {
        return false;
      }

      @Override
      protected void forEach(Object array, Consumer arrayConsumer) {

      }

      @Override
      public Class getEncoderClass() {
        return null;
      }
    };
  }
}
