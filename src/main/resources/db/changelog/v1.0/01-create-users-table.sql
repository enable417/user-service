create table users (
    id bigserial not null primary key,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    patronymic varchar(255) not null,
    date_of_birth date not null
)
