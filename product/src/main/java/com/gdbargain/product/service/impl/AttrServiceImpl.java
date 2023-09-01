package com.gdbargain.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gdbargain.product.dao.AttrAttrgroupRelationDao;
import com.gdbargain.product.dao.AttrGroupDao;
import com.gdbargain.product.dao.CategoryDao;
import com.gdbargain.product.entity.AttrAttrgroupRelationEntity;
import com.gdbargain.product.entity.AttrGroupEntity;
import com.gdbargain.product.entity.CategoryEntity;
import com.gdbargain.product.service.CategoryService;
import com.gdbargain.product.vo.AttrGroupRelationVO;
import com.gdbargain.product.vo.AttrRespVO;
import com.gdbargain.product.vo.AttrVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.Query;

import com.gdbargain.product.dao.AttrDao;
import com.gdbargain.product.entity.AttrEntity;
import com.gdbargain.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        //要保证VO和PO里面的属性名要一一对应
        //1.保存基本数据
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        //2.保存关联关系
        if(attr.getAttrGroupId() != null){
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type", "base".equalsIgnoreCase(type)?1:0);
        if(catelogId != 0){
            queryWrapper.eq("catelogId",catelogId);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((e) -> {
                e.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params), queryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        /******************添加分类和分组的时候有问题***************************/

        List<AttrEntity> records = page.getRecords();
        List<AttrRespVO> respVOS = records.stream().map((e) -> {
            //e是attrEntity
            AttrRespVO attrRespVO = new AttrRespVO();
            BeanUtils.copyProperties(e, attrRespVO);
            //1.设置分组
            if("base".equalsIgnoreCase(type)){
                AttrAttrgroupRelationEntity attrId =
                        relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", e.getAttrId()));
                if (attrId != null && attrId.getAttrGroupId() != null) {
                    Long attrGroupId = attrId.getAttrGroupId();
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                    attrRespVO.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            //2.设置分类名字
            CategoryEntity categoryEntity = categoryDao.selectById(e.getCatelogId());
            if (categoryEntity != null) {
                attrRespVO.setCatelogName(categoryEntity.getName());
            }
            return attrRespVO;
        }).collect(Collectors.toList());

        pageUtils.setList(respVOS);

        /*********************************************/
        return pageUtils;
    }

    @Override
    public AttrRespVO getAttrInfo(Long attrId) {
        //1.先查属性的详细信息
        AttrEntity attrEntity = this.getById(attrId);
        AttrRespVO attrRespVO = new AttrRespVO();

        //2.将查出的entity 拷贝到VO里面，将VO返回给前端
        BeanUtils.copyProperties(attrEntity, attrRespVO);

        //3.需要查询 attrRespVO.setAttrGroupId();  attrRespVO.setCatelogPath();
        //3.1设置分组信息
        AttrAttrgroupRelationEntity attrgroupRelation =
                relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
        //分组信息非空
        if(attrgroupRelation != null){
            attrRespVO.setAttrGroupId(attrgroupRelation.getAttrGroupId());
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelation.getAttrGroupId());
            if(attrGroupEntity != null){
                attrRespVO.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }
        //3.2设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] categoryPath = categoryService.findCategoryPath(catelogId);
        attrRespVO.setCatelogPath(categoryPath);

        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if(categoryEntity != null){
            attrRespVO.setCatelogName(categoryEntity.getName());
        }

        return attrRespVO;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        //1.修改分组关联
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attr.getAttrId());
        Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
        if(count > 0){
            relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
        }else {
            relationDao.insert(relationEntity);
        }
    }

    /**
     * 根据分组ID查找关联的所有基本属性
     * 在relation表根据attrgroupId查到attrID, 然后用attrID到attr表查询
     * @param attrgroupId
     * @return
     */

    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        //因为要使用attrid，所以直接将relation表的attrID收集起来
        List<Long> attrIds = entities.stream().map((e) -> {
            Long attrId = e.getAttrId();
            return attrId;
        }).collect(Collectors.toList());

        if(attrIds == null || attrIds.size() == 0){
            return null;
        }

        //根据所有attr的id集合，查到所有attr的信息
        List<AttrEntity> entityList = this.listByIds(attrIds);
        return entityList;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVO[] vos) {
        //不推荐，因为要删除多条，就要发送多次请求
        //relationDao.delete(new QueryWrapper<>().eq("attr_id", 1L).eq("attrgroup_id", 1L))
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map((e) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            //将遍历的值复制到对应的relationEntity中去
            BeanUtils.copyProperties(e, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());

        relationDao.deleteBatchRelation(entities);
    }

    /**
     * 获取属性分组没有关联的其他属性
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //1.当前分组只能关联自己所属的分类里面的所有属性
        //查出当前分组的信息
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2.当前分组只能关联别的分组没有引用的属性
        //2.1找到当前分类下的其他分组
        List<AttrGroupEntity> group =
                attrGroupDao.selectList(
                        new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId).ne("attr_group_id", attrgroupId));
        List<Long> collect = group.stream().map((e) -> {
            return e.getAttrGroupId();
        }).collect(Collectors.toList());
        //2.2找到这些分组关联的属性
        List<AttrAttrgroupRelationEntity> groupId =
                relationDao.selectList(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        //其他分组关联的所有属性
        List<Long> attrIds = groupId.stream().map((e) -> {
            return e.getAttrId();
        }).collect(Collectors.toList());
        //2.3从当前属性中移除这些属性
        //查询条件
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId);
        //不是空才来拼装
        if(attrIds != null && attrIds.size() > 0){
            wrapper.notIn("attr_id", attrIds);
        }
        //拼接模糊查询的条件
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w) -> {
                w.eq("attr_id", key).like("attr_name", key);
            });
        }

        //分页方法
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    /**
     * 在所有的指定集合里面，挑出所有的检索属性
     * select attr_id from pms_attr where attr_id in (?) and search_type = 1
     */
    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        return baseMapper.selectSearchAttrIds(attrIds);
    }


}