package com.yiyi.service.global.entity;

import com.yiyi.commom.base.model.BaseEntity;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Image extends BaseEntity {
    private String userId;
    private String studyTime;
    private String studyPredict;
    private String studyIntroduce;
    private String studyDate;
}
