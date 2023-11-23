- Setting k9s hot keys

```bash
XDG_CONFIG_HOME=$HOME
mkdir -p $XDG_CONFIG_HOME/k9s
vi $XDG_CONFIG_HOME/k9s/hotkey.yml
```

- Copy and paste the following content in the file

```yaml
hotKey:
  shift-1:
    shortCut:    Shift-1
    description: Viewing pods
    command:     pods

  shift-2:
    shortCut:    Shift-2
    description: View deployments
    command:     dp

  shift-3:
    shortCut:    Shift-3
    description: View services
    command:     services

  shift-4:
    shortCut:    Shift-4
    description: View secrets
    command:     secrets

  shift-5:
    shortCut:    Shift-5
    description: View configmaps
    command:     configmaps

  shift-6:
    shortCut:    Shift-6
    description: View persistent volumes
    command:     pv

  shift-7:
    shortCut:    Shift-7
    description: View ingresses
    command:     ingresses

  shift-8:
    shortCut:    Shift-8
    description: View nodes
    command:     nodes

  shift-9:
    shortCut:    Shift-9
    description: View namespaces
    command:     namespaces

  shift-0:
    shortCut:    Shift-0
    description: View storage classes
    command:     sc
```

- Run k9s

```bash
XDG_CONFIG_HOME=$HOME k9s
```