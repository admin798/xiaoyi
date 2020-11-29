package com.yiyi.service.dataCalculate.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yiyi.commom.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("note_calculate")
@ApiModel(value="Card对象", description="")
public class Calculate extends BaseEntity{

    private Integer familiarity;
    private Integer memoryTime;
    private Date studyTime;
    private String typeId;

}
