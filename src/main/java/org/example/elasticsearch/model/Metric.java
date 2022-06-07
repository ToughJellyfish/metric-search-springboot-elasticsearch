package org.example.elasticsearch.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(indexName = "metricindex")
public class Metric {
    @Id
    private String id;

    @Field(type=FieldType.Keyword, name = "name")
    @JsonProperty
    public String name;

    @Field(type=FieldType.Keyword, name = "stats")
    @JsonProperty
    public String stats;


    @Field(type=FieldType.Date, name="timestamp")
    @JsonProperty
    public String timestamp;

    @Field(type=FieldType.Text, name="desc")
    @JsonProperty
    public String desc;

    public Metric(){

    }

    public Metric(String id, String name, String stats, String timestamp, String desc) {
        this.id = id;
        this.name = name;
        this.stats = stats;
        this.timestamp = timestamp;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStats() {
        return stats;
    }

    public void setStats(String stats) {
        this.stats = stats;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + '\"' +
                ", \"name\":\"" + name + '\"' +
                ", \"stats\":\"" + stats + '\"' +
                ", \"timestamp\":\"" + timestamp + '\"' +
                ", \"desc\":\"" + desc + '\"' +
                '}';
    }
}
