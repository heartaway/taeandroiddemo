package com.taobao.tae.buyingdemo.constant;

/**
 * <p>Api 访问路径</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/12
 * Time: 下午5:18
 */
public class ApiConfig {

    /**
     * 获取商品列表
     */
    public static final String GET_INDEX_BANNERS = "openApi/index/getBanners";

    /**
     * 获取商品详情
     */
    public static final String GET_ITEMS_DETAIL = "openApi/category/getItemDetail";

    /**
     * 获取商品列表
     */
    public static final String GET_INDEX_ITEMS = "openApi/index/getItems";

    /**
     * 获取父分类
     */
    public static final String GET_PARENT_CATEGORY = "openApi/category/getParentCategories";

    /**
     * 获取子分类
     */
    public static final String GET_CHILD_CATEGORY = "openApi/category/getChildCategories";

    /**
     * 获取搜索的商品列表(分类也属于搜索结果)
     */
    public static final String GET_SEARCH_ITEMS = "openApi/category/getItems";

    /**
     * URL 请求参数名称
     */
    public static final String PAGE_NAME = "page";
    public static final String PAGE_SIZE_NAME = "pageSize";
    public static final String SERVER_KEY_NAME = "serverKey";
    public static final String TIME_STAMP_NAME = "timestamp";
    public static final String TIME_NAME = "time";
    public static final String IS_MOCK_NAME = "isMock";
    public static final String SIGN_NAME = "sign";
    public static final String CATEGORY_ID = "categoryId";/*父分类ID*/
    public static final String SUB_CATEGORY_ID = "subCategoryId";/*子分类ID*/

    public static final String SEARCH_TITLE = "title";
    public static final String SEARCH_KEYWORD = "keyword";
    public static final String SEARCH_SORT = "sort";

    public static final String ITEM_ID = "itemId";
    public static final String TAOBAO_ITEM_ID = "tbItemId";
    public static final String ID = "id";
    public static final String BUYER_IP = "buyerIp";


}
