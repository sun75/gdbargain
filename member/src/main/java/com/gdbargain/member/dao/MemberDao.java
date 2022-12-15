package com.gdbargain.member.dao;

import com.gdbargain.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-11 11:11:48
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
