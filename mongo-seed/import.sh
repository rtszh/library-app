#! /bin/bash

mongoimport -h mongodb -u root -p root --authenticationDatabase admin -d library-service -c book --type=json --file ./mongo-seed/book.json --jsonArray
#mongoimport -h mongodb -u root -p root --authenticationDatabase admin -d library-service -c page --type=json --file ./mongo-seed/page.json --jsonArray
