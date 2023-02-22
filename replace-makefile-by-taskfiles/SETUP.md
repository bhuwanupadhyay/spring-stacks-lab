# To setting up the project, run the following commands:

```bash
mkdir -p replace-makefile-by-taskfiles
cd replace-makefile-by-taskfiles
mkdir -p .taskfiles
touch Taskfile.yml
touch .taskfiles/backend.yaml
touch .taskfiles/frontend.yaml
touch .taskfiles/tools.yaml
npx create-react-app frontend
spring init --dependencies=web,data-jpa,h2,actuator backend
```