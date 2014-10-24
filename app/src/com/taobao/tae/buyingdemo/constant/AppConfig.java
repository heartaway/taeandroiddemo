package com.taobao.tae.buyingdemo.constant;

/**
 * <p>APP 配置信息</p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/12
 * Time: 下午5:18
 */
public class AppConfig {

    /**
     * 服务端提供 API 地址
     * 如果您的服务端部署在TAE环境中，请修改为您TAE环境中的服务地址
     * 注意：需要以 / 结尾
     */
    public final static String SERVER_DOMAIN = "http://baichuannew.wx.jaeapp.com/";

    /**
     * 当 App Crash后发送异常日志的接收者
     */
    public final static String APP_CRASH_REPORT_MAIL = "xinyuan.ymm@alibaba-inc.com";

    /**
     * ServerKey用于与服务端进行数据交互时的鉴权，而非百川项目中的前台Key或者后台Key，也建议ISV不要将百川KEY和Secret暴露与代码中
     */
    public final static long SERVER_KEY = 1234l;

    /**
     * ServerSecret用于与服务端进行数据交互时的鉴权，而非百川项目中的前台Key或者后台Key，也建议ISV不要将百川KEY和Secret暴露与代码中
     */
    public final static String SERVER_SECRET = "5678";

    /**
     * 用户多次点击返回按钮，退出App的间隔时间
     */
    public final static int EXIT_CLICK_INTERVAL_TIME = 2000;

    /**
     * 首页图片轮播间隔时间
     */
    public final static int AUTO_SCROLL_VIEW_PAGER_INTERVAL = 3000;

    /**
     * 默认标识Activity名称的Key
     */
    public final static String ACTIVITY_NAME_KEY = "ACTIVITY_NAME_KEY";

    /**
     * Activity间跳转的标识
     */
    public final static String ACTIVITY_JUMP_TAG = "to";

    /**
     * 每次请求的商品列表中商品数量
     */
    public final static int PAGE_SIZE = 20;

    /**
     * 首页中，列表滑动时底部菜单是否动画隐藏
     */
    public final static boolean isbottomMenuHiddenOnScroll = false;

    /**
     * 商品标题最大长度
     */
    public final static int ITEM_TITLE_MAX_LENGTH = 30;

    /**
     * 商品详情页中更多信息的标题
     */
    public static final String[] ITEM_DETAIL_MORE_INFO_TITLE = new String[]{"图文详情"};

    /**
     * 商品搜索结果页、二级分类页结果的分类方式,以及每个分类方式对应的数组中的索引值
     */
    public static final String[] ITEM_SEARCH_RESULT_SORT = new String[]{"最新", "销量", "价格"};
    public static final int ITEM_SEARCH_SORT_BY_NEW_INDEX = 0;
    public static final int ITEM_SEARCH_SORT_BY_SALES_INDEX = 1;
    public static final int ITEM_SEARCH_SORT_BY_PRICE_INDEX = 2;


    /**
     * 按价格 正向 排序商品结果
     */
    public static final int SORT_PRICE_ORDER_BY_ASC = 1;
    /**
     * 按价格 反向 排序商品结果
     */
    public static final int SORT_PRICE_ORDER_BY_DASC = -1;
    /**
     * 按上新 正向 排序商品结果
     */
    public static final int SORT_NEW_ORDER_BY_ASC = 2;
    /**
     * 按上新 反向 排序商品结果
     */
    public static final int SORT_NEW_ORDER_BY_DASC = -2;
    /**
     * 按销量 正向 排序商品结果
     */
    public static final int SORT_SALES_ORDER_BY_ASC = 3;
    /**
     * 按销量 反向 排序商品结果
     */
    public static final int SORT_SALES_ORDER_BY_DASC = -3;

    public static final int ERROR_CODE = -1;

    /**
     * 全局数字字体
     */
    public static final String NUMBER_FONT_NAME = "fonts/Roboto.ttf";

    public static final String ENCODING = "UTF-8";

    /**
     * 云推送AppId/APPKEY，加入你使用了TAE的云推送，请修改此参数
     */
    public static int CPUSH_APP_ID = 804;

    public static String CPUSH_APP_KEY = "BB649F03666460A492D97D3ACA5CD32A";

}
