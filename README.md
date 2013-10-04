ToDo
====
[![Build Status](https://travis-ci.org/illya13/ToDo.png?branch=master)](https://travis-ci.org/illya13/ToDo)

ToDO Web App

[Live demo version](http://188.40.140.195:8080/todo/)

*Users to login:*
- illya / illya
- or register a new user

Dependencies:
=============
- Backend
    - Spring Framework 3.1.x
    - Jetty 8.1.x
    - full list of dependencies could be found in [pom.xml](pom.xml)
- Frontend
    - Bootstrap 2.3.2
    - jquery
    - requirejs
    - Fuel UX

Runtime Requirements:
=====================
- Java 6
- Web Container (tested with Jetty 8.1.x)
- MongoDB 2.4.x [http://188.40.140.195:27017/](http://188.40.140.195:27017/)
- ElasticSearch 0.90.5 [http://188.40.140.195:9300/](http://188.40.140.195:9300/)

REST API:
=========
- **GET** [/user/login]() - login
- **GET** [/user/logout]() - logout
- **POST** [/user]() - create user
- **GET** [/user]() - get all users
- **GET** [/user/{nickname}]() - get user with *nickname*
- **DELETE** [/user/{nickname}]() - gelete user *nickname*
- **POST** [/item]() - create ToDo item
- **GET** [/item]() - get all ToDo items
- **GET** [/item/{id}]() - get ToDo item with *id*
- **POST** [/item/{id}]() - update ToDo item with *id*
- **DELETE** [/item/{id}]() - delete ToDo item with *id*
- **GET** [/item/filter]() - get filtered ToDo items sorted by Date, Priority
- **GET** [/item/suggest]() - get suggests based on ToDo item Titles
- **GET** [/count/item]() - get all items count
- **POST** [/item/{id}/toggle]() - toggle completion status

ElasticSearch
=============
- mapping for item

```javascript
{
      "item" : {
          "properties" : {
              "completed": {
                  "type": "boolean"
              },
              "description" : {
                  "type" : "string"
              },
              "title" : {
                  "type" : "multi_field",
                  "fields" : {
                      "title" : {
                          "type" : "string"
                      },
                      "completion" : {
                          "type" : "completion",
                          "analyzer" : "simple",
                          "payloads" : false,
                          "preserve_separators" : true,
                          "preserve_position_increments" : true,
                          "max_input_len" : 50
                      }
                  }
              },
              "user" : {
                  "type" : "string"
              }
          }
      }
}
````

- get /post mapping

```javascript
http://localhost:9200/todo/item/_mapping
````
