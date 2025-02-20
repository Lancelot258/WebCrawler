import java.util.ArrayList;
import java.util.List;
class CustomTreeNode {
    String url;
    List<CustomTreeNode> children;

    CustomTreeNode(String url) {
        this.url = url;
        this.children = new ArrayList<>();
    }
    String getUrl() {
        return url;
    }
    List<CustomTreeNode> getChildren() {
        return children;
    }
    void setChildren(List<CustomTreeNode> children) {
        this.children = children;
    }
    void setUrl(String url) {
        this.url = url;
    }
    void addChild(CustomTreeNode child) {
        children.add(child);
    }
    @Override
    public String toString() {
        return url;
    }
}