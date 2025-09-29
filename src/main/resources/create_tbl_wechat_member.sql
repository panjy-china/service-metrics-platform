-- 创建微信会员表
CREATE TABLE IF NOT EXISTS tbl_wechat_member (
    wechat_id String,
    colCltID String
) ENGINE = MergeTree()
ORDER BY (wechat_id);