## Tools Used

- [mkcert](https://github.com/FiloSottile/mkcert)
- [gaskmask](https://github.com/2ndalpha/gasmask)

## To setting up the project, run the following commands:

```bash
spring init --dependencies=webflux,data-cassandra-reactive,actuator,devtools orders
```

```bash
spring init --dependencies=webflux,data-jpa,actuator,devtools payments
```

```bash
spring init --dependencies=gateway,actuator,devtools shippings
```

## Mac OSX

* Get the LoadBalancer IP address of the `nginx-ingress` service:

```bash
export LB_IP=$(kubectl get svc/nginx-ingress-ingress-nginx-controller -n kube-addons -o=jsonpath='{.status.loadBalancer.ingress[0].ip}')
echo "Your LoadBalancer IP is: $LB_IP"
```

* Add the following line to the `/etc/hosts` file:

- Grafana

```bash
echo "$LB_IP grafana.k8s.localdev" | sudo tee -a /etc/hosts
```

- https://grafana.k8s.localdev

### Special Note for Mac OSX

* Unlock `/etc/hosts` file if it is locked in macOS:

```bash
sudo chflags nouchg /etc/hosts && sudo chflags noschg /etc/hosts
```

* Lock `/etc/hosts` file

```bash
sudo chflags uchg /etc/hosts && sudo chflags schg /etc/hosts
```
