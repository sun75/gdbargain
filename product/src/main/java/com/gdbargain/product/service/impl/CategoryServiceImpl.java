package com.gdbargain.product.service.impl;

import com.gdbargain.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.common.utils.Query;

import com.gdbargain.product.dao.CategoryDao;
import com.gdbargain.product.entity.CategoryEntity;
import com.gdbargain.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    //sorted()返回的是整数
    //filter()里面是lambda表达式，去掉了一个参数的括号和返回值的大括号，去掉了返回值的return
    //map()，映射，用来改变菜单里面的相关属性：改了menu，将menu重新返回，组装成list
    //map()里面的子菜单，setChildren，设置子菜单
    //getChildrens()递归获取每个子菜单
    //sort：找到父菜单之后，进行排序
    @Cacheable({"categorytest"})
    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);


        //2.组装成父子的树形结构
        //2.1 找到所有的一级分类 filter：看数据库可以得知，一级分类pid=0
        //2.2 找到一级分类的子分类 map ：添加children到实体类
        // map 映射菜单的相关属性：添加children属性
        // .filter(categoryEntity -> categoryEntity.getParentCid() != null)
        //.sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
        List<CategoryEntity> list = entities.stream()


                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map((menu) -> {
                    //1.找到子菜单
                    menu.setChildren(getChildrens(menu, entities));
                    return menu;
                })
                .sorted((menu1, menu2) -> {
                    //2.菜单的排序
                    return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
                })
                .collect(Collectors.toList());


        return list;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //todo 检查当前删除的菜单，是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 找到catelogId的完整路径[父/子/孙]
     * [2/25/225]
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCategoryPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        //更新自己
        this.updateById(category);
        //更新关联表
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    //当前分类如果还有父分类，就继续找
    //225,25,2
    private List<Long> findParentPath(Long catelogId, List<Long> paths){
        //1.收集当前节点的ID
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if(byId.getParentCid() != 0){
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }


    /**
     * 递归查找每个菜单的子菜单
     * root:当前的某个菜单
     * all：所有菜单
     * filter：如果当前菜单的父id=指定菜单的id, 那么说明当前菜单就是子菜单
     * 每一个子菜单都可能有子菜单
     * 排序规则 .sorted(Comparator.comparing(CategoryEntity::getSort))
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream()
                .filter(e -> e.getParentCid().equals(root.getCatId()))
                .map((e) -> {
                    e.setChildren(getChildrens(e, all));
                    return e;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());


                })
                .collect(Collectors.toList());
        return children;
    }


}