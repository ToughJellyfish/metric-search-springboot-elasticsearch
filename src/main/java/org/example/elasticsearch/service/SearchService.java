package org.example.elasticsearch.service;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.example.elasticsearch.model.Metric;
import org.example.elasticsearch.repositories.MetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;

@Service
public class SearchService {

    private MetricRepository metricRepository;

    private  ElasticsearchOperations elasticsearchOperations;



    @Autowired
    public SearchService(@Qualifier("metrices")MetricRepository metricRepository, ElasticsearchOperations elasticsearchOperations) {
        super();
        this.metricRepository = metricRepository;
        this.elasticsearchOperations = elasticsearchOperations;

    }



    public List<Metric> fetchMetricNameContains(final String name){



        return metricRepository.findByNameContains(name);
    }

    public MetricRepository getMetricRepository() {
        return metricRepository;
    }

    @Autowired
    public void setMetricRepository(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    public ElasticsearchOperations getElasticsearchOperations() {
        return elasticsearchOperations;
    }

    public void setElasticsearchOperations(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public void deleteAll(){
        this.metricRepository.deleteAll();
    }

    public List<Metric> getAll(){
        return new ArrayList<Metric>((Collection<? extends Metric>) this.metricRepository.findAll());
    }

    public long count(){
        return this.metricRepository.count();
    }
}
