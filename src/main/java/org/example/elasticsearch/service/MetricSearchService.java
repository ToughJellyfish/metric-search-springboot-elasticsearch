package org.example.elasticsearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.example.elasticsearch.model.Metric;
import org.example.elasticsearch.repositories.MetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MetricSearchService {

    private static final String  METRIC_INDEX = "metricindex";

    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public MetricSearchService(final ElasticsearchOperations elasticsearchOperations) {
        super();
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<IndexedObjectInformation> createMetricIndexBulk(final List<Metric> metrics) {

        List<IndexQuery> queries = metrics.stream()
                .map(product -> new IndexQueryBuilder().withId(product.getId().toString()).withObject(product).build())
                .collect(Collectors.toList());
        ;

        return elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(METRIC_INDEX));

    }

    public String createMetricIndex(Metric metric) {

        IndexQuery indexQuery = new IndexQueryBuilder().withId(metric.getId().toString()).withObject(metric).build();
        String documentId = elasticsearchOperations.index(indexQuery, IndexCoordinates.of(METRIC_INDEX));

        return documentId;
    }


    public void findByName(final String metricName) {
        Query searchQuery = new StringQuery(
                "{\"match\":{\"name\":{\"query\":\""+ metricName + "\"}}}\"");

        SearchHits<Metric> products = elasticsearchOperations.search(searchQuery, Metric.class,
                IndexCoordinates.of(METRIC_INDEX));
    }

    public void findByMetricStats(final String stats) {
        Criteria criteria = new Criteria("stats");
        Query searchQuery = new CriteriaQuery(criteria);

        SearchHits<Metric> products = elasticsearchOperations.search(searchQuery, Metric.class,
                IndexCoordinates.of(METRIC_INDEX));
    }

    public List<Metric> processSearch(final String query) {
        log.info("Search with query {}", query);


        QueryBuilder queryBuilder =
                QueryBuilders
                        .multiMatchQuery(query, "name")
                        .fuzziness(Fuzziness.AUTO);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(queryBuilder)
                .build();

        SearchHits<Metric> productHits =
                elasticsearchOperations
                        .search(searchQuery, Metric.class,
                                IndexCoordinates.of(METRIC_INDEX));


        List<Metric> metricMatches = new ArrayList<Metric>();
        productHits.forEach(srchHit->{
            metricMatches.add(srchHit.getContent());
        });
        return metricMatches;
    }




    public List<String> fetchSuggestions(String query) {
        QueryBuilder queryBuilder = QueryBuilders
                .wildcardQuery("name", query+"*");

        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(queryBuilder)
                .withPageable(PageRequest.of(0, 5))
                .build();

        SearchHits<Metric> searchSuggestions =
                elasticsearchOperations.search(searchQuery,
                        Metric.class,
                        IndexCoordinates.of(METRIC_INDEX));

        List<String> suggestions = new ArrayList<String>();

        searchSuggestions.getSearchHits().forEach(searchHit->{
            suggestions.add(searchHit.getContent().getName());
        });
        return suggestions;
    }


    public void addMetrics(Metric metric){
        elasticsearchOperations.save(metric);

    }


    public void populateData(){
        Resource resource = new ClassPathResource("MetricDefConf.json");
        try (InputStream inputStream = resource.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();


            List<Metric> metrics =  mapper.readValue(inputStream, mapper.getTypeFactory().constructCollectionType(List.class, Metric.class));

            elasticsearchOperations.save(metrics);
        }catch(Exception e){
                e.printStackTrace();
        }
    }

    public Metric findById(final String id) {

        if(elasticsearchOperations.exists(id, Metric.class)){
            return elasticsearchOperations.get(id, Metric.class);
        }else{
            return null;
        }
    }
    public void delete(String id){
        elasticsearchOperations.delete(id, Metric.class);
    }




}
