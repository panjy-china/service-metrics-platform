package org.panjy.servicemetricsplatform.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class UserFirstFeedback {
    private Long id;
    private String wechatId;
    private Integer photoCount;
    private Boolean hasTonguePhoto;
    private Boolean hasBodyTypePhoto;
    private Boolean customerServiceRequested;
    private String analysis;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime = LocalDateTime.now(); // 设置默认值
    
    public UserFirstFeedback() {
        this.createTime = LocalDateTime.now();
    }
    
    public UserFirstFeedback(String wechatId, Integer photoCount, Boolean hasTonguePhoto, 
                           Boolean hasBodyTypePhoto, Boolean customerServiceRequested, String analysis) {
        this.wechatId = wechatId;
        this.photoCount = photoCount;
        this.hasTonguePhoto = hasTonguePhoto;
        this.hasBodyTypePhoto = hasBodyTypePhoto;
        this.customerServiceRequested = customerServiceRequested;
        this.analysis = analysis;
        this.createTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getWechatId() {
        return wechatId;
    }
    
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }
    
    public Integer getPhotoCount() {
        return photoCount;
    }
    
    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
    }
    
    public Boolean getHasTonguePhoto() {
        return hasTonguePhoto;
    }
    
    public void setHasTonguePhoto(Boolean hasTonguePhoto) {
        this.hasTonguePhoto = hasTonguePhoto;
    }
    
    public Boolean getHasBodyTypePhoto() {
        return hasBodyTypePhoto;
    }
    
    public void setHasBodyTypePhoto(Boolean hasBodyTypePhoto) {
        this.hasBodyTypePhoto = hasBodyTypePhoto;
    }
    
    public Boolean getCustomerServiceRequested() {
        return customerServiceRequested;
    }
    
    public void setCustomerServiceRequested(Boolean customerServiceRequested) {
        this.customerServiceRequested = customerServiceRequested;
    }
    
    public String getAnalysis() {
        return analysis;
    }
    
    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}