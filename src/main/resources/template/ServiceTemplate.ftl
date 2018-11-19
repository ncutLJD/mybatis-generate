#if(!${entityPackage})
package ${bussPackage}.service;
#else
package ${bussPackage}.service.${entityPackage};
#end
import com.minshengec.magic.api.entity.${className}Entity;
import com.minshengec.magic.api.base.service.BaseService;
public interface ${className}Service extends BaseService<${className}Entity,String>{
}
