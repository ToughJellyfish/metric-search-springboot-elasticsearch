package org.example.elasticsearch.repositories;

import org.example.elasticsearch.model.Metric;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
@Qualifier("metrices")
@Repository
@EnableElasticsearchRepositories
public interface MetricRepository extends ElasticsearchRepository<Metric, String> {
    List<Metric> findByName(String name);
    List<Metric> findByNameContains(String nameRegex);



}
