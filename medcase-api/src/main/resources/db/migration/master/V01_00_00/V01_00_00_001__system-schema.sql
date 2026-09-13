-- 系统表结构基线。
-- 该文件只用于首次上线数据库，直接创建最终结构，不执行历史增量变更。

create table sys_dept (
  dept_id         bigint       not null auto_increment comment '部门id',
  parent_id       bigint       default 0 comment '父部门id',
  ancestors       varchar(50)  default '' comment '祖级列表',
  dept_name       varchar(30)  default '' comment '部门名称',
  order_num       int          default 0 comment '显示顺序',
  leader          varchar(20)  default null comment '负责人',
  phone           varchar(11)  default null comment '联系电话',
  email           varchar(50)  default null comment '邮箱',
  enabled         tinyint      not null default 1 comment '部门启用状态（1启用 0停用）',
  del_flag        tinyint      default 0 comment '逻辑删除（1删除，0未删除）',
  create_user_id  bigint       default null comment '创建者用户ID',
  create_by       varchar(64)  default '' comment '创建者',
  create_time     datetime     default null comment '创建时间',
  update_user_id  bigint       default null comment '更新者用户ID',
  update_by       varchar(64)  default '' comment '更新者',
  update_time     datetime     default null comment '更新时间',
  primary key (dept_id)
) engine=innodb auto_increment=200 comment='部门表';

create table sys_user (
  user_id                  bigint        not null auto_increment comment '用户ID',
  dept_id                  bigint        default null comment '部门ID',
  user_name                varchar(30)   not null comment '用户账号',
  nick_name                varchar(30)   default null comment '用户昵称',
  supplier_id              bigint        default null comment '供应商ID',
  user_type                varchar(2)    default null comment '用户类型（00后台用户，01医生，02患者）',
  id_card_number           varchar(30)   default null comment '身份证号码',
  id_card_front            text          default null comment '身份证正面图片附件JSON',
  id_card_back             text          default null comment '身份证反面图片附件JSON',
  title                    varchar(50)   default null comment '医生职称（患者为空）',
  qualification_certificate text         default null comment '医师职业资格证图片附件JSON',
  review_reason            varchar(500)  default null comment '注册审核拒绝原因',
  email                    varchar(50)   default '' comment '用户邮箱',
  phonenumber              varchar(11)   default '' comment '手机号码',
  sex                      char(1)       default '0' comment '用户性别（0男 1女 2未知）',
  avatar                   varchar(1000) default '' comment '头像附件JSON',
  password                 varchar(100)  default '' comment '密码',
  status                   char(1)       default '0' comment '账号状态（0正常 1停用 3待审核 4审核失败 5注册）',
  del_flag                 char(1)       default '0' comment '逻辑删除（1删除，0未删除）',
  pwd_update_date          datetime      default null comment '密码最后更新时间',
  remark                   varchar(500)  default null comment '备注',
  create_user_id           bigint        default null comment '创建者用户ID',
  create_by                varchar(64)   default '' comment '创建者',
  create_time              datetime      default null comment '创建时间',
  update_user_id           bigint        default null comment '更新者用户ID',
  update_by                varchar(64)   default '' comment '更新者',
  update_time              datetime      default null comment '更新时间',
  primary key (user_id)
) engine=innodb auto_increment=100 comment='用户信息表';

create table sys_post (
  post_id         bigint       not null auto_increment comment '岗位ID',
  post_code       varchar(64)  not null comment '岗位编码',
  post_name       varchar(50)  not null comment '岗位名称',
  post_sort       int          not null comment '显示顺序',
  enabled         tinyint      not null default 1 comment '岗位启用状态（1启用 0停用）',
  remark          varchar(500) default null comment '备注',
  create_user_id  bigint       default null comment '创建者用户ID',
  create_by       varchar(64)  default '' comment '创建者',
  create_time     datetime     default null comment '创建时间',
  update_user_id  bigint       default null comment '更新者用户ID',
  update_by       varchar(64)  default '' comment '更新者',
  update_time     datetime     default null comment '更新时间',
  primary key (post_id)
) engine=innodb comment='岗位信息表';

create table sys_role (
  role_id             bigint       not null auto_increment comment '角色ID',
  role_name           varchar(30)  not null comment '角色名称',
  role_key            varchar(100) not null comment '角色权限字符串',
  role_sort           int          not null comment '显示顺序',
  menu_check_strictly tinyint     default 1 comment '菜单树选择项是否关联显示',
  enabled             tinyint      not null default 1 comment '角色启用状态（1启用 0停用）',
  del_flag            char(1)      default '0' comment '逻辑删除（1删除，0未删除）',
  remark              varchar(500) default null comment '备注',
  create_user_id      bigint       default null comment '创建者用户ID',
  create_by           varchar(64)  default '' comment '创建者',
  create_time         datetime     default null comment '创建时间',
  update_user_id      bigint       default null comment '更新者用户ID',
  update_by           varchar(64)  default '' comment '更新者',
  update_time         datetime     default null comment '更新时间',
  primary key (role_id)
) engine=innodb auto_increment=100 comment='角色信息表';

create table sys_menu (
  id                  bigint        not null auto_increment comment '主键',
  menu_name           varchar(50)   not null comment '菜单名称',
  parent_id           bigint        default 0 comment '父菜单ID',
  order_num           int           default 0 comment '显示顺序',
  route_path          varchar(200)  default '' comment '路由路径',
  vue_component_path  varchar(255)  default null comment '路由对应前端组件路径',
  route_name          varchar(50)   default null comment '路由名称，菜单必填且全局唯一',
  menu_type           char(1)       default '' comment '菜单类型（M目录 C菜单 F按钮）',
  visible             tinyint       not null default 1 comment '菜单显示状态（1显示 0隐藏）',
  enabled             tinyint       not null default 1 comment '菜单启用状态（1启用 0停用）',
  perms               varchar(100)  default null comment '权限标识',
  icon                varchar(100)  default '#' comment '菜单图标',
  create_user_id      bigint        default null comment '创建者用户ID',
  create_by           varchar(64)   default '' comment '创建者',
  create_time         datetime      default null comment '创建时间',
  update_user_id      bigint        default null comment '更新者用户ID',
  update_by           varchar(64)   default '' comment '更新者',
  update_time         datetime      default null comment '更新时间',
  remark              varchar(500)  default '' comment '备注',
  primary key (id)
) engine=innodb auto_increment=2000 comment='菜单权限表';

create unique index uk_sys_menu_route_name on sys_menu (route_name);

create table sys_user_role (
  user_id bigint not null comment '用户ID',
  role_id bigint not null comment '角色ID',
  primary key (user_id, role_id)
) engine=innodb comment='用户和角色关联表';

create table sys_role_menu (
  role_id bigint not null comment '角色ID',
  menu_id bigint not null comment '菜单ID',
  primary key (role_id, menu_id)
) engine=innodb comment='角色和菜单关联表';

create table sys_user_post (
  user_id bigint not null comment '用户ID',
  post_id bigint not null comment '岗位ID',
  primary key (user_id, post_id)
) engine=innodb comment='用户与岗位关联表';

create table sys_dict_type (
  dict_id         bigint       not null auto_increment comment '字典主键',
  dict_name       varchar(100) default '' comment '字典名称',
  dict_type       varchar(100) default '' comment '字典类型',
  enabled         tinyint      not null default 1 comment '字典类型启用状态（1启用 0停用）',
  remark          varchar(500) default null comment '备注',
  create_user_id  bigint       default null comment '创建者用户ID',
  create_by       varchar(64)  default '' comment '创建者',
  create_time     datetime     default null comment '创建时间',
  update_user_id  bigint       default null comment '更新者用户ID',
  update_by       varchar(64)  default '' comment '更新者',
  update_time     datetime     default null comment '更新时间',
  primary key (dict_id)
) engine=innodb auto_increment=100 comment='字典类型表';

create unique index uk_sys_dict_type on sys_dict_type (dict_type);

create table sys_dict_data (
  dict_code       bigint       not null auto_increment comment '字典编码',
  dict_sort       int          default 0 comment '字典排序',
  dict_label      varchar(100) default '' comment '字典标签',
  dict_value      varchar(100) default '' comment '字典键值',
  dict_type       varchar(100) default '' comment '字典类型',
  css_class       varchar(100) default null comment '样式属性（其他样式扩展）',
  list_class      varchar(100) default null comment '表格回显样式',
  is_default      tinyint      not null default 0 comment '是否默认（1是 0否）',
  enabled         tinyint      not null default 1 comment '字典数据启用状态（1启用 0停用）',
  remark          varchar(500) default null comment '备注',
  create_user_id  bigint       default null comment '创建者用户ID',
  create_by       varchar(64)  default '' comment '创建者',
  create_time     datetime     default null comment '创建时间',
  update_user_id  bigint       default null comment '更新者用户ID',
  update_by       varchar(64)  default '' comment '更新者',
  update_time     datetime     default null comment '更新时间',
  primary key (dict_code)
) engine=innodb auto_increment=100 comment='字典数据表';

create table sys_config (
  config_id       int          not null auto_increment comment '参数主键',
  config_name     varchar(100) default '' comment '参数名称',
  config_key      varchar(100) default '' comment '参数键名',
  config_value    varchar(500) default '' comment '参数键值',
  built_in        tinyint      not null default 0 comment '系统内置（1是 0否）',
  remark          varchar(500) default null comment '备注',
  create_user_id  bigint       default null comment '创建者用户ID',
  create_by       varchar(64)  default '' comment '创建者',
  create_time     datetime     default null comment '创建时间',
  update_user_id  bigint       default null comment '更新者用户ID',
  update_by       varchar(64)  default '' comment '更新者',
  update_time     datetime     default null comment '更新时间',
  primary key (config_id)
) engine=innodb auto_increment=100 comment='参数配置表';

create table sys_login_record (
  id              bigint       not null auto_increment comment '主键',
  user_id         bigint       default null comment '用户ID',
  user_type       varchar(2)   default null comment '用户类型',
  user_name       varchar(64)  default null comment '用户账号',
  success         tinyint      default null comment '登录状态（1成功 0失败）',
  ipaddr          varchar(128) default null comment '登录IP地址',
  login_location  varchar(255) default null comment '登录地点',
  browser         varchar(255) default null comment '浏览器',
  os              varchar(255) default null comment '操作系统',
  msg             varchar(255) default null comment '提示消息',
  login_time      datetime     default null comment '登录时间',
  primary key (id)
) engine=innodb comment='登录记录表';

create table sys_audit_record (
  id             bigint unsigned not null auto_increment comment '主键',
  trace_id       varchar(32)  default null comment '业务操作ID',
  user_id        bigint       default null comment '用户ID',
  user_nickname  varchar(64)  default null comment '用户昵称',
  operation      varchar(255) default null comment '操作',
  req_method     varchar(20)  default null comment '请求方法',
  req_path       varchar(255) default null comment '请求路径',
  req_argument   text         default null comment '请求参数',
  result_detail  text         default null comment '结果明细',
  created        datetime     default null comment '创建日期',
  primary key (id)
) engine=innodb comment='审计日志表';

create table sys_notice (
  notice_id       int          not null auto_increment comment '公告ID',
  notice_title    varchar(50)  not null comment '公告标题',
  notice_type     char(1)      not null comment '公告类型（1通知 2公告）',
  notice_content  longblob     default null comment '公告内容',
  enabled         tinyint      not null default 1 comment '公告启用状态（1启用 0关闭）',
  remark          varchar(255) default null comment '备注',
  create_user_id  bigint       default null comment '创建者用户ID',
  create_by       varchar(64)  default '' comment '创建者',
  create_time     datetime     default null comment '创建时间',
  update_user_id  bigint       default null comment '更新者用户ID',
  update_by       varchar(64)  default '' comment '更新者',
  update_time     datetime     default null comment '更新时间',
  primary key (notice_id)
) engine=innodb auto_increment=10 comment='通知公告表';

create table sys_notice_read (
  read_id   bigint   not null auto_increment comment '已读主键',
  notice_id int      not null comment '公告id',
  user_id   bigint   not null comment '用户id',
  read_time datetime not null comment '阅读时间',
  primary key (read_id)
) engine=innodb auto_increment=1 comment='公告已读记录表';

create unique index uk_user_notice on sys_notice_read (user_id, notice_id);
