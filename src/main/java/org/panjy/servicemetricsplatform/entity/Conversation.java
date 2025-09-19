package org.panjy.servicemetricsplatform.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
    private String wechatId;
    private List<Message> messages;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    
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
}
