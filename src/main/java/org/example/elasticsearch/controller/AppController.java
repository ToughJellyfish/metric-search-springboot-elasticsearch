package org.example.elasticsearch.controller;



import org.example.elasticsearch.model.Metric;
import org.example.elasticsearch.service.MetricSearchService;
import org.example.elasticsearch.service.SearchService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
public class AppController {


    private MetricSearchService metricSearchService;
    private SearchService searchService ;
    @GetMapping("/hi")
    public String test(){
        return "hi";
    }

    @Autowired
    public AppController(MetricSearchService metricSearchService, SearchService searchService) {
        this.metricSearchService = metricSearchService;
        this.searchService=searchService;
    }


    @GetMapping("/metrics")
    @ResponseBody
    public List<Metric> fetchByName(@RequestParam(value = "q", required = false) String query) {

        List<Metric> metrics = metricSearchService.processSearch(query) ;
        return metrics;
    }

    @GetMapping("/suggestions")
    @ResponseBody
    public List<String> fetchSuggestions(@RequestParam(value = "q", required = false) String query) {

        List<String> suggests = metricSearchService.fetchSuggestions(query);
        return suggests;
    }

    @PutMapping(value = "/add", consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object addMetric( @RequestBody Metric data){

       metricSearchService.addMetrics(data);
       //long count = searchService.count();
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(data.toString(), headers, HttpStatus.CREATED);

    }

    @GetMapping("/populate")
    @ResponseBody
    public Object addBulkMetrics(){
       metricSearchService.populateData();
       return new ResponseEntity<>( HttpStatus.CREATED);
    }

    @DeleteMapping("/deleteAll")
    public Object deleteAll(){
        searchService.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public Object deleteById(@PathVariable("id") String id){
        if(metricSearchService.findById(id)==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            metricSearchService.delete(id);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/count")
    @ResponseBody
    public Object count(){
        long count =searchService.count();
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>("{\"size\":"+count+"}", headers,HttpStatus.OK);
    }



}
