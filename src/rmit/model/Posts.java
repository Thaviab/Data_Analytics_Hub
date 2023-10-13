package rmit.model;

public class Posts {
    private int postID;
    private String content;
    private String author;
    private int noOfLikes;
    private int noOfShares;
    private String dateTime;

    public Posts(int postID, String content, String author, int noOfLikes, int noOfShares, String dateTime) {
        this.postID = postID;
        this.content = content;
        this.author = author;
        this.noOfLikes = noOfLikes;
        this.noOfShares = noOfShares;
        this.dateTime = dateTime;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getNoOfLikes() {
        return noOfLikes;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.noOfLikes = noOfLikes;
    }

    public int getNoOfShares() {
        return noOfShares;
    }

    public void setNoOfShares(int noOfShares) {
        this.noOfShares = noOfShares;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Posts{" +
                "postID=" + postID +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", noOfLikes=" + noOfLikes +
                ", noOfShares=" + noOfShares +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}
