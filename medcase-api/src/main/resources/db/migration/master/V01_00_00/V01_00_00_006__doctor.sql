-- 医生注册资料增量字段
alter table sys_user
    add column id_card_number varchar(30) default null comment '身份证号码' after nick_name,
    add column id_card_front text default null comment '身份证正面图片附件JSON' after id_card_number,
    add column id_card_back text default null comment '身份证反面图片附件JSON' after id_card_front,
    add column title varchar(50) default null comment '医生职称' after id_card_back,
    add column qualification_certificate text default null comment '医师职业资格证图片附件JSON' after title;
