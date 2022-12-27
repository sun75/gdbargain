package com.gdbargain.product.service.impl;

import org.springframework.stereotype.Service;

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


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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