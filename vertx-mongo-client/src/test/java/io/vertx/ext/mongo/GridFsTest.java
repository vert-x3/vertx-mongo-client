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

package io.vertx.ext.mongo;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:dbush@redhat.com">David Bush</a>
 */
public class GridFsTest extends MongoTestBase {

  protected MongoClient mongoClient;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    mongoClient = MongoClient.createNonShared(vertx, config);
    CountDownLatch latch = new CountDownLatch(1);
    dropCollections(mongoClient, latch);
    awaitLatch(latch);
  }

  @Override
  public void tearDown() throws Exception {
    mongoClient.close();
    super.tearDown();
  }

  private static String createTempFileWithContent(int length) {

    try {
      Path path = Files.createTempFile("sample-file", ".txt");
      File file = path.toFile();
      FileOutputStream fos = new FileOutputStream(file);
      for (int i = 0; i < length; i++) fos.write(ThreadLocalRandom.current().nextInt(-128, 128));
      fos.close();
      file.deleteOnExit();

      return file.getAbsolutePath();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }
  private static String createTempFile() {

    try {
      Path path = Files.createTempFile("sample-file", ".txt");
      File file = path.toFile();
      file.deleteOnExit();

      return file.getAbsolutePath();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  @Test
  public void testDelete() {

    String fileName = createTempFileWithContent((1024 * 3) + 70);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {
      Future<String> uploadFuture = Future.future();
      gridFsClient.get().uploadFile(fileName, uploadFuture.completer());
      return uploadFuture;
    }).compose(id -> {
      assertNotNull(id);
      Future<Void> deleteFuture = Future.future();
      gridFsClient.get().delete(id, deleteFuture.completer());
      return deleteFuture;
    }).compose(deleted -> {
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  @Test
  public void testFileUpload() {

    String fileName = createTempFileWithContent((1024 * 3) + 70);
    String downloadFileName = createTempFile();

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {
      Future<String> uploadFuture = Future.future();
      gridFsClient.get().uploadFile(fileName, uploadFuture.completer());
      return uploadFuture;
    }).compose(id -> {
      assertNotNull(id);
      Future<Long> downloadFuture = Future.future();
      gridFsClient.get().downloadFileAs(fileName, downloadFileName, downloadFuture.completer());
      return downloadFuture;
    }).compose(length -> {
      byte[] original = new byte[0];
      try {
        original = Files.readAllBytes(new File(fileName).toPath());
        byte[] copy = Files.readAllBytes(new File(downloadFileName).toPath());
        assertTrue(Arrays.equals(original, copy));
        testComplete();
      } catch (IOException e) {
        fail(e);
      }
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  @Test
  public void testBigFileUpload() {

    String originalFileName = createTempFileWithContent((1024 * 60) + 16);
    long originalLength = new File(originalFileName).length();
    String copiedFileName = createTempFile();

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {
      Future<String> uploadFuture = Future.future();
      gridFsClient.get().uploadFile(originalFileName, uploadFuture.completer());
      return uploadFuture;
    }).compose(id -> {
      assertNotNull(id);
      Future<Long> downloadFuture = Future.future();
      gridFsClient.get().downloadFileAs(originalFileName, copiedFileName, downloadFuture.completer());
      return downloadFuture;
    }).compose(length -> {
      assertEquals(originalLength, length.longValue());
      byte[] original = new byte[0];
      try {
        original = Files.readAllBytes(new File(originalFileName).toPath());
        byte[] copy = Files.readAllBytes(new File(copiedFileName).toPath());
        assertTrue(Arrays.equals(original, copy));
        testComplete();
      } catch (IOException e) {
        fail(e);
      }
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  @Test
  public void testFileUploadWithOptions() {

    String fileName = createTempFileWithContent((1024 * 3) + 70);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    JsonObject meta = new JsonObject();
    meta.put("nick_name", "Puhi the eel");

    UploadOptions options = new UploadOptions();
    options.setMetadata(meta);

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {
      Future<String> uploadFuture = Future.future();
      gridFsClient.get().uploadFileWithOptions(fileName, options, uploadFuture.completer());
      return uploadFuture;
    }).compose(id -> {
      assertNotNull(id);
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  @Test
  public void testFindWithMetadata() {

    String fileName = createTempFileWithContent((1024 * 3) + 70);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    JsonObject meta = new JsonObject();
    meta.put("nick_name", "Puhi the eel");

    UploadOptions options = new UploadOptions();
    options.setMetadata(meta);

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {
      Future<String> uploadFuture = Future.future();
      gridFsClient.get().uploadFileWithOptions(fileName, options, uploadFuture.completer());
      return uploadFuture;
    }).compose(id -> {
      assertNotNull(id);
      Future<List<String>> findFuture = Future.future();
      JsonObject query = new JsonObject().put("metadata.nick_name", "Puhi the eel");
      gridFsClient.get().findIds(query, findFuture.completer());
      return findFuture;
    }).compose(list -> {
      assertTrue(list.size() > 0);
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  @Test
  public void testFindAllIds() {

    String fileName = createTempFileWithContent((1024 * 3) + 70);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {
      Future<String> uploadFuture = Future.future();
      gridFsClient.get().uploadFile(fileName, uploadFuture.completer());
      return uploadFuture;
    }).compose(id -> {
      assertNotNull(id);
      Future<List<String>> findFuture = Future.future();
      gridFsClient.get().findAllIds(findFuture.completer());
      return findFuture;
    }).compose(list -> {
      assertTrue(list.size() == 1);
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  @Test
  public void testDrop() {

    String fileName = createTempFileWithContent((1024 * 3) + 70);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  private void handleDownload(MongoGridFsDownload download, OutputStream stream, Handler<AsyncResult<Void>> completeHandler) {

    download.read(1024, onSuccess(encodedString -> {
      try {
        if (encodedString != null) {
          byte[] bytes = Base64.getDecoder().decode(encodedString);
          stream.write(bytes);
          handleDownload(download, stream, completeHandler);
        } else {
          stream.close();
          completeHandler.handle(Future.succeededFuture());
        }
      } catch (IOException ioe) {
        completeHandler.handle(Future.failedFuture(ioe));
      }
    }));

  }

  @Test
  public void testDownloadBuffer() {

    String fileName = createTempFileWithContent((1024 * 3) + 70);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {
      Future<String> uploadFuture = Future.future();
      gridFsClient.get().uploadFile(fileName, uploadFuture.completer());
      return uploadFuture;
    }).compose(id -> {
      assertNotNull(id);
      Future<MongoGridFsDownload> downloadBufferFuture = Future.future();
      gridFsClient.get().downloadBuffer(fileName, downloadBufferFuture.completer());
      return downloadBufferFuture;
    }).compose(download -> {

      Future<Void> downloadCompleteFuture = Future.future();

      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      this.handleDownload(download, stream, downloadCompleteFuture.completer());

      return downloadCompleteFuture;
    }).compose(nothing -> {
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  @Test
  public void testUploadBuffer() {

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {

      Future<MongoGridFsUpload> uploadFuture = Future.future();
      gridFsClient.get().uploadBuffer("goose.fil", uploadFuture.completer());

      return uploadFuture;
    }).compose(upload -> {

      String fileName = createTempFileWithContent(512);

      byte[] bFile = new byte[512];

      try {
        FileInputStream fileInputStream = new FileInputStream(new File(fileName));
        fileInputStream.read(bFile);
        fileInputStream.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      Future<Integer> uploadFuture = Future.future();

      String encodedBuffer = Base64.getEncoder().encodeToString(bFile);

      Future<String> uploadedFuture = Future.future();

      upload.uploadBuffer(encodedBuffer, onSuccess(length -> {
        assertEquals(512, length.intValue());
        upload.end(uploadedFuture.completer());
      }));
      return uploadedFuture;
    }).compose(id -> {
      assertNotNull(id);
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  @Test
  public void testUploadBufferWithOptions() {


    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {

      Future<MongoGridFsUpload> uploadFuture = Future.future();
      UploadOptions options = new UploadOptions();
      options.setMetadata(new JsonObject().put("nick_name", "Mini Boo"));
      gridFsClient.get().uploadBufferWithOptions("goose.fil", options, uploadFuture.completer());

      return uploadFuture;
    }).compose(upload -> {

      String fileName = createTempFileWithContent(512);

      byte[] bFile = new byte[512];

      try {
        FileInputStream fileInputStream = new FileInputStream(new File(fileName));
        fileInputStream.read(bFile);
        fileInputStream.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      Future<Integer> uploadFuture = Future.future();

      String encodedBuffer = Base64.getEncoder().encodeToString(bFile);

      Future<String> uploadedFuture = Future.future();

      upload.uploadBuffer(encodedBuffer, onSuccess(length -> {
        assertEquals(512, length.intValue());
        upload.end(uploadedFuture.completer());
      }));
      return uploadedFuture;
    }).compose(id -> {
      assertNotNull(id);
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  @Test
  public void testUploadMultipleBuffer() {

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {

      Future<MongoGridFsUpload> uploadFuture = Future.future();
      gridFsClient.get().uploadBuffer("eel.fil", uploadFuture.completer());

      return uploadFuture;
    }).compose(upload -> {

      String fileName = createTempFileWithContent((1024 * 3) + 70);

      FileInputStream fileInputStream;

      try {
        fileInputStream = new FileInputStream(new File(fileName));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      Future<String> uploadedFuture = Future.future();
      this.handleUpload(gridFsClient.get(), fileInputStream, upload, uploadedFuture.completer());
      return uploadedFuture;
    }).compose( id -> {
      assertNotNull(id);
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  @Test
  public void testUploadMultipleBufferWithOptions() {

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {


      JsonObject meta = new JsonObject();
      meta.put("nick_name", "Io the hawk");

      UploadOptions options = new UploadOptions();
      options.setMetadata(meta);

      Future<MongoGridFsUpload> uploadFuture = Future.future();
      gridFsClient.get().uploadBufferWithOptions("eel.fil", options, uploadFuture.completer());

      return uploadFuture;
    }).compose(upload -> {

      String fileName = createTempFileWithContent((1024 * 3) + 70);

      FileInputStream fileInputStream;

      try {
        fileInputStream = new FileInputStream(new File(fileName));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      Future<String> uploadedFuture = Future.future();
      this.handleUpload(gridFsClient.get(), fileInputStream, upload, uploadedFuture.completer());
      return uploadedFuture;
    }).compose( id -> {
      assertNotNull(id);
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  private void handleUpload(MongoGridFsClient mongoGridFsClient, FileInputStream fileInputStream, MongoGridFsUpload upload, Handler<AsyncResult<String>> completeHandler) {

    try {
      if (fileInputStream.available() > 0) {
        int size = fileInputStream.available();
        if (size > 1024) size = 1024;
        byte[] bFile = new byte[size];
        fileInputStream.read(bFile);
        String encodedBuffer = Base64.getEncoder().encodeToString(bFile);
        upload.uploadBuffer( encodedBuffer, onSuccess(number -> {
          assertTrue(number > 0);
          this.handleUpload(mongoGridFsClient, fileInputStream, upload, completeHandler);
        }));
      } else {
        upload.end(onSuccess(id -> {
          completeHandler.handle(Future.succeededFuture(id));
        }));
      }
    } catch (IOException ioe) {
      fail(ioe);
    }

  }


  @Test
  public void testFileDownload() {

    String fileName = createTempFileWithContent(1024);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {

      Future<String> uploadFuture = Future.future();
      gridFsClient.get().uploadFile(fileName, uploadFuture.completer());
      return uploadFuture;
    }).compose(uploaded -> {
      Future<Long> downloadFuture = Future.future();
      gridFsClient.get().downloadFile(fileName, downloadFuture.completer());

      return downloadFuture;
    }).compose(length -> {
      assertEquals(1024L, length.longValue());
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  @Test
  public void testFileDownloadAs() {

    String fileName = createTempFileWithContent(1024);
    String asFileName = createTempFile();

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createGridFsBucketService("fs", gridFsClientFuture.completer());

    gridFsClientFuture.compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Future<Void> dropFuture = Future.future();
      mongoGridFsClient.drop(dropFuture.completer());
      return dropFuture;
    }).compose(dropped -> {
      Future<String> uploadFuture = Future.future();
      gridFsClient.get().uploadFile(fileName, uploadFuture.completer());
      return uploadFuture;
    }).compose(uploaded -> {
      Future<Long> downloadFuture = Future.future();
      gridFsClient.get().downloadFileAs(fileName, asFileName, downloadFuture.completer());

      return downloadFuture;
    }).compose(length -> {
      assertEquals(1024L, length.longValue());
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

}
