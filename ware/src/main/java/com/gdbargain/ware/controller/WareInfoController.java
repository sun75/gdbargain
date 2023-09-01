package com.gdbargain.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gdbargain.ware.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gdbargain.ware.entity.WareInfoEntity;
import com.gdbargain.ware.service.WareInfoService;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.R;



/**
 * 仓库信息
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-11 11:47:17
 */
@RestController
@RequestMapping("ware/wareinfo")
public class WareInfoController {
    @Autowired
    private WareInfoService wareInfoService;

    /**
     * 查询sku是否有库存
     * @RequestBody List<Long> skuIds:表示将请求体里面的数据，转换成List<Long>集合
     */
    @PostMapping("/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds){
        List<SkuHasStockVo> vos = wareInfoService.getSkuHasStock(skuIds);
        return R.ok().put("data", vos);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareInfoEntity wareInfo = wareInfoService.getById(id);

        return R.ok().put("wareInfo", wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.save(wareInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.updateById(wareInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
