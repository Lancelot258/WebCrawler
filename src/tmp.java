//import javax.swing.*;
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.DefaultTreeModel;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class WebCrawler {
//    private static final int MAX_DEPTH = 1;
//    private static final int THREAD_POOL_SIZE = 10;
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/webcrawler";
//    private static final String DB_USER = "root";
//    private static final String DB_PASSWORD = "XjyTyy981103.";
//
//    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
//    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//    private final Queue<Link> linkQueue = new ConcurrentLinkedQueue<>();
//
//    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Crawled Links");
//    private DefaultTreeModel treeModel = new DefaultTreeModel(root);
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            WebCrawler crawler = new WebCrawler();
//            crawler.createAndShowGUI();
//            new Thread(() -> {
//                crawler.initializeDatabase();
//                crawler.startCrawl("https://google.com");
//                crawler.shutdownExecutor();
//            }).start();
//        });
//    }
//
//    public void startCrawl(String startUrl) {
//        linkQueue.add(new Link(startUrl, 0));
//
//        while (!linkQueue.isEmpty() || !executor.isTerminated()) {
//            Link link = linkQueue.poll();
//            if (link == null) continue;
//            String url = link.url;
//            int depth = link.depth;
//
//            if (depth > MAX_DEPTH || visitedUrls.contains(url)) continue;
//
//            visitedUrls.add(url);
//            executor.execute(() -> {
//                try {
//                    List<String> links = downloadAndExtractLinks(url);
//                    System.out.println("Crawled: " + url + ", Found " + links.size() + " links");
//                    saveToDatabase(url, links.size());
//
//                    SwingUtilities.invokeLater(() -> {
//                        DefaultMutableTreeNode parentNode = findNode(root, url);
//                        if (parentNode == null) {
//                            parentNode = new DefaultMutableTreeNode(url);
//                            root.add(parentNode);
//                        }
//                        for (String linkUrl : links) {
//                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(linkUrl);
//                            parentNode.add(childNode);
//                        }
//                        treeModel.reload(root);
//                    });
//
//                    if (depth < MAX_DEPTH) {
//                        for (String linkUrl : links) {
//                            if (!visitedUrls.contains(linkUrl)) {
//                                linkQueue.add(new Link(linkUrl, depth + 1));
//                            }
//                        }
//                    }
//                } catch (IOException e) {
//                    System.err.println("Failed to crawl: " + url + " - " + e.getMessage());
//                }
//            });
//        }
//
//        waitForCompletion();
//    }
//
//    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode root, String url) {
//        Enumeration<?> e = root.breadthFirstEnumeration();
//        while (e.hasMoreElements()) {
//            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
//            if (node.getUserObject().equals(url)) {
//                return node;
//            }
//        }
//        return null;
//    }
//
//    private void waitForCompletion() {
//        try {
//            executor.shutdown();
//            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
//                executor.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            executor.shutdownNow();
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    private List<String> downloadAndExtractLinks(String url) throws IOException {
//        List<String> links = new ArrayList<>();
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setRequestMethod("GET");
//        connection.setConnectTimeout(5000);
//        connection.setReadTimeout(5000);
//
//        if (connection.getResponseCode() == 200) {
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    links.addAll(extractLinks(line));
//                }
//            }
//        }
//        return links;
//    }
//
//    private List<String> extractLinks(String text) {
//        List<String> links = new ArrayList<>();
//        Pattern pattern = Pattern.compile("href=\"(http[s]?://[^\"]+)\"");
//        Matcher matcher = pattern.matcher(text);
//
//        while (matcher.find()) {
//            String link = matcher.group(1);
//            links.add(link);
//        }
//        return links;
//    }
//
//    private void saveToDatabase(String url, int size) {
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
//            String sql = "INSERT INTO crawled_links (url, size, crawl_time) VALUES (?, ?, NOW())";
//            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//                pstmt.setString(1, url);
//                pstmt.setInt(2, size);
//                pstmt.executeUpdate();
//            }
//        } catch (SQLException e) {
//            System.err.println("Failed to save to database: " + e.getMessage());
//        }
//    }
//
//    private void shutdownExecutor() {
//        executor.shutdown();
//        try {
//            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
//                executor.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            executor.shutdownNow();
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    private void initializeDatabase() {
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
//            String sql = "CREATE TABLE IF NOT EXISTS crawled_links (id INT AUTO_INCREMENT PRIMARY KEY, url VARCHAR(2083), size INT, crawl_time DATETIME)";
//            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//                pstmt.executeUpdate();
//            }
//        } catch (SQLException e) {
//            System.err.println("Failed to initialize database: " + e.getMessage());
//        }
//    }
//
//    private void createAndShowGUI() {
//        JFrame frame = new JFrame("Web Crawler Result");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        JTree tree = new JTree(treeModel);
//        JScrollPane scrollPane = new JScrollPane(tree);
//        frame.add(scrollPane);
//
//        frame.setSize(400, 600);
//        frame.setVisible(true);
//    }
//
//    static class Link {
//        String url;
//        int depth;
//
//        Link(String url, int depth) {
//            this.url = url;
//            this.depth = depth;
//        }
//    }
//}
