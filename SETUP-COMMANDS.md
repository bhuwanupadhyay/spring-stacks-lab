# To setting up the project, run the following commands:

* Create a new directory for the project

```bash
mkdir -p grafana-loki-tempo-prometheus-with-spring-boot-microservices
cd grafana-loki-tempo-prometheus-with-spring-boot-microservices
```

```bash
spring init --dependencies=webflux,data-cassandra-reactive,actuator,devtools orders
spring init --dependencies=webflux,data-jpa,actuator,devtools payments
spring init --dependencies=gateway,actuator,devtools shippings
```