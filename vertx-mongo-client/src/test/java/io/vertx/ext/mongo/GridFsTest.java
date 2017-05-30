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

import io.vertx.core.Future;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:dbush@redhat.com">David Bush</a>
 */
public class GridFsTest extends MongoTestBase {

  protected MongoClient mongoClient;

  private static String createTempFileWithContent(long length) {

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

  private Boolean fileContentsEqual(String fileName, String compareToFileName) {
    byte[] original = new byte[0];
    try {
      original = Files.readAllBytes(new File(fileName).toPath());
      byte[] copy = Files.readAllBytes(new File(compareToFileName).toPath());
      return Arrays.equals(original, copy);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

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

  @Test
  public void testDownloadStream() {

    long fileLength = (1024 * 3) + 70;
    String fileName = createTempFileWithContent(fileLength);
    String downloadFileName = createTempFile();

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createDefaultGridFsBucketService(gridFsClientFuture.completer());

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
      Future<AsyncFile> openFuture = Future.future();
      vertx.fileSystem().open(downloadFileName, new OpenOptions().setWrite(true), openFuture.completer());
      return openFuture;
    }).compose(asyncFile -> {
      MongoGridFsStreamClient client = MongoGridFsStreamClient.create(gridFsClient.get());
      Future<Long> downloadedFuture = Future.future();
      client.downloadByFileName(asyncFile, fileName, downloadedFuture.completer());
      return downloadedFuture;
    }).compose(length -> {
      assertTrue(fileLength == length);
      assertTrue(fileContentsEqual(fileName, downloadFileName));
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

  }

  @Test
  public void testDownloadStreamWithOptions() {

    long fileLength = (1024 * 3) + 70;
    String fileName = createTempFileWithContent(fileLength);
    String downloadFileName = createTempFile();
    DownloadOptions options = new DownloadOptions();
    options.setRevision(DownloadOptions.DEFAULT_REVISION);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Future<MongoGridFsClient> gridFsClientFuture = Future.future();

    mongoClient.createDefaultGridFsBucketService(gridFsClientFuture.completer());

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
      Future<AsyncFile> openFuture = Future.future();
      vertx.fileSystem().open(downloadFileName, new OpenOptions().setWrite(true), openFuture.completer());
      return openFuture;
    }).compose(asyncFile -> {
      MongoGridFsStreamClient client = MongoGridFsStreamClient.create(gridFsClient.get());
      Future<Long> downloadedFuture = Future.future();
      client.downloadByFileNameWithOptions(asyncFile, fileName, options, downloadedFuture.completer());
      return downloadedFuture;
    }).compose(length -> {
      assertTrue(fileLength == length);
      assertTrue(fileContentsEqual(fileName, downloadFileName));
      testComplete();
    }, Future.future().setHandler(handler -> {
      if (handler.failed()) fail(handler.cause());
    }));
    await();

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
  public void testStreamUpload() {

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
      Future<AsyncFile> openFuture = Future.future();
      vertx.fileSystem().open(fileName, new OpenOptions(), openFuture.completer());
      return openFuture;
    }).compose(asyncFile -> {
      MongoGridFsStreamClient client = MongoGridFsStreamClient.create(gridFsClient.get());
      Future<String> uploadedFuture = Future.future();
      client.uploadByFileName(asyncFile, fileName, uploadedFuture.completer());
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
  public void testStreamUploadWithOptions() {

    String fileName = createTempFileWithContent(1024);
    UploadOptions options = new UploadOptions();
    options.setChunkSizeBytes(1024);
    options.setMetadata(new JsonObject().put("meta_test", "Kamapua`a"));

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
      Future<AsyncFile> openFuture = Future.future();
      vertx.fileSystem().open(fileName, new OpenOptions(), openFuture.completer());
      return openFuture;
    }).compose(asyncFile -> {
      MongoGridFsStreamClient client = MongoGridFsStreamClient.create(gridFsClient.get());
      Future<String> uploadedFuture = Future.future();
      client.uploadByFileNameWithOptions(asyncFile, fileName, options, uploadedFuture.completer());
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

  @Test
  public void testFileDownloadById() {

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
    }).compose(id -> {
      Future<Long> downloadFuture = Future.future();
      gridFsClient.get().downloadFileByID(id, asFileName, downloadFuture.completer());

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
