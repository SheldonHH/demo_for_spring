# demo_for_spring


### Q1

### S

### Question: docker: Error response from daemon: Ports are not available: listen tcp 0.0.0.0:5432: bind: address already in use.

### Soultion: `sudo lsof -i tcp:5432`

```
docker run  -d --name openjdk -it openjdk /bin/bash
docker exec -it $(docker ps | grep -E 'openjdk' | awk '{print $1}') /bin/bash
```
API Layer / Controller Layer
Service Layer
Data Access Layer
```
lsof -i:5432 | awk 'NR==2 {print $2}'
java -jar demo-0.0.1-SNAPSHOT.jar
```
ps -A | grep psql | awk '{print $1}'
pgrep psql

```
docker stop  $(docker ps -a | grep -E 'postgres-spring' | awk '{print $1}' | awk 'NR==1') && docker rm  $(docker ps -a | grep -E 'postgres-spring' | awk '{print $1}' | awk 'NR==1')
docker run --name postgres-spring -e POSTGRES_PASSWORD=password -d -p 5432:5432 postgres:alpine
docker exec -it $(docker ps | grep -E 'postgres-spring' | awk '{print $1}') /bin/bash

docker port postgres-spring
psql
```

JDBC: Java Database Connectivity (JDBC)
flywaydb: applying new migrations

Well, they are just different data formats. Which one's nicer and easier to read? 
That's obviously subjective. Here's a useful blog post.

As far as spring-boot configuration is concerned, 
note that there's only one documented shortcoming of using YAML. Per the documentation:
YAML files can’t be loaded via the @PropertySource annotation. 
So in the case that you need to load values that way,
you need to use a properties file.


As per my knowledge, these are at least some of the differences:

1. .properties stores data in sequential format, whereas
.yml stores data in hierarchical format.
2 .properties supports only key-value pairs (basically string values), whereas
.yml supports key-value pair, as well as map, list & scalar type values.
3 .properties is specifically used by Java, whereas
.yml can be used by other languages (eg Java, Python, ROR, etc).
4 When managing multiple configuration profiles, then:
.properties requires you to create .properties file per every profile, whereas in
.yml you can create a section for each specific profile inside a single .yml file.
2. In Spring projects, @PropertySource annotation can only be used with .properties.
