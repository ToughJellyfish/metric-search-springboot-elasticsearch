package org.example.elasticsearch.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.elasticsearch.model.Metric;
import org.example.elasticsearch.service.MetricSearchService;
import org.example.elasticsearch.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Controller
@Slf4j
public class UIController {
    private SearchService searchService;
    private MetricSearchService metricSearchService;

    enum STATS {
        SUM,
        AVG,
        MAX,
        MIN
    }
    @Autowired
    public UIController(SearchService searchService, MetricSearchService metricSearchService) {
        this.searchService = searchService;
        this.metricSearchService=metricSearchService;
    }

    @GetMapping("/")
    public String home( Model model) {
        return "home.html";
    }
    @GetMapping("/search")
    public String search( Model model) {


        List<Metric> metrics = searchService.fetchMetricNameContains("CPU");

        model.addAttribute("metric", metrics);
        return "searches.html";
    }

    @GetMapping("/insert")
    public String add(Model model){
        Metric metric = new Metric();
        List<STATS> statsList = new ArrayList<STATS> (Arrays.asList(STATS.values()));
        model.addAttribute("statslist", statsList);
        model.addAttribute("metric", metric);
        //metricSearchService.addMetrics(metric);
        return "add_metric.html";
    }

    @PostMapping("/insert")
    public String submitForm(@ModelAttribute("metric") Metric metric) {
                metricSearchService.addMetrics(metric);
        return "insert_success.html";
    }
}
