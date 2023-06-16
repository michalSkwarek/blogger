-- add few accounts
INSERT INTO account (email, password) VALUES ('a1@gmail.com', '111');
INSERT INTO account (email, password) VALUES ('b2@gmail.com', '222');
INSERT INTO account (email, password) VALUES ('c3@gmail.com', '333');

-- add few posts
INSERT INTO post (content, account_id) VALUES ('post no 1 to account1', 1);
INSERT INTO post (content, account_id) VALUES ('post no 2 to account1', 1);
INSERT INTO post (content, account_id) VALUES ('post no 3 to account1', 1);
INSERT INTO post (content, account_id) VALUES ('post no 1 to account2', 2);

-- add few comments
INSERT INTO comment (content, post_id) VALUES ('comment no 1 to post1', 1);
INSERT INTO comment (content, post_id) VALUES ('comment no 2 to post1', 1);
INSERT INTO comment (content, post_id) VALUES ('comment no 3 to post1', 1);
INSERT INTO comment (content, post_id) VALUES ('comment no 1 to post2', 2);
INSERT INTO comment (content, post_id) VALUES ('comment no 2 to post2', 2);
