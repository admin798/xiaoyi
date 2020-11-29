package com.yiyi.service.global.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yiyi.commom.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("note_calculate")
@ApiModel(value="Card对象", description="")
public class Calculate extends BaseEntity {

    private String userId;
    private String typeId;
    private String cardId;
    private Integer familiarity;
    private Integer memoryTime;
    private Date studyTime;

}
