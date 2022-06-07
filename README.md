# metric-search-springboot-elasticsearch
This is a search app using springboot elasticsearch. The supported data are infrastructure performance metric data. 
Supported field:

"name": metric name

"stats": metric aggregation statistics

"desc": metric description

Sample data:

```{"name":"DISK_UTIL","stats":"AVG", "desc": "disk utilization"}```


### Prerequisites:
Elasticsearch cluster 7.0.0 and up

Docker

kubectl


### Build the project:

```mvn clean install```

```mvn clean package spring-boot:repackage``` or  ```mvn clean package spring-boot:repackage -Dmaven.test.skip.exec```


### Create a docker image:

```docker build -t springelasticsearch_app .```


### Deploy the local docker image on Kubernetes:
1. Deploy elasticsearch image on kubernetes

```kubectl apply -f k8s/elasticsearch-deployment.yaml```

2. Deploy springboot application on kubernetes

```kubectl apply -f k8s/bb.yaml```

3. (Optional) Expose to a external ip address

```kubectl expose deployment bb-demo --type=LoadBalancer --name=<service-name> --port <port>```


## UI:

Home page:
http://ip:port/

Search page:
http://ip:port/search

Add metric page: 
http://ip:port/insert


## Rest API:

#### Add sample data:

```curl --request GET 'http://<ip>:<port>/populate' ```

#### Add metric data:

```curl --request PUT 'http://<ip>:<port>/add' --header 'Content-Type: application/json' --data-raw '{"name":"DISK_SIZE","stats":"AVG", "desc": "disk utilization"}'  ```

#### Get metrics by name:

```curl --request GET 'http://<ip>:<port>/metrics?q=<METRIC-NAME>'```

#### Get metrics by regex:

```curl --request GET 'http://<ip>:<port>/suggestions?q=<METRIC-NAME-START-STRING>'```

#### Delete a metric by ID:

```curl --request DELETE 'http://<ip>:<port>/delete/<METRIC-ID>'```

#### Delete all metric data:

```curl --location --request DELETE 'http://<ip>:<port>/deleteAll'```

#### Count stored metric data:

```curl --request GET 'http://<ip>:<port>/count'```



