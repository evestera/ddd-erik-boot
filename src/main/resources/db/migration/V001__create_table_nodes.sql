create table nodes(
    url text not null unique check(char_length(url) < 1000),
    state text not null
);
