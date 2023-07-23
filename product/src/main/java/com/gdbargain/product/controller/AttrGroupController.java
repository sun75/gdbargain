package com.gdbargain.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gdbargain.product.entity.AttrEntity;
import com.gdbargain.product.entity.AttrGroupEntity;
import com.gdbargain.product.service.AttrAttrgroupRelationService;
import com.gdbargain.product.service.AttrService;
import com.gdbargain.product.service.CategoryService;
import com.gdbargain.product.vo.AttrGroupRelationVO;
import com.gdbargain.product.vo.AttrGroupWithAttrsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gdbargain.product.service.AttrGroupService;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.R;



/**
 * 属性分组
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-09 16:23:20
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    /**
     * 列表
     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = attrGroupService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId){
        PageUtils page = attrGroupService.queryPage(params, catelogId);

        return R.ok().put("page", page);
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId")Long attrgroupId){
        List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data", entities);
    }

    /**
     * params         收集页面带来的分页参数
     * attrgroupId    收集路径变量
     * 获取属性分组没有关联的其他属性
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params,
                            @PathVariable("attrgroupId")Long attrgroupId){
        PageUtils page = attrService.getNoRelationAttr(params, attrgroupId);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCategoryPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    /**
     * post请求发送过来的都是对象，所以必须要加requestbody，因为封装成对象发过来的
     * @param vos
     * @return
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVO[] vos){
        attrService.deleteRelation(vos);
        return R.ok();
    }

    /**
     * 新增关联关系
     * @param vos
     * @return
     */
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVO> vos){
        relationService.saveBatch(vos);
        return R.ok();
    }

    /**
     * 获取分类ID下所有分组的属性
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
        //1.查出当前分类下的所有分组
        List<AttrGroupWithAttrsVO> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        //2.查出每个分组的所有属性

        return R.ok().put("data", vos);
    }
}
