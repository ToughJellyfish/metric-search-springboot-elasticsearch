
mvn clean package spring-boot:repackage
or 
mvn clean package spring-boot:repackage -Dmaven.test.skip.exec

Create a docker image:
docker build -t springelasticsearch_app .

deploy on Kubernetes:
kubectl apply -f k8s/elasticsearch-deployment.yaml
kubectl apply -f k8s/bb.yaml
