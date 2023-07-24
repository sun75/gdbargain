package com.gdbargain.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.product.entity.SpuInfoDescEntity;
import com.gdbargain.product.entity.SpuInfoEntity;
import com.gdbargain.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-09 16:23:20
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveSpuInfo(SpuInfoEntity infoEntity);

}

