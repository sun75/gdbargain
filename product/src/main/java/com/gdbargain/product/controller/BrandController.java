package com.gdbargain.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gdbargain.product.entity.BrandEntity;
import com.gdbargain.product.service.BrandService;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-09 16:23:20
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     * @Valid=：该注解表示需要验证
     */
//    @RequestMapping("/save")
//    public R save(@Valid @RequestBody BrandEntity brand, BindingResult result){
//        Map<String, String> map = new HashMap<>();
//        //1.获取校验的错误结果
//        if(result.hasErrors()){
//            result.getFieldErrors().forEach((i)->{
//                //获取错误提示
//                String message = i.getDefaultMessage();
//                //获取属性名字
//                String field = i.getField();
//                map.put(field, message);
//            });
//            return R.error(400, "提交的数据不合法").put("data", map);
//        }else{
//            brandService.save(brand);
//        }
//
//        return R.ok();
//    }

    //注释掉上面的方法，用下面的方法，没有处理异常
    //不使用BindingResult result处理异常
    //异常交给ControllerAdvice进行集中处理
    @RequestMapping("/save")
    public R save(@Valid @RequestBody BrandEntity brand){

        brandService.save(brand);


        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody BrandEntity brand){
		brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
