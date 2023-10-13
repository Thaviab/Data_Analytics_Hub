package rmit.control;

import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import rmit.model.Posts;

import java.util.List;

public class RetrieveTopNPostsFormController {

    public TextArea txtAreaTopPosts;
    public AnchorPane retrieveTopPostsFormContext;

    public void displayPosts(List<Posts> topPosts) {
        StringBuilder displayText = new StringBuilder();

        for(Posts posts : topPosts){
            displayText.append("ID: ").append(posts.getPostID())
                    .append(", Content: ").append(posts.getContent())
                    .append(", Author: ").append(posts.getAuthor())
                    .append(", Likes: ").append(posts.getNoOfLikes())
                    .append(", Shares: ").append(posts.getNoOfShares())
                    .append(", Date & Time: ").append(posts.getDateTime())
                    .append("\n-----------------------------------------------------\n");
        }
        txtAreaTopPosts.setText(displayText.toString());
    }
}
