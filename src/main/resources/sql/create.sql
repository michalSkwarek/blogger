-- add few users
INSERT INTO user (id, email, password) VALUES (1, 'a1@gmail.com', '111');
INSERT INTO user (id, email, password) VALUES (2, 'b2@gmail.com', '222');
INSERT INTO user (id, email, password) VALUES (3, 'c3@gmail.com', '333');

-- add few posts
INSERT INTO post (id, content, user_id) VALUES (1, 'post no 1 to user1', 1);
INSERT INTO post (id, content, user_id) VALUES (2, 'post no 2 to user1', 1);
INSERT INTO post (id, content, user_id) VALUES (3, 'post no 3 to user1', 1);
INSERT INTO post (id, content, user_id) VALUES (4, 'post no 1 to user2', 2);

-- add few comments
INSERT INTO comment (id, content, post_id) VALUES (1, 'comment no 1 to post1', 1);
INSERT INTO comment (id, content, post_id) VALUES (2, 'comment no 2 to post1', 1);
INSERT INTO comment (id, content, post_id) VALUES (3, 'comment no 3 to post1', 1);
INSERT INTO comment (id, content, post_id) VALUES (4, 'comment no 1 to post2', 2);
INSERT INTO comment (id, content, post_id) VALUES (5, 'comment no 2 to post2', 2);
