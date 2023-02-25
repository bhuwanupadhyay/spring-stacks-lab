# To setting up the project, run the following commands:

```bash
spring init --dependencies=webflux,data-cassandra-reactive,actuator,devtools orders
```

```bash
spring init --dependencies=webflux,data-jpa,actuator,devtools payments
```

```bash
spring init --dependencies=gateway,actuator,devtools shippings
```

## Mac OS

* Install dnsmasq

```bash
brew install dnsmasq
```

* Get the LoadBalancer IP address of the `nginx-ingress` service:

```bash
export LB_IP=$(kubectl get svc/nginx-ingress-ingress-nginx-controller -n kube-system -o=jsonpath='{.status.loadBalancer.ingress[0].ip}')
echo "Your LoadBalancer IP is: $LB_IP"
```

* Append the following line to the `dnsmasq.conf` file:

```bash
echo "address=/.localdev/$LB_IP" >> /opt/homebrew/etc/dnsmasq.conf
```

* To start the `dnsmasq` service:

```bash
brew services start dnsmasq
```

if failed, try:

```bash
brew services restart dnsmasq
```

* Add the following line to the `/etc/resolver/localdev` file:

```bash
sudo mkdir -p /etc/resolver
echo "nameserver $LB_IP" | sudo tee /etc/resolver/localdev
cat /etc/resolver/localdev
```

* To verify the configuration, run the following command:

```bash
ping orders.k8s.localdev
```