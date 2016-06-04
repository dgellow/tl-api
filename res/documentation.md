tl-api(1)

current version: v1


_Notes_

_You can get this page in a terminal friendly format:_
```
curl https://tl.remembr.moe
```

## Name

tl-api - Welcome to the unofficial TL Live API

## Synopsis

List of bus and metro lines:

```
curl https://tl.remembr.moe/api/v1/lines
```

Details of the line "m1":

```
curl https://tl.remembr.moe/api/v1/lines/m1
```

The line `:name` or `:id` can be used interchangeably

```
curl https://tl.remembr.moe/api/v1/lines/11821953316814882
```

## Description

The unofficial TL Live API allows you to access information from the
TL Live platform via a clean and public Web API.

You can use the API directly from `curl`, your browser or from any
language you want, just query via HTTP the resource you're interested
into and parse the returned JSON.

A successful response has a the status code `200` and looks like this:

```
{"ok":true,"result":[ ... ]}
```

If the route you're using doesn't match anything in the API the error
response will have the status code `501` and look like this:

```
{"ok":false,"error-code":404,"description":"Method not found"}
```

If the resource you're trying to fetch doesn't exist the error
response will have the status code `501` and look like this:

```
{"ok":false,"error-code":404,"description":"Resource not found"}
```

Any other response should be considered as a bug in the API, if you
encounter one please let us know.

We are interested in your feedbacks, if you use this project and you
want to share an idea, signal a bug or a security issue or just share
your love don't hesitate to contact us. See the AUTHOR section below
for contact information.

This project is independent from any entities and doesn't make money
in any ways, we aren't associated with the Transports Lausannois or
anyone else. See the AUTHOR section below if you want to make a
complain.

## Web API
### lines

Bus and metro lines.  A line has an `:id` and a human readable `:name`
(the name commonly used IRL).

The one that matters is the `:id` but you can use the `:name` if you
prefer, we handle the conversion from `:name` to `:id` if we detect
a short param.

You can get a list of every lines in the TL network:

```
curl https://tl.remembr.moe/api/v1/lines
```

Response:

```
[{"id":"11822125115506799","name":"LEB"},
 {"id":"11821953316814928","name":"BUS LEB"},
 {"id":"11821953316814882","name":"m1"}, ...]
```

And more detailed information for a specific line

```
curl https://tl.remembr.moe/api/v1/m1
# ... or ...
curl https://tl.remembr.moe/api/v1/11821953316814882
```

Response:

```
{"id":"11821953316814882",
 "name":"m1",
 "directions":
   [{"id":"2533279085547610",
     "direction":"11821953316814882",
     "stations":
       [{"id":"3377704015495891","id-stop":"2533279085547610"},
        {"id":"3377704015495524","id-stop":"2533279085547610"},
         ...]}
     ...]}
```

### directions

Lines have in general two directions.

You can have a list of directions for a specific line:

```
curl https://tl.remembr.moe/api/v1/lines/m1/directions
```

Response:

```
{"id-line":"11821953316814882",
 "name-line":"m1",
 "directions":
   [{"id":"2533279085547610", "direction":"11821953316814882"},
    {"id":"2533279085549603","direction":"11821953316814882"}]}
```

You can also get that information from `/lines/:id_or_name` (see LINES).

### stations

Given a direction, a line has stations.

List of stations:

```
curl https://tl.remembr.moe/api/v1/lines/m1/directions/2533279085547610/stations
```

Response:

```
{"id-line":"11821953316814882",
 "name-line":"m1",
 "id-direction":"2533279085547610",
 "stations":
   [{"id":"3377704015495891","id-stop":"2533279085547610"},
    {"id":"3377704015495524","id-stop":"2533279085547610"},
    {"id":"3377704015495445","id-stop":"2533279085547610"},
     ...]}
```

### horaires

Certainly the things you're interested into, that's why the service is
called TL **Live**.  You can access the live schedule for a bus line
given a direction and a station.

Get live horaires:

```
curl https://tl.remembr.moe/api/v1/lines/m1/directions/2533279085547610/stations/3377704015495719/horaires
```

Response:

```
["4'", "7'", "12'", "17:55", ...]
```

## Author

Samuel El-Borai

[https://twitter.com/dgellow](https://twitter.com/dgellow)

[https://github.com/dgellow](https://github.com/dgellow)

[http://www.webp.ch/](http://www.webp.ch/)

[https://github.com/RememberMoe/tl-api](https://github.com/RememberMoe/tl-api)

## See also

Github for bug report and sources - [https://github.com/RememberMoe/tl-api](https://github.com/RememberMoe/tl-api)

Clojure client - [https://github.com/dgellow/tl-api-client](https://github.com/dgellow/tl-api-client)
