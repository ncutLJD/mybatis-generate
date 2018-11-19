#if(!${entityPackage})
package ${bussPackage}.page;
#else
package ${bussPackage}.page.${entityPackage};
#end

import com.msok.base.page.BasePage;
import java.math.BigDecimal;

/**
 * 
 */
public class ${className}Page extends BasePage {
	${feilds}
}
