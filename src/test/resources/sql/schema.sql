create table account
(
    id       bigint auto_increment
        primary key,
    email    varchar(255) null,
    password varchar(255) null,
    constraint UK_q0uja26qgu1atulenwup9rxyr
        unique (email)
);

create table post
(
    account_id bigint       null,
    id         bigint auto_increment
        primary key,
    content    varchar(255) null,
    constraint FKe5hjewhnd6trrdgt8i6uapkhy
        foreign key (account_id) references account (id)
);

create table comment
(
    id      bigint auto_increment
        primary key,
    post_id bigint       null,
    content varchar(255) null,
    constraint FKs1slvnkuemjsq2kj4h3vhx7i1
        foreign key (post_id) references post (id)
);

