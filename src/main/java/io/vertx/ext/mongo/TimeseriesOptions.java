package io.vertx.ext.mongo;

import com.mongodb.client.model.TimeSeriesGranularity;
import com.mongodb.lang.Nullable;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.json.JsonObject;
import java.util.Objects;

/**
 * Options used to configure timeseries options.
 *
 * <p>Added in MongoDB 5.0 https://www.mongodb.com/docs/manual/core/timeseries-collections/
 *
 * @author <a href="mailto:mail@liuchong.io">Liu Chong</a>
 */
@DataObject(generateConverter = true)
public class TimeSeriesOptions {
  // required for time series collections
  private String timeField;
  private String metaField;
  private TimeSeriesGranularity granularity;

  public TimeSeriesOptions(final String timeField) {
    this.timeField = com.mongodb.assertions.Assertions.notNull("timeField", timeField);
  }

  /**
   * Copy constructor
   *
   * @param options
   */
  public TimeSeriesOptions(TimeSeriesOptions options) {
    timeField = options.timeField;
    metaField = options.metaField;
    granularity = options.granularity;
  }

  public TimeSeriesOptions(JsonObject json) {
    com.mongodb.assertions.Assertions.notNull("timeField", json.getString("timeField"));
    TimeSeriesOptionsConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    TimeSeriesOptionsConverter.toJson(this, json);
    return json;
  }

  public com.mongodb.client.model.TimeSeriesOptions toMongoDriverObject() {
    com.mongodb.client.model.TimeSeriesOptions timeSeriesOptions =
        new com.mongodb.client.model.TimeSeriesOptions(timeField);
    if (metaField != null) {
      timeSeriesOptions.metaField(metaField);
    }
    if (granularity != null) {
      timeSeriesOptions.granularity(granularity);
    }
    return timeSeriesOptions;
  }

  /**
   * @return the timeField
   */
  public String getTimeField() {
    return timeField;
  }

  /**
   * @param timeField the timeField to set
   */
  public TimeSeriesOptions setTimeField(String timeField) {
    this.timeField = com.mongodb.assertions.Assertions.notNull("timeField", timeField);
    return this;
  }

  /**
   * @return the metaField
   */
  @Nullable
  public String getMetaField() {
    return metaField;
  }

  /**
   * @param metaField the metaField to set
   */
  public TimeSeriesOptions setMetaField(String metaField) {
    this.metaField = metaField;
    return this;
  }

  /**
   * @return the granularity
   */
  @Nullable
  public TimeSeriesGranularity getGranularity() {
    return granularity;
  }

  /**
   * @param granularity the granularity to set
   */
  public TimeSeriesOptions setGranularity(TimeSeriesGranularity granularity) {
    this.granularity = granularity;
    return this;
  }

  /**
   * @param granularity the granularity to set, from string
   */
  @GenIgnore
  public TimeSeriesOptions setGranularity(String granularity) {
    granularity =
        com.mongodb.assertions.Assertions.notNull("granularity", granularity).toUpperCase();
    if (Objects.equals(granularity, "SECONDS")) {
      this.granularity = TimeSeriesGranularity.SECONDS;
    } else if (Objects.equals(granularity, "MINUTES")) {
      this.granularity = TimeSeriesGranularity.MINUTES;
    } else if (Objects.equals(granularity, "HOURS")) {
      this.granularity = TimeSeriesGranularity.HOURS;
    } else {
      throw new IllegalArgumentException("invalid granularity string");
    }
    return this;
  }

  @Override
  public String toString() {
    return "TimeseriesOptions{"
        + "timeField='"
        + timeField
        + '\''
        + ", metaField="
        + metaField
        + ", granularity="
        + granularity
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TimeSeriesOptions that = (TimeSeriesOptions) o;
    return Objects.equals(timeField, that.timeField)
        && Objects.equals(metaField, that.metaField)
        && Objects.equals(granularity, that.granularity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timeField, metaField, granularity);
  }
}
