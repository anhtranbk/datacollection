package com.vcc.bigdata.collect.transform;

import com.vcc.bigdata.collect.model.GraphModel;
import com.vcc.bigdata.extract.model.GenericModel;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface DataTransformer {

    static DataTransformer create(String type) {
        switch (type) {
            case GenericModel.TYPE_FB_FANPAGE_POST:
                return new FbPagePostTransformer();
            case GenericModel.TYPE_FB_FANPAGE_COMMENT:
                return new FbPageCmtTransformer();
            case GenericModel.TYPE_FB_GROUP_POST:
                return new FbGroupPostTransformer();
            case GenericModel.TYPE_FB_GROUP_COMMENT:
                return new FbGroupCmtTransformer();
            case GenericModel.TYPE_FB_PROFILE_NEW:
                return new FbProfile2Transformer();

            case GenericModel.TYPE_FORUM_ARTICLE:
                return new ForumArtTransformer();
            case GenericModel.TYPE_FORUM_COMMENT:
                return new ForumCmtTransformer();

            case GenericModel.TYPE_ZAMBA:
                return new ZambaTransformer();

            case GenericModel.TYPE_ORG:
                return new OrgTransformer();

            case GenericModel.TYPE_DMP:
                return new DmpTransformer();

            case GenericModel.TYPE_ECOMMERCE:
                return new EcommerceTransformer();
            case GenericModel.TYPE_API_VIETID:
                return new VietIdTransformer();

            case GenericModel.TYPE_API_RB:
                return new RbTransformer();

            default:
                throw new IllegalArgumentException("Invalid data type");
        }
    }

    GraphModel transform(GenericModel generic);
}
