package com.yiyi.service.images.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class JsonEntity {
    private List<Map<String,Object>> list;

}
