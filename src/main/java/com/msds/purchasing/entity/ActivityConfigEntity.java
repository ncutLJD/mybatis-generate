package com.msds.purchasing.entity;

import com.minshengec.magic.api.base.entity.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

public class ActivityConfigEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;	
		/**	*	**/	private Object largePropValue;	/**	*	**/	private String propName;	/**	*	**/	private String propValue;	/**	*	**/	private Integer version;	/**	*	**/	private String activityId;	public Object getLargePropValue() {	    return this.largePropValue;	}	public void setLargePropValue(Object largePropValue) {	    this.largePropValue = largePropValue;	}	public String getPropName() {	    return this.propName;	}	public void setPropName(String propName) {	    this.propName = propName;	}	public String getPropValue() {	    return this.propValue;	}	public void setPropValue(String propValue) {	    this.propValue = propValue;	}	public Integer getVersion() {	    return this.version;	}	public void setVersion(Integer version) {	    this.version = version;	}	public String getActivityId() {	    return this.activityId;	}	public void setActivityId(String activityId) {	    this.activityId = activityId;	}
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ActivityConfig [");   
        builder.append("id=");
        builder.append(id);
        builder.append(", largePropValue=");
        builder.append(largePropValue);
        builder.append(", propName=");
        builder.append(propName);
        builder.append(", propValue=");
        builder.append(propValue);
        builder.append(", version=");
        builder.append(version);
        builder.append(", activityId=");
        builder.append(activityId);
        builder.append("]");
        return builder.toString();
    }
}

