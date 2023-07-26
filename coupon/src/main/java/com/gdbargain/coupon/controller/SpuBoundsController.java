package com.gdbargain.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gdbargain.coupon.entity.SpuBoundsEntity;
import com.gdbargain.coupon.service.SpuBoundsService;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.R;



/**
 * 商品spu积分设置
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-11 10:53:58
 */
@RestController
@RequestMapping("coupon/spubounds")
public class SpuBoundsController {
    @Autowired
    private SpuBoundsService spuBoundsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuBoundsService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuBoundsEntity spuBounds = spuBoundsService.getById(id);

        return R.ok().put("spuBounds", spuBounds);
    }

    /**
     * 保存：保存商品的积分信息
     * 1、CouponFeignService.saveSpuBounds(spuBoundTo);表示传的对象，不是传的基本类型数据
     *     1）、@RequestBody将这个对象转为json。
     *     2）、找到gulimall-coupon服务，给/coupon/spubounds/save发送请求。
     *         将上一步转的json放在请求体位置，发送请求；
     *     3）、对方服务收到请求。请求体里有json数据。
     *         (@RequestBody SpuBoundsEntity spuBounds)；将请求体的json转为SpuBoundsEntity；
     *只要json数据模型是兼容的。双方服务无需使用同一个to
     *
     */
    @PostMapping("/save")
    public R save(@RequestBody SpuBoundsEntity spuBoundsEntity){
		spuBoundsService.save(spuBoundsEntity);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuBoundsEntity spuBounds){
		spuBoundsService.updateById(spuBounds);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuBoundsService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
