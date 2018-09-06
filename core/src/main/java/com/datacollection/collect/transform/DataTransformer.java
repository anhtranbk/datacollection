package com.datacollection.collect.transform;

import com.datacollection.collect.model.GraphModel;
import com.datacollection.extract.EventType;
import com.datacollection.entity.GenericModel;

public interface DataTransformer {

    static DataTransformer create(String type) {
        switch (type) {
            case EventType.TYPE_FB_FANPAGE_POST:
                return new FbPagePostTransformer();
            case EventType.TYPE_FB_FANPAGE_COMMENT:
                return new FbPageCmtTransformer();
            case EventType.TYPE_FB_GROUP_POST:
                return new FbGroupPostTransformer();
            case EventType.TYPE_FB_GROUP_COMMENT:
                return new FbGroupCmtTransformer();
            case EventType.TYPE_FB_PROFILE_NEW:
                return new FbProfile2Transformer();

            case EventType.TYPE_FORUM_ARTICLE:
                return new ForumArtTransformer();
            case EventType.TYPE_FORUM_COMMENT:
                return new ForumCmtTransformer();

            case EventType.TYPE_ZAMBA:
                return new ZambaTransformer();

            case EventType.TYPE_ORG:
                return new OrgTransformer();

            case EventType.TYPE_DMP:
                return new DmpTransformer();

            case EventType.TYPE_ECOMMERCE:
                return new EcommerceTransformer();
            case EventType.TYPE_API_VIETID:
                return new VietIdTransformer();

            case EventType.TYPE_API_RB:
                return new RbTransformer();

            default:
                throw new IllegalArgumentException("Invalid data type");
        }
    }

    GraphModel transform(GenericModel generic);
}
