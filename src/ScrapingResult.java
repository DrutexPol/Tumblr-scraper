import java.util.HashSet;

public class ScrapingResult {
    private HashSet<String> urls;
    private String keyword;

    public ScrapingResult(HashSet<String> urls, String keyword) {
        this.urls = urls;
        this.keyword = keyword;
    }

    public HashSet<String> getUrls() {
        return urls;
    }

    public String getKeyword() {
        return keyword;
    }
}
