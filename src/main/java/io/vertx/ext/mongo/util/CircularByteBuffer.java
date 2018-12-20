package io.vertx.ext.mongo.util;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public class CircularByteBuffer {

  private byte[] array;
  private int sizeMask;
  private final AtomicInteger writeSequence = new AtomicInteger(0);
  private final AtomicInteger readSequence = new AtomicInteger(0);

  public CircularByteBuffer(int size) {
    //Find the next power of two to allow bitwise masking. Double the requested size to avoid resizing mid processing.
    size = nextPowerOfTwo(size);
    array = new byte[size];
    sizeMask = array.length - 1;
  }

  private int nextPowerOfTwo(int number) {
    number = number - 1;
    number |= number >> 1;
    number |= number >> 2;
    number |= number >> 4;
    number |= number >> 8;
    number |= number >> 16;
    return number + 1;
  }

  public void fillFrom(ByteBuffer buffer) {
    while (buffer.hasRemaining()) {
      add(buffer.get());
    }
  }

  private void add(byte value) {
    ensureSize();
    array[getIndex(writeSequence.getAndIncrement())] = value;
  }

  private void ensureSize() {
    if (isFull()) {
      final byte[] newArray = new byte[array.length << 1];
      final int newSizeMask = newArray.length - 1;
      final int readIndex = getIndex(readSequence.get());
      final int unwrappedBytes = array.length - readIndex;
      System.arraycopy(array, readIndex, newArray, 0, unwrappedBytes);
      // If logically successive bytes are physically at the start of the array we need to append them in the new array
      if (unwrappedBytes < array.length) {
        System.arraycopy(array, 0, newArray, unwrappedBytes, readIndex);
      }
      readSequence.set(0);
      writeSequence.set(array.length);
      array = newArray;
      sizeMask = newSizeMask;
    }
  }

  public int drainInto(ByteBuffer byteBuffer) {
    int byteWritten = 0;
    while (readSequence.get() < writeSequence.get() && byteBuffer.hasRemaining()) {
      byteBuffer.put(get());
      byteWritten++;
    }
    return byteWritten;
  }

  private byte get() {
    return array[getIndex(readSequence.getAndIncrement())];
  }

  private int getIndex(int sequence) {
    return sequence & sizeMask;
  }

  public boolean isFull() {
    return remaining() >= array.length;
  }

  public int remaining() {
    return writeSequence.get() - readSequence.get();
  }

  public int capacity() {
    return array.length;
  }

  public boolean isEmpty() {
    return writeSequence.get() == readSequence.get();
  }

}
