# The Unofficial TL (Transports Lausannois) API

Documentation of the Web API: https://tl.remembr.moe/

# Dev

Dependencies

- `boot-clj` https://github.com/boot-clj/boot#install


```
# Clone project
git clone git@bitbucket.org:remembrmoe/tl-api.git

# Build the project ...
boot build
# ... then start the server
java -jar target/project.jar 3000

# Run an instance of nREPL
boot repl
```

# Docker image

Dependencies

- `docker` http://docker.com/

```
# Build docker image and run a container
docker build -t tl-api . && docker run tl-api
```
