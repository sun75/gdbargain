package com.gdbargain.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdbargain.common.utils.PageUtils;
import com.gdbargain.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author holasunhui
 * @email holasunhui@sina.com
 * @date 2022-12-11 11:11:48
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

