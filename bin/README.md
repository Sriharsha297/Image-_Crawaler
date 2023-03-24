## Goal
Performs a WebCrawl on a url Provided by the user and parses all the images on the web page

### Functionality
- Finds all images on the target web page(s).
- Crawls sub-pages to find more images.
- Implements multi-threading so that the crawl can be performed on multiple pages at a time.
- Keeps the crawl within the same domain as the input URL.
- Avoids re-crawling any pages that have already been visited.
- Recognizes images that contain people.
- Recognizes logos.

## Requirements
- Maven 3.5
- Java 8

## Setup
> mvn clean test package jetty:run

