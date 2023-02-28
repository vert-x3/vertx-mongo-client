package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.DataObject;
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

  /**
   * The default time field value for timeseries collections.
   */
  public static final String DEFAULT_TIME_FIELD = "timestamp";

  // required for time series collections
  private String timeField;
  private String metaField;
  private TimeSeriesGranularity granularity;

  public TimeSeriesOptions() {
    init();
  }

  private void init() {
    timeField = DEFAULT_TIME_FIELD;
  }

  /**
   * Copy constructor.
   */
  public TimeSeriesOptions(TimeSeriesOptions options) {
    timeField = options.timeField;
    metaField = options.metaField;
    granularity = options.granularity;
  }

  public TimeSeriesOptions(JsonObject json) {
    init();
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
      switch (granularity) {
        case SECONDS:
          timeSeriesOptions.granularity(com.mongodb.client.model.TimeSeriesGranularity.SECONDS);
          break;
        case MINUTES:
          timeSeriesOptions.granularity(com.mongodb.client.model.TimeSeriesGranularity.MINUTES);
          break;
        case HOURS:
          timeSeriesOptions.granularity(com.mongodb.client.model.TimeSeriesGranularity.HOURS);
          break;
        default:
          throw new UnsupportedOperationException(granularity.toString());
      }
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
    this.timeField = timeField;
    return this;
  }

  /**
   * @return the metaField
   */
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
