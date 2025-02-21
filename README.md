# WebCrawler  

## Overview  
This project is a **Multithreaded Web Crawler** implemented in Java. It explores a website starting from an initial URL, recursively extracts all reachable HTTP links, and downloads their content up to a specified depth. To enhance efficiency, it uses **multithreading** for parallel downloading and logs essential data such as URL, size, and crawl time. Additionally, a **GUI-based browsable index** allows users to navigate through crawled links interactively.  

## Features  
- **Recursive Web Crawling**: Extracts and follows links up to a user-defined depth.  
- **Multithreading for Speed**: Uses **Java's ExecutorService** to handle multiple downloads concurrently.  
- **Data Logging**: Stores crawled data (URL, page size, and timestamps) in a **MySQL database**.  
- **Interactive GUI**: Displays crawled links in a hierarchical **tree view** for easy exploration.  
- **Error Handling**: Handles broken links, timeouts, and duplicate URLs efficiently.  

## Technologies Used  
- **Programming Language**: Java  
- **Networking**: `HttpURLConnection` for HTTP requests  
- **Multithreading**: `ExecutorService` for efficient thread management  
- **HTML Parsing**: Regular expressions (optional: `Jsoup` for advanced parsing)  
- **Database**: MySQL for storing crawled data  
- **GUI Framework**: Java Swing  

## How It Works  
1. **Start Crawling**: The crawler begins with a **specified URL**.  
2. **Extract Links**: It extracts `<a href>` links from the HTML content.  
3. **Multithreading Execution**: Each link is processed in a **separate thread** (max 10 concurrent).  
4. **Data Storage**: Extracted information is stored in a **MySQL database**.  
5. **GUI Visualization**: Crawled data is displayed in a **tree structure** using Java Swing.  

## Installation  
### **1. Clone the Repository**  
```sh
git clone https://github.com/Lancelot258/WebCrawler.git
cd WebCrawler
```
### **2. Setup MySQL Database**
Create a database in MySQL:
```sql
CREATE DATABASE web_crawler;
USE web_crawler;
CREATE TABLE crawled_links (
    id INT AUTO_INCREMENT PRIMARY KEY,
    url TEXT NOT NULL,
    size INT,
    crawl_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
Update database credentials in WebCrawler.java:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/web_crawler";
private static final String DB_USER = "your_username";
private static final String DB_PASSWORD = "your_password";
```
### **3.Compile and Run**
```sh
javac -cp .:mysql-connector-java-8.0.26.jar WebCrawler.java
java -cp .:mysql-connector-java-8.0.26.jar WebCrawler
```
## GUI Preview
The application presents a tree view of crawled links in an interactive GUI

## Future Enhancements
- Improve Performance: Implement politeness policies (rate limiting) to avoid overloading servers.
- Better Error Handling: Handle HTTP redirects and implement retry mechanisms for failed requests.
- Scalability: Extend to support distributed crawling across multiple machines.

## License
This project is licensed under the MIT License.

## Contributors
Lancelot







