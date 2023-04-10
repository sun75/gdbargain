package com.gdbargain.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.gdbargain.product.entity.AttrEntity;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.R;
import com.gdbargain.product.vo.AttrRespVO;
import com.gdbargain.product.vo.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gdbargain.product.service.AttrService;



/**
 * 商品属性
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-09 16:23:20
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * /product/attr/base/list/
     * 传入分页查询的条件 params
     * 传的三级分类的ID catelogId
     *
     * 因为前端里面传的参数是分页参数，有好多个字段的信息，所以前端传来的参数直接封装成map
     * 查询内容：三级分类--->就是路径变量(PathVariable)
     * 查询结束之后，会返回整个分页的信息内容
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId")Long catelogId, @PathVariable("attrType")String type){
        //传入分页查询的条件
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, type);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
		AttrRespVO respVO = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", respVO);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVO attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVO attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
