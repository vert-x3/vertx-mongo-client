package io.vertx.ext.mongo;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:dbush@redhat.com">David Bush</a>
 * @author <a href="mailto:kostya05983@mail.ru">Konstantin Volivach</a>
 */
public class GridFsTest extends MongoTestBase {

  protected MongoClient mongoClient;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JsonObject config = getConfig();
    mongoClient = MongoClient.create(vertx, config);
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

    Promise<MongoGridFsClient> mongoGridFsPromise = Promise.promise();

    mongoClient.createGridFsBucketService("fs").onComplete(mongoGridFsPromise);

    mongoGridFsPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(fileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      assertNotNull(id);
      Promise<Void> deletePromise = Promise.promise();
      gridFsClient.get().delete(id).onComplete(deletePromise);
      return deletePromise.future();
    }).onComplete(event -> {
      if (event.succeeded()) {
        testComplete();
      } else {
        fail(event.cause());
      }
    });
    await();
  }

  @Test
  public void testFileUpload() {

    long fileLength = (1024 * 3) + 70;
    String fileName = createTempFileWithContent(fileLength);
    String downloadFileName = createTempFile();

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createGridFsBucketService("fs").onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(fileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      assertNotNull(id);
      Promise<Long> downloadPromise = Promise.promise();
      gridFsClient.get().downloadFileAs(fileName, downloadFileName).onComplete(downloadPromise);
      return downloadPromise.future();
    }).compose(length -> {
      assertEquals((long)length, fileLength);
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      } else {
        testComplete();
      }
    });
    await();
  }


  @Test
  public void testBigFileUpload() {
    String originalFileName = createTempFileWithContent((1024 * 50) + 16);
    long originalLength = new File(originalFileName).length();
    String copiedFileName = createTempFile();

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createGridFsBucketService("fs").onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(originalFileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      assertNotNull(id);
      Promise<Long> downloadPromise = Promise.promise();
      gridFsClient.get().downloadFileAs(originalFileName, copiedFileName).onComplete(downloadPromise);
      return downloadPromise.future();
    }).compose(length -> {
      assertEquals(originalLength, length.longValue());
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      } else {
        testComplete();
      }
    });
    await();
  }

  @Test
  public void testFileUploadWithOptions() {

    String fileName = createTempFileWithContent((1027) + 7000);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    JsonObject meta = new JsonObject();
    meta.put("nick_name", "Puhi the eel");

    GridFsUploadOptions options = new GridFsUploadOptions();
    options.setMetadata(meta);

    mongoClient.createGridFsBucketService("fs").onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFileWithOptions(fileName, options).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      assertNotNull(id);
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();
  }

  @Test
  public void testFindWithMetadata() {
    String fileName = createTempFileWithContent((1024 * 3) + 70);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    JsonObject meta = new JsonObject();
    meta.put("nick_name", "Puhi the eel");

    GridFsUploadOptions options = new GridFsUploadOptions();
    options.setMetadata(meta);

    mongoClient.createGridFsBucketService("fs").onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFileWithOptions(fileName, options).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      assertNotNull(id);
      Promise<List<String>> findPromise = Promise.promise();
      JsonObject query = new JsonObject().put("metadata.nick_name", "Puhi the eel");
      gridFsClient.get().findIds(query).onComplete(findPromise);
      return findPromise.future();
    }).compose(list -> {
      assertFalse(list.isEmpty());
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();
  }

  @Test
  public void testFindAllIds() {

    String fileName = createTempFileWithContent((1024 * 3) + 70);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createGridFsBucketService("fs").onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(fileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      assertNotNull(id);
      Promise<List<String>> findPromise = Promise.promise();
      gridFsClient.get().findAllIds().onComplete(findPromise);
      return findPromise.future();
    }).compose(list -> {
      assertTrue(list.size() == 1);
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();
  }

  @Test
  public void testDrop() {
    createTempFileWithContent((1024 * 3) + 70);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createGridFsBucketService("fs").onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();
  }

  @Test
  public void testReadByFileName() {
    long fileLength = (1024 * 3) + 70;
    String fileName = createTempFileWithContent(fileLength);
    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createDefaultGridFsBucketService().onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(fileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      assertNotNull(id);

      Promise<Long> readPromise = Promise.promise();
      AtomicLong count = new AtomicLong(0);

      ReadStream<Buffer> rs = gridFsClient.get().readByFileName(fileName);
      rs.handler(buffer -> {
        count.addAndGet(buffer.length());
      });
      rs.endHandler(x->{
        readPromise.complete(count.get());
      });

      return readPromise.future();
    }).compose(length -> {
      assertTrue(fileLength == length);
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();

  }

  @Test
  public void testReadByFileNameWithOptions() {
    long fileLength = (1024 * 3) + 70;
    String fileName = createTempFileWithContent(fileLength);
    GridFsDownloadOptions options = new GridFsDownloadOptions();
    options.setRevision(GridFsDownloadOptions.DEFAULT_REVISION);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createDefaultGridFsBucketService().onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(fileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      assertNotNull(id);

      Promise<Long> readPromise = Promise.promise();
      AtomicLong count = new AtomicLong(0);

      ReadStream<Buffer> rs = gridFsClient.get().readByFileNameWithOptions(fileName, options);
      rs.handler(buffer -> {
        count.addAndGet(buffer.length());
      });
      rs.endHandler(x -> {
        readPromise.complete(count.get());
      });

      return readPromise.future();
    }).compose(length -> {
      assertTrue(fileLength == length);
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();
  }

  @Test
  public void testReadById() {
    long fileLength = (1027) + 7000;
    String fileName = createTempFileWithContent(fileLength);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();
    AtomicReference<String> idCreated = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createDefaultGridFsBucketService().onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(fileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      assertNotNull(id);
      idCreated.set(id);

      Promise<Long> readPromise = Promise.promise();
      AtomicLong count = new AtomicLong(0);

      ReadStream<Buffer> rs = gridFsClient.get().readById(idCreated.get());
      rs.handler(buffer -> {
        count.addAndGet(buffer.length());
      });
      rs.endHandler(x -> {
        readPromise.complete(count.get());
      });

      return readPromise.future();
    }).compose(length -> {
      assertTrue(fileLength == length);
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();
  }

  @Test
  public void testDownloadStream() {
    long fileLength = (1024 * 3) + 70;
    String fileName = createTempFileWithContent(fileLength);
    String downloadFileName = createTempFile();

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createDefaultGridFsBucketService().onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(fileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      assertNotNull(id);
      Promise<AsyncFile> openPromise = Promise.promise();
      vertx.fileSystem().open(downloadFileName, new OpenOptions().setWrite(true)).onComplete(openPromise);
      return openPromise.future();
    }).compose(asyncFile -> {
      Promise<Long> downloadedPromise = Promise.promise();
      gridFsClient.get().downloadByFileName(asyncFile, fileName).onComplete(downloadedPromise);
      return downloadedPromise.future();
    }).compose(length -> {
      assertTrue(fileLength == length);
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();

  }

  @Test
  public void testDownloadStreamById() {
    long fileLength = (1027) + 7000;
    String fileName = createTempFileWithContent(fileLength);
    String downloadFileName = createTempFile();

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();
    AtomicReference<String> idCreated = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createDefaultGridFsBucketService().onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(fileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      assertNotNull(id);
      idCreated.set(id);
      Promise<AsyncFile> openPromise = Promise.promise();
      vertx.fileSystem().open(downloadFileName, new OpenOptions().setWrite(true)).onComplete(openPromise);
      return openPromise.future();
    }).compose(asyncFile -> {
      Promise<Long> downloadedPromise = Promise.promise();
      gridFsClient.get().downloadById(asyncFile, idCreated.get()).onComplete(downloadedPromise);
      return downloadedPromise.future();
    }).compose(length -> {
      assertTrue(fileLength == length);
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();
  }

  @Test
  public void testDownloadStreamWithOptions() {
    long fileLength = (1024 * 3) + 70;
    String fileName = createTempFileWithContent(fileLength);
    String downloadFileName = createTempFile();
    GridFsDownloadOptions options = new GridFsDownloadOptions();
    options.setRevision(GridFsDownloadOptions.DEFAULT_REVISION);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createDefaultGridFsBucketService().onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(fileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      assertNotNull(id);
      Promise<AsyncFile> openPromise = Promise.promise();
      vertx.fileSystem().open(downloadFileName, new OpenOptions().setWrite(true)).onComplete(openPromise);
      return openPromise.future();
    }).compose(asyncFile -> {
      Promise<Long> downloadedPromise = Promise.promise();
      gridFsClient.get().downloadByFileNameWithOptions(asyncFile, fileName, options).onComplete(downloadedPromise);
      return downloadedPromise.future();
    }).compose(length -> {
      assertTrue(fileLength == length);
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();
  }

  @Test
  public void testFileDownload() {
    String fileName = createTempFileWithContent(1024);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createGridFsBucketService("fs").onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(fileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(uploaded -> {
      Promise<Long> downloadPromise = Promise.promise();
      gridFsClient.get().downloadFile(fileName).onComplete(downloadPromise);
      return downloadPromise.future();
    }).compose(length -> {
      assertEquals(1024L, length.longValue());
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();

  }

  @Test
  public void testStreamUpload() {
    String fileName = createTempFileWithContent(1024);

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createGridFsBucketService("fs").onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<AsyncFile> openPromise = Promise.promise();
      vertx.fileSystem().open(fileName, new OpenOptions()).onComplete(openPromise);
      return openPromise.future();
    }).compose(asyncFile -> {
      Promise<String> uploadedPromise = Promise.promise();
      gridFsClient.get().uploadByFileName(asyncFile, fileName).onComplete(uploadedPromise);
      return uploadedPromise.future();
    }).compose(id -> {
      assertNotNull(id);
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();

  }

  @Test
  public void testStreamUploadWithOptions() {
    String fileName = createTempFileWithContent(1024);
    GridFsUploadOptions options = new GridFsUploadOptions();
    options.setChunkSizeBytes(1024);
    options.setMetadata(new JsonObject().put("meta_test", "Kamapua`a")
      .put("nested", new JsonObject().put("foobar", true)));

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createGridFsBucketService("fs").onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<AsyncFile> openPromise = Promise.promise();
      vertx.fileSystem().open(fileName, new OpenOptions()).onComplete(openPromise);
      return openPromise.future();
    }).compose(asyncFile -> {
      Promise<String> uploadedPromise = Promise.promise();
      gridFsClient.get().uploadByFileNameWithOptions(asyncFile, fileName, options).onComplete(uploadedPromise);
      return uploadedPromise.future();
    }).compose(id -> {
      assertNotNull(id);
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();
  }

  @Test
  public void testFileDownloadAs() {
    String fileName = createTempFileWithContent(1024);
    String asFileName = createTempFile();

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createGridFsBucketService("fs").onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(fileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(uploaded -> {
      Promise<Long> downloadPromise = Promise.promise();
      gridFsClient.get().downloadFileAs(fileName, asFileName).onComplete(downloadPromise);
      return downloadPromise.future();
    }).compose(length -> {
      assertEquals(1024L, length.longValue());
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();
  }

  @Test
  public void testFileDownloadById() {
    String fileName = createTempFileWithContent(1024);
    String asFileName = createTempFile();

    AtomicReference<MongoGridFsClient> gridFsClient = new AtomicReference<>();

    Promise<MongoGridFsClient> gridFsClientPromise = Promise.promise();

    mongoClient.createGridFsBucketService("fs").onComplete(gridFsClientPromise);

    gridFsClientPromise.future().compose(mongoGridFsClient -> {
      assertNotNull(mongoGridFsClient);
      gridFsClient.set(mongoGridFsClient);
      Promise<Void> dropPromise = Promise.promise();
      mongoGridFsClient.drop().onComplete(dropPromise);
      return dropPromise.future();
    }).compose(dropped -> {
      Promise<String> uploadPromise = Promise.promise();
      gridFsClient.get().uploadFile(fileName).onComplete(uploadPromise);
      return uploadPromise.future();
    }).compose(id -> {
      Promise<Long> downloadPromise = Promise.promise();
      gridFsClient.get().downloadFileByID(id, asFileName).onComplete(downloadPromise);
      return downloadPromise.future();
    }).compose(length -> {
      assertEquals(1024L, length.longValue());
      testComplete();
      return Future.succeededFuture();
    }).onComplete(event -> {
      if (event.failed()) {
        fail(event.cause());
      }
    });
    await();
  }

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
}
