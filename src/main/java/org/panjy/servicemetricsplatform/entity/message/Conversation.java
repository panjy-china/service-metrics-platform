package org.panjy.servicemetricsplatform.entity.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Embeddable
public class Conversation {
    private String wechatId;
    private List<Message> messages;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    
    // 手动添加无参构造函数
    public Conversation() {
    }
    
    // 手动添加有参构造函数
    public Conversation(String wechatId, List<Message> messages, LocalDateTime date) {
        this.wechatId = wechatId;
        this.messages = messages;
        this.date = date;
    }
    
    // 手动添加getter方法以确保编译通过
    public String getWechatId() {
        return wechatId;
    }
    
    public List<Message> getMessages() {
        return messages;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    // 手动添加setter方法以确保编译通过
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}