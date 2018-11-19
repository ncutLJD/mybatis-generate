#if(!${entityPackage})
package ${bussPackage}.service.impl;
#else
package ${bussPackage}.service.impl.${entityPackage};
#end

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.minshengec.magic.api.mapper.${className}Mapper;
#if(!${entityPackage})
import ${bussPackage}.service.${className}Service;
#else
import ${bussPackage}.service.${entityPackage}.${className}Service;
#end
import com.minshengec.magic.api.entity.${className}Entity;
import com.minshengec.magic.api.base.service.impl.BaseServiceImpl;
import javax.annotation.Resource;

@Service("$!{lowerName}Service")
public class ${className}ServiceImpl extends BaseServiceImpl<${className}Entity,String> implements ${className}Service {
	private final static Logger logger = Logger.getLogger(${className}ServiceImpl.class);
	@Autowired
    private ${className}Mapper $!{lowerName}Mapper;

	@Resource
	public void setBaseMapper(${className}Mapper $!{lowerName}Mapper) {
		super.setBaseMapper($!{lowerName}Mapper);
	}
}
