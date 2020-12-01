create table persisted_nodes(
    client text not null unique check(char_length(client) < 1000),
    nodes text not null
);
