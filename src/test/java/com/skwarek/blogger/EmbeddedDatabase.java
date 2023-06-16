package com.skwarek.blogger;

import com.skwarek.blogger.domain.Account;
import com.skwarek.blogger.domain.Comment;
import com.skwarek.blogger.domain.Post;

import java.util.*;

public class EmbeddedDatabase {

    public static Map<String, Object> fill() {
        Comment comment1 = new Comment(1L, "comment no 1 to post1", null);
        Comment comment2 = new Comment(2L, "comment no 2 to post1", null);
        Comment comment3 = new Comment(3L, "comment no 3 to post1", null);
        Comment comment4 = new Comment(4L, "comment no 1 to post2", null);
        Comment comment5 = new Comment(5L, "comment no 2 to post2", null);

        Post post1 = new Post(1L, "post no 1 to account1", null, new ArrayList<>(List.of(comment1, comment2, comment3)));
        Post post2 = new Post(2L, "post no 2 to account1", null, new ArrayList<>(List.of(comment4, comment5)));
        Post post3 = new Post(3L, "post no 3 to account1", null, Collections.emptyList());
        Post post4 = new Post(4L, "post no 1 to account2", null, Collections.emptyList());
        post1.getComments().forEach(c -> c.setPost(post1));
        post2.getComments().forEach(c -> c.setPost(post2));
        post3.getComments().forEach(c -> c.setPost(post3));
        post4.getComments().forEach(c -> c.setPost(post4));

        Account account1 = new Account(1L, "a1@gmail.com", "111", new ArrayList<>(List.of(post1, post2, post3)));
        Account account2 = new Account(2L, "b2@gmail.com", "222", new ArrayList<>(List.of(post4)));
        Account account3 = new Account(3L, "c3@gmail.com", "333", Collections.emptyList());
        account1.getPosts().forEach(p -> p.setAccount(account1));
        account2.getPosts().forEach(p -> p.setAccount(account2));
        account3.getPosts().forEach(p -> p.setAccount(account3));

        List<Account> accounts = new ArrayList<>(List.of(account1, account2, account3));
        List<Post> posts = new ArrayList<>(List.of(post1, post2, post3, post4));
        List<Comment> comments = new ArrayList<>(List.of(comment1, comment2, comment3, comment4, comment5));

        Map<String, Object> map = new HashMap<>();
        map.put("accounts", accounts);
        map.put("posts", posts);
        map.put("comments", comments);

        return map;
    }

    @SuppressWarnings("unchecked")
    public static Account createAccountNo(int number) {
        return ((List<Account>) fill().get("accounts")).get(number - 1);
    }

    @SuppressWarnings("unchecked")
    public static Post createPostNo(int number) {
        return ((List<Post>) fill().get("posts")).get(number - 1);
    }

    @SuppressWarnings("unchecked")
    public static Comment createCommentNo(int number) {
        return ((List<Comment>) fill().get("comments")).get(number - 1);
    }

}
