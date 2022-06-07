package org.example.elasticsearch;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.example.elasticsearch.model.Metric;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.json.JsonArray;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@AutoConfigureMockMvc
public class AppControllerTest {

    @Autowired
    private MockMvc mvc;


    public void getHello() throws Exception {

//        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().string(equalTo("Greetings from Spring Boot Elasticsearch!")));
    }

    @Test
    @Order(2)
    public void putAllObjects() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/populate")).andExpect(status().isCreated());
        TimeUnit.SECONDS.sleep(1);

    }



    @Test
    @Order(4)
    public void getObjects() throws Exception {
//        deleteAll();
        MvcResult testResult = this.mvc
                .perform(put("/add")
                        .content(new String("{\"name\":\"NONHEAP_MEM_UTIL\",\"stats\":\"AVG\", \"desc\": \"nonHeap memory utilization\"}".getBytes(), StandardCharsets.UTF_8)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        assertEquals(MediaType.APPLICATION_JSON.toString(), testResult.getResponse().getContentType());

        TimeUnit.SECONDS.sleep(1);
        MvcResult result = this.mvc.perform(get("/metrics").param("q", "NONHEAP_MEM_UTIL")).andReturn();
        assertTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("NONHEAP_MEM_UTIL"));
    }

    @Test
    @Order(5)
    public void deleteAll() throws Exception{
        MvcResult result = this.mvc.perform(delete("/deleteAll")).andExpect(status().isOk()).andReturn();
        TimeUnit.SECONDS.sleep(1);
        MvcResult count= this.mvc.perform(get("/count")).andExpect(status().isOk()).andReturn();
        JsonNode node = getResponseJson(count);

        assertEquals(node.get("size").asLong(), 0l);
    }

    @Test
    @Order(3)
    public void putObjects() throws Exception {
        //deleteAll();
        MvcResult testResult = this.mvc
                .perform(put("/add")
                        .content(new String("{\"name\":\"HEAP_MEM_UTIL\",\"stats\":\"AVG\", \"desc\": \"Heap memory utilization\"}".getBytes(), StandardCharsets.UTF_8)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        assertEquals(MediaType.APPLICATION_JSON.toString(), testResult.getResponse().getContentType());

        JsonNode testResults = getResponseJson(testResult);

        assertEquals(testResults.get("name").asText(), ("HEAP_MEM_UTIL"));

    }
    @Test
    @Order(6)
    public void getMissing() throws Exception{
        MvcResult missingResult = this.mvc.perform(get("/metrics").param("q", "Nonsense")).andReturn();
        assertFalse(missingResult.getResponse().getContentAsString().contains("name"));
    }
    @Test
    @Order(7)
    public void deleteMissing() throws Exception{
        MvcResult missingDelete = this.mvc.perform(delete("/delete/NONSENSE")).andExpect(status().isNotFound()).andReturn();
    }
    @Test
    @Order(8)
    public void deleteById() throws Exception{
       // deleteAll();
        MvcResult testResult = this.mvc
                .perform(put("/add")
                        .content(new String("{\"name\":\"GC_TIMES\",\"stats\":\"AVG\", \"desc\": \"Garbage collection times\"}".getBytes(), StandardCharsets.UTF_8)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        assertEquals(MediaType.APPLICATION_JSON.toString(), testResult.getResponse().getContentType());

        TimeUnit.SECONDS.sleep(1);
        JsonNode testResults = getResponseJson(testResult);
        MvcResult result = this.mvc.perform(get("/metrics").param("q", "GC_TIMES")).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("GC_TIMES"));

        String id=testResults.get("id").asText();
        MvcResult deleteById=this.mvc.perform(delete("/delete/"+id)).andExpect(status().isOk()).andReturn();
        TimeUnit.SECONDS.sleep(1);
        MvcResult result1 = this.mvc.perform(get("/metrics").param("q", "GC_TIMES")).andReturn();
        TimeUnit.SECONDS.sleep(1);
        assertFalse(result1.getResponse().getContentAsString().contains(id));
    }

    @Test
    public void suggestion() throws Exception {
        MvcResult result1 = this.mvc
                .perform(put("/add")
                        .content(new String("{\"name\":\"FILE_SIZE\",\"stats\":\"SUM\", \"desc\": \"File size\"}".getBytes(), StandardCharsets.UTF_8)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        MvcResult result2 = this.mvc
                .perform(put("/add")
                        .content(new String("{\"name\":\"FILE_COUNT\",\"stats\":\"SUM\", \"desc\": \"Number of files\"}".getBytes(), StandardCharsets.UTF_8)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        TimeUnit.SECONDS.sleep(1);

        MvcResult suggestion = this.mvc
                .perform(get("/suggestions").param("q", "FILE")).andDo(print())
                .andReturn();
        ArrayNode arrayNode = (ArrayNode) getResponseJson(suggestion);
        ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<List<String>>() {
        });

        List<String> list = reader.readValue(arrayNode);

        assertTrue(list.contains("FILE_COUNT")&& list.contains("FILE_SIZE"));

    }
    private JsonNode getResponseJson(MvcResult result) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }



}
