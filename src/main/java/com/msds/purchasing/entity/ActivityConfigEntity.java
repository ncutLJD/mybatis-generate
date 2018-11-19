package com.msds.purchasing.entity;

import com.minshengec.magic.api.base.entity.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

public class ActivityConfigEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;	
	
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
