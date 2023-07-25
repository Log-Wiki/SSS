insert into authority (type)
values ('ROLE_USER');
insert into authority (type)
values ('ROLE_ADMIN');

insert into account (email, password, gender, name, phone_number, response_survey_count, create_survey_count, winning_giveaway_count, point, birthday, create_at, modified_at)
values ('admin@naver.com', '$2a$10$SuhlmaC1Rfyn4F7tc0x8duB9YkDjU1PXY2BXI7xtc08hjsI/RmzF.', 'MALE', '최주인', '010-1111-2222', 0, 0, 0, 0, now(), now(), now());
insert into account (email, password, gender, name, phone_number, response_survey_count, create_survey_count, winning_giveaway_count, point, birthday, create_at, modified_at)
values ('user@naver.com', '$2a$10$s493xSE6O4c6kS.TStPjIuiuF0nHXjhemDH3YaSzzIBdFb54EuZNS', 'FEMALE', '김유저', '010-3333-4444', 0, 0, 0, 0, now(), now(), now());

insert into account_authority (account_id, authority_id, create_at, modified_at)
values (1, 1, now(), now());
insert into account_authority (account_id, authority_id, create_at, modified_at)
values (1, 2, now(), now());
insert into account_authority (account_id, authority_id, create_at, modified_at)
values (2, 1, now(), now());

insert into account_code (type)
values ('MAN');
insert into account_code (type)
values ('WOMAN');
insert into account_code (type)
values ('UNDER_TEENS');
insert into account_code (type)
values ('TEENS');
insert into account_code (type)
values ('TWENTIES');
insert into account_code (type)
values ('THIRTIES');
insert into account_code (type)
values ('FORTIES');
insert into account_code (type)
values ('FIFTIES');
insert into account_code (type)
values ('SIXTIES');

insert into survey(title, HEAD_COUNT, CLOSED_HEAD_COUNT)
values ('설문1', 100, 200);