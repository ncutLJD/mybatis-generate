#if(!${entityPackage})
package ${bussPackage}.entity;
#else
package ${bussPackage}.entity.${entityPackage};
#end

import com.minshengec.magic.api.base.entity.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

public class ${className}Entity extends BaseEntity {

	private static final long serialVersionUID = 1L;	
	${feilds}
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("${className} [");   
#foreach($item in $!{columnDatas})
#if($velocityCount == 1)
        builder.append("$!item.fieldName=");
#else
        builder.append(", $!item.fieldName=");
#end
        builder.append($!item.fieldName);
#end
        builder.append("]");
        return builder.toString();
    }
}

