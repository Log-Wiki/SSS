-- 유저 권한 등록
insert into authority (type)
values ('ROLE_USER');
insert into authority (type)
values ('ROLE_ADMIN');

-- 유저(관리자, 일반 사용자) 등록
insert into account (email, password, gender, name, phone_number, response_survey_count, create_survey_count, winning_giveaway_count, point, birthday, create_at, modified_at)
values ('admin@naver.com', '$2a$10$SuhlmaC1Rfyn4F7tc0x8duB9YkDjU1PXY2BXI7xtc08hjsI/RmzF.', 'MALE', '최주인', '010-1111-2222', 0, 0, 0, 0, now(), now(), now());
insert into account (email, password, gender, name, phone_number, response_survey_count, create_survey_count, winning_giveaway_count, point, birthday, create_at, modified_at)
values ('user@naver.com', '$2a$10$s493xSE6O4c6kS.TStPjIuiuF0nHXjhemDH3YaSzzIBdFb54EuZNS', 'FEMALE', '김유저', '010-3333-4444', 0, 0, 0, 0, now(), now(), now());

-- 유저에게 권한 부여
insert into account_authority (account_id, authority_id, create_at, modified_at)
values (1, 1, now(), now());
insert into account_authority (account_id, authority_id, create_at, modified_at)
values (1, 2, now(), now());
insert into account_authority (account_id, authority_id, create_at, modified_at)
values (2, 1, now(), now());

-- 나이 성별 공통 코드 등록
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

insert into question(content, survey_id)
values ('문항1', 1);

-- 당첨 상품 등록
insert into giveaway (price, name, giveaway_type, create_at, modified_at)
values (4500, '스타벅스 아이스 아메리카노', 'COFFEE', now(), now());
insert into giveaway (price, name, giveaway_type, create_at, modified_at)
values (1500, '컴포즈 아이스 아메리카노', 'COFFEE', now(), now());

