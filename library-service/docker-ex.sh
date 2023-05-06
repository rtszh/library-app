docker run --name docker-hw-ex\
        --rm \
        -p 8080:8080 \
        -e MONGO_HOST=mongodb \
        -e MONGO_PORT=27017 \
        -e MONGO_DATABASE=book_db \
        -e MONGO_USERNAME=root \
        -e MONGO_PASSWORD=root \
        --network hw-34-docker_nw \
        test-hw34


# docker build --tag test-hw34 .