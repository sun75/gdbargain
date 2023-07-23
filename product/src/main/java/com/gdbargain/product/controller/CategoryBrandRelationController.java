package com.gdbargain.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gdbargain.product.entity.BrandEntity;
import com.gdbargain.product.service.AttrGroupService;
import com.gdbargain.product.vo.AttrGroupWithAttrsVO;
import com.gdbargain.product.vo.BrandVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gdbargain.product.entity.CategoryBrandRelationEntity;
import com.gdbargain.product.service.CategoryBrandRelationService;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-09 16:23:20
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取当前品牌所有分类的列表
     * 查询条件：brandId=指定的值
     */
    @GetMapping("/catelog/list")
    public R cateloglist(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
          new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId)
        );
        return R.ok().put("data", data);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * /product/categorybrandrelation/brands/list
     * 参数：catId
     * 1.controller:处理请求，接收和校验数据
     * 2.service:接收controller传来的数据，进行业务处理
     * 3.controller:接收service处理完的数据，封装成页面指定的vo
     */
    @GetMapping("/brands/list")
    public R relationBrandsList(@RequestParam(value = "catId", required = true)Long catId){
        //1.接收数据
        List<BrandEntity> vos = categoryBrandRelationService.getBrandsByCatId(catId);
        //2.交给service
        List<BrandVO> collect = vos.stream().map((item) -> {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandId(item.getBrandId());
            brandVO.setBrandName(item.getName());
            return brandVO;
        }).collect(Collectors.toList());
        //3.处理返回
        return R.ok().put("data", collect);
    }

    /**
     * 获取当前分类下的所有属性分组，还要带上它所属的属性
     * /product/attrgroup/{catelogId}/withattr
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
        //1.查出当前分类下的所有分组

        //2.查出每个属性分组下面的所有属性
        List<AttrGroupWithAttrsVO> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);

        return R.ok().put("data", vos);
    }
}
