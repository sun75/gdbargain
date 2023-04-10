package com.gdbargain.product.service.impl;

import com.gdbargain.product.entity.AttrEntity;
import com.gdbargain.product.service.AttrService;
import com.gdbargain.product.vo.AttrGroupWithAttrsVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.Query;

import com.gdbargain.product.dao.AttrGroupDao;
import com.gdbargain.product.entity.AttrGroupEntity;
import com.gdbargain.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    /**模糊查询
    SELECT * FROM pms_attr_group where
    catelog_id=? and (attr_group_id=key or attr_group_name like %key%)  */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        //参数为0，查所有数据
        if(catelogId != 0){
            wrapper.eq("catelog_id", catelogId);
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params), wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 根据catID查出所有的分组和组里面的属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //1.查询分组信息
        List<AttrGroupEntity> attrGroupEntityList = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        //2.查询所有属性
        List<AttrGroupWithAttrsVO> collect = attrGroupEntityList.stream().map((group) -> {
            AttrGroupWithAttrsVO attrsVO = new AttrGroupWithAttrsVO();
            BeanUtils.copyProperties(group, attrsVO);
            List<AttrEntity> attrs = attrService.getRelationAttr(attrsVO.getAttrGroupId());
            attrsVO.setAttrs(attrs);
            return attrsVO;
        }).collect(Collectors.toList());
        return collect;
    }

}