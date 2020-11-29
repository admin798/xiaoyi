package com.yiyi.service.global.entity.respon;

import com.yiyi.service.global.entity.Card;
import lombok.Data;

/**
 * @author : Jack.F
 * @date : 2020/9/23
 */
@Data
public class CardSearchVo extends Card {

    private String fileTitle;

    private String typeTitle;

    private String fileName;

    private String typeName;
}
