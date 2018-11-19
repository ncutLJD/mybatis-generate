package com.msds.purchasing.service.impl;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.minshengec.magic.api.mapper.ActivityConfigMapper;
import com.msds.purchasing.service.ActivityConfigService;
import com.minshengec.magic.api.entity.ActivityConfigEntity;
import com.minshengec.magic.api.base.service.impl.BaseServiceImpl;
import javax.annotation.Resource;

@Service("activityConfigService")
public class ActivityConfigServiceImpl extends BaseServiceImpl<ActivityConfigEntity,String> implements ActivityConfigService {
	private final static Logger logger = Logger.getLogger(ActivityConfigServiceImpl.class);
	@Autowired
    private ActivityConfigMapper activityConfigMapper;

	@Resource
	public void setBaseMapper(ActivityConfigMapper activityConfigMapper) {
		super.setBaseMapper(activityConfigMapper);
	}
}
