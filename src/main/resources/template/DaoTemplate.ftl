#if(!${entityPackage})
package ${bussPackage}.mapper;
#else
package ${bussPackage}.dao.${entityPackage};
#end

import com.minshengec.magic.api.base.mapper.BaseMapper;
#if(!${entityPackage})
import ${bussPackage}.entity.${className}Entity;
#else
import ${bussPackage}.entity.${entityPackage}.${className};
#end

public interface ${className}Mapper extends BaseMapper<${className}Entity,String> {
}
