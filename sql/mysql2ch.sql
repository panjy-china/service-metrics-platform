CREATE TABLE aikang.wechat_account
(
    `id` UInt64 COMMENT '主键ID，唯一标识一条微信账号记录',
    `wechat_id` String COMMENT '微信ID（如 wxid_xxx）',
    `device_account_id` UInt64 COMMENT '设备账号ID（关联设备表）',
    `imei` String COMMENT '设备IMEI号',
    `device_memo` String COMMENT '设备备注信息',
    `account_user_name` String COMMENT '账号用户名（如F0746）',
    `account_real_name` String COMMENT '账号真实姓名',
    `account_nickname` String COMMENT '账号昵称',
    `ke_fu_alive` UInt8 COMMENT '客服是否在线',
    `device_alive` UInt8 COMMENT '设备是否在线',
    `wechat_alive` UInt8 COMMENT '微信是否在线',
    `yesterday_msg_count` Int32 COMMENT '昨日消息数量',
    `seven_day_msg_count` Int32 COMMENT '近7日消息数量',
    `thirty_day_msg_count` Int32 COMMENT '近30日消息数量',
    `total_friend` Int32 COMMENT '好友总数',
    `male_friend` Int32 COMMENT '男性好友数',
    `female_friend` Int32 COMMENT '女性好友数',
    `wechat_group_name` String COMMENT '微信群名称',
    `tenant_id` UInt64 COMMENT '租户ID',
    `nickname` String COMMENT '显示昵称',
    `alias` String COMMENT '微信别名',
    `avatar` String COMMENT '头像地址',
    `gender` UInt8 COMMENT '性别（0=未知，1=男，2=女）',
    `region` String COMMENT '地区',
    `signature` String COMMENT '个性签名',
    `bind_qq` String COMMENT '绑定QQ号',
    `bind_email` String COMMENT '绑定邮箱',
    `bind_mobile` String COMMENT '绑定手机号',
    `create_time` DateTime COMMENT '账号创建时间',
    `current_device_id` UInt64 COMMENT '当前绑定的设备ID',
    `is_deleted` UInt8 COMMENT '是否已删除',
    `delete_time` DateTime COMMENT '删除时间',
    `group_id` UInt64 COMMENT '分组ID',
    `memo` String COMMENT '备注',
    `wechat_version` String COMMENT '微信版本号',
    `last_update_time` DateTime COMMENT '最后更新时间'
)
    ENGINE = MergeTree
ORDER BY (id)
SETTINGS index_granularity = 8192;

CREATE TABLE aikang.wechat_account_first_order
(
    `id` UInt64 COMMENT '主键ID',
    `wechat_id` String COMMENT '微信ID',
    `device_account_id` UInt64 COMMENT '设备账号ID',
    `imei` String COMMENT '设备IMEI',
    `device_memo` String COMMENT '设备备注',
    `account_username` String COMMENT '账号用户名',
    `account_real_name` String COMMENT '账号真实姓名',
    `account_nickname` String COMMENT '账号昵称',
    `nickname` String COMMENT '展示昵称',
    `alias` String COMMENT '微信别名',
    `avatar` String COMMENT '头像地址',
    `gender` UInt8 COMMENT '性别 1=男 2=女 0=未知',
    `region` String COMMENT '地区',
    `bind_mobile` String COMMENT '绑定手机号',
    `memo` String COMMENT '备注',
    `current_device_id` UInt64 COMMENT '当前设备ID',
    `group_id` UInt64 DEFAULT 0 COMMENT '分组ID',
    `is_deleted` UInt8 DEFAULT 0 COMMENT '是否删除',
    `create_time` DateTime DEFAULT now() COMMENT '创建时间'
)
    ENGINE = MergeTree
ORDER BY (id)
SETTINGS index_granularity = 8192;

CREATE TABLE aikang.wechat_account_info
(
    `id` UInt64 COMMENT '主键ID',
    `user_name` String COMMENT '用户名',
    `real_name` String COMMENT '真实姓名',
    `account_type` Int32 COMMENT '账户类型',
    `avatar` String COMMENT '头像',
    `nickname` String COMMENT '昵称',
    `department_id` Int32 COMMENT '部门ID',
    `allot_friend_count` Int32 COMMENT '分配好友数量',
    `daily_allot_friend_count` Int32 COMMENT '每日分配好友数量',
    `today_friend_first_allot_count` Int32 COMMENT '今日首次分配好友数量',
    `allot_chatroom_count` Int32 COMMENT '分配群聊数量',
    `created_time` DateTime DEFAULT now() COMMENT '创建时间',
    `updated_time` DateTime DEFAULT now() COMMENT '更新时间'
)
    ENGINE = ReplacingMergeTree(updated_time)
ORDER BY (user_name)
SETTINGS index_granularity = 8192;


CREATE TABLE aikang.wechat_call
(
    `id` UInt64 COMMENT '主键ID，唯一标识一条通话记录',
    `device_phone` String COMMENT '设备绑定手机号',
    `user_name` String COMMENT '用户系统账号（如工号）',
    `nickname` String COMMENT '用户昵称',
    `real_name` String COMMENT '用户真实姓名',
    `device_memo` String COMMENT '设备备注信息，如设备位置或用途',
    `file_name` String COMMENT '录音文件原始文件名（含扩展名）',
    `imei` String COMMENT '设备唯一标识IMEI（可能为加密或哈希值）',
    `tenant_id` Int32 COMMENT '租户ID，用于多租户数据隔离',
    `device_owner_id` Int32 COMMENT '设备所有者用户ID',
    `phone` String COMMENT '通话中显示的号码（可能脱敏，如178****5121）',
    `is_call_out` UInt8 COMMENT '是否为主动呼出：1=呼出，0=呼入',
    `begin_time` Int64 COMMENT '录音开始时间戳（毫秒级Unix时间戳）',
    `end_time` Int64 COMMENT '录音结束时间戳（毫秒级Unix时间戳）',
    `audio_url` String COMMENT '录音文件在OSS上的完整URL地址',
    `is_deleted` UInt8 DEFAULT 0 COMMENT '是否已逻辑删除：0=未删除，1=已删除',
    `create_time` DateTime COMMENT '记录创建时间（ISO8601格式）',
    `mp3_audio_url` String COMMENT '转换后的MP3音频文件URL（可为空）',
    `call_begin_time` Int64 COMMENT '通话接通时间戳（毫秒级，可能早于录音开始）',
    `call_log_id` Int32 COMMENT '关联的通话日志ID（外键）',
    `call_type` Int32 COMMENT '通话类型：1=普通通话，2=视频通话等（具体看业务定义）',
    `duration` Int32 COMMENT '通话持续时间（秒）',
    `skip_reason` String COMMENT '跳过上传原因（如网络问题等，通常为空）',
    `skip_upload` UInt8 DEFAULT 0 COMMENT '是否跳过上传：0=正常上传，1=跳过',
    `last_update_time` DateTime COMMENT '记录最后更新时间',
    `called_number` String COMMENT '被叫号码（用于呼出场景）',
    `call_type_description` String COMMENT '通话类型描述，如“来电”、“去电”',
    `is_answered` UInt8 COMMENT '是否被接听（1=已接听，0=未接听）',
    `recording_start_time` DateTime COMMENT '录音开始时间（由begin_time转换而来）',
    `recording_end_time` DateTime COMMENT '录音结束时间（由end_time转换而来）',
    `recording_duration` Int32 COMMENT '录音时长（秒），与duration可能不同',
    `call_duration` Int32 COMMENT '实际通话时长（秒），与duration一致',
    `audio_file` String COMMENT '录音文件名（不含路径，与file_name一致）',
    `create_time_field` DateTime COMMENT '创建时间',
    `update_time_field` DateTime COMMENT '更新时间'
)
    ENGINE = MergeTree
ORDER BY (tenant_id, user_name, begin_time)
SETTINGS index_granularity = 8192;

CREATE TABLE IF NOT EXISTS aikang.wechat_friend
(
    `id` UInt64 COMMENT '主键ID',
    `wechat_friend_id` UInt64 COMMENT '微信好友ID',
    `msg_count` Int32 DEFAULT 0 COMMENT '消息数量',
    `send_count` Int32 DEFAULT 0 COMMENT '发送数量',
    `wechat_account_id` UInt64 COMMENT '微信账户ID',
    `owner_nickname` String COMMENT '所有者昵称',
    `owner_wechat_id` String COMMENT '所有者微信ID',
    `owner_avatar` String COMMENT '所有者头像',
    `owner_alias` String COMMENT '所有者别名',
    `wechat_id` String COMMENT '微信ID',
    `alias` String COMMENT '别名',
    `con_remark` String COMMENT '备注',
    `nickname` String COMMENT '昵称',
    `avatar` String COMMENT '头像',
    `gender` Int32 COMMENT '性别',
    `region` String COMMENT '地区',
    `add_from` Int32 COMMENT '添加来源',
    `phone` String COMMENT '手机号',
    `signature` String COMMENT '签名',
    `account_id` UInt64 COMMENT '账户ID',
    `account_user_name` String COMMENT '账户用户名',
    `account_real_name` String COMMENT '账户真实姓名',
    `group_id` Int32 COMMENT '群组ID',
    `created_time` DateTime DEFAULT now() COMMENT '创建时间',
    `updated_time` DateTime DEFAULT now() COMMENT '更新时间'
    )
    ENGINE = MergeTree
    ORDER BY (wechat_id, nickname, created_time)
    SETTINGS index_granularity = 8192
    COMMENT '微信好友信息表';

CREATE TABLE IF NOT EXISTS aikang.wechat_group
(
    `id` UInt64 COMMENT '群记录ID',
    `wechat_account_id` UInt64 COMMENT '群主微信账号ID',
    `wechat_account_alias` String COMMENT '群主微信别名',
    `wechat_account_wechat_id` String COMMENT '群主微信ID',
    `wechat_account_avatar` String COMMENT '群主头像URL',
    `wechat_account_nickname` String COMMENT '群主昵称',
    `chatroom_id` String COMMENT '微信群ID',
    `has_me` UInt8 COMMENT '是否包含我',
    `chatroom_owner_nickname` String COMMENT '群主昵称',
    `chatroom_owner_avatar` String COMMENT '群主头像',
    `con_remark` String COMMENT '备注',
    `nickname` String COMMENT '群名称',
    `py_initial` String COMMENT '拼音首字母',
    `quan_pin` String COMMENT '全拼',
    `chatroom_avatar` String COMMENT '群头像',
    `is_deleted` UInt8 COMMENT '是否已删除',
    `delete_time` DateTime COMMENT '删除时间',
    `create_time` DateTime COMMENT '创建时间',
    `account_id` UInt64 COMMENT '账号ID',
    `account_username` String COMMENT '账号用户名',
    `account_real_name` String COMMENT '账号真实姓名',
    `account_nickname` String COMMENT '账号昵称',
    `group_id` UInt64 COMMENT '组ID'
)
    ENGINE = MergeTree
    ORDER BY (chatroom_id, wechat_account_id, create_time)
    SETTINGS index_granularity = 8192
    COMMENT '微信群信息表';

CREATE TABLE IF NOT EXISTS aikang.wechat_group_member
(
    `id` UInt64 COMMENT '主键ID',
    `group_id` UInt64 COMMENT '群ID（关联 wechat_group.id）',
    `member_id` UInt64 COMMENT '成员ID（关联 wechat_member.id）'
)
    ENGINE = MergeTree
    ORDER BY (group_id, member_id)
    SETTINGS index_granularity = 8192
    COMMENT '微信群成员关系表';

CREATE TABLE IF NOT EXISTS aikang.wechat_group_message
(
    `id` UInt64 COMMENT '消息记录ID',
    `wechat_chatroom_id` UInt64 COMMENT '群ID（关联 wechat_group.id）',
    `sender` String COMMENT '发送者微信ID（可为空）',
    `wechat_account_id` UInt64 COMMENT '关联微信账号ID',
    `wechat_id` String COMMENT '微信账号',
    `tenant_id` UInt64 COMMENT '租户ID',
    `account_id` UInt64 COMMENT '账号ID',
    `synergy_account_id` UInt64 COMMENT '协作账号ID',
    `content` String COMMENT '消息内容（可能为JSON字符串）',
    `msg_type` Int32 COMMENT '消息类型',
    `msg_sub_type` Int32 COMMENT '消息子类型',
    `msg_svr_id` String COMMENT '微信服务器消息ID',
    `is_send` UInt8 COMMENT '是否为自己发送',
    `create_time` DateTime COMMENT '创建时间',
    `is_deleted` UInt8 COMMENT '是否已删除',
    `delete_time` DateTime COMMENT '删除时间',
    `send_status` Int32 COMMENT '发送状态',
    `wechat_time` Int64 COMMENT '微信消息时间戳',
    `origin` Int32 COMMENT '来源',
    `msg_id` UInt64 COMMENT '消息内部ID',
    `recalled` UInt8 COMMENT '是否撤回'
)
    ENGINE = MergeTree
    ORDER BY (wechat_chatroom_id, wechat_account_id, create_time)
    SETTINGS index_granularity = 8192
    COMMENT '微信群消息记录表';

CREATE TABLE IF NOT EXISTS aikang.wechat_member
(
    `id` UInt64 COMMENT '主键ID',
    `wechat_id` String COMMENT '成员微信ID',
    `nickname` String COMMENT '成员昵称',
    `avatar` String COMMENT '成员头像',
    `is_admin` UInt8 COMMENT '是否管理员',
    `is_deleted` UInt8 COMMENT '是否已删除',
    `deleted_date` DateTime COMMENT '删除时间'
)
    ENGINE = MergeTree
    ORDER BY (wechat_id, id)
    SETTINGS index_granularity = 8192
    COMMENT '微信群成员信息表';

CREATE TABLE IF NOT EXISTS aikang.wechat_message
(
    `id` UInt64 COMMENT '主键ID',
    `wechat_nickname` String COMMENT '微信昵称',
    `wechat_id` String COMMENT '微信ID',
    `wechat_account` String COMMENT '微信账户',
    `friend_nickname` String COMMENT '好友昵称',
    `friend_wechat_id` String COMMENT '好友微信ID',
    `friend_wechat_account` String COMMENT '好友微信账户',
    `friend_remark` String COMMENT '好友备注',
    `friend_category` String COMMENT '好友分类',
    `content` String COMMENT '消息内容',
    `sender` String COMMENT '发送者',
    `message_type` String COMMENT '消息类型',
    `send_status` String COMMENT '发送状态',
    `chat_time` DateTime COMMENT '聊天时间',
    `created_time` DateTime COMMENT '创建时间',
    `updated_time` DateTime COMMENT '更新时间'
)
    ENGINE = MergeTree
    ORDER BY (wechat_id, chat_time, id)
    SETTINGS index_granularity = 8192
    COMMENT '聊天记录信息表';

CREATE TABLE IF NOT EXISTS aikang.wechat_message_a_analyze_address
(
    `wechat_id` String COMMENT '客户微信号',
    `msg_type` Int32 COMMENT '消息类型（例如：文本、图片等）',
    `wechat_time` Int64 COMMENT '微信服务器时间戳（毫秒）',
    `content` String COMMENT '聊天消息的内容',
    `address` String COMMENT 'AI分析后的地址'
)
    ENGINE = MergeTree
    ORDER BY (wechat_id, wechat_time)
    SETTINGS index_granularity = 8192
    COMMENT '微信消息AI分析地址表';

CREATE TABLE IF NOT EXISTS aikang.wechat_messages_a
(
    `id` UInt64 COMMENT '聊天消息的唯一标识符',
    `wechat_friend_id` UInt64 COMMENT '与消息关联的微信好友ID',
    `wechat_account_id` UInt64 COMMENT '消息发送者或接收者的微信账号ID',
    `wechat_id` String COMMENT '客户微信号',
    `content` String COMMENT '聊天消息的内容',
    `msg_type` Int32 COMMENT '消息类型（例如：文本、图片等）',
    `msg_sub_type` Int32 COMMENT '消息子类型，用于进一步分类',
    `msg_svr_id` String COMMENT '服务器端消息ID，用于同步',
    `is_send` UInt8 COMMENT '消息是否为发送：1表示发送，0表示接收',
    `create_time` DateTime64(3) COMMENT '消息创建的时间戳',
    `is_deleted` UInt8 COMMENT '消息是否被删除：1表示已删除，0表示未删除',
    `send_status` Int32 COMMENT '消息发送状态（0表示默认状态）',
    `wechat_time` Int64 COMMENT '微信服务器时间戳（毫秒）'
    )
    ENGINE = MergeTree
    ORDER BY (wechat_account_id, wechat_friend_id, create_time)
    SETTINGS index_granularity = 8192
    COMMENT '存储A销售私聊聊天消息的表';
