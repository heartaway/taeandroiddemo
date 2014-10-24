# <center> TaeDemo工程代码说明</center>

工程采用客户端、服务端发布方式，客户端发送HTTP请求从服务端获取数据并展示数据。
## 一. 目录结构
```java
  |---res #资源文件
  |---assets #静态资源
          --- fonts #系统字体 
          --- theme #系统主题 
  |---**com.taobao.tae.bugyingdemo**
          --- activity 
          --- adapter #数据适配器
          --- app #Application
          --- constant #常量类和App配置类
          --- fragment 
          --- model #模型
          --- push #云推送
          --- util #工具类
          --- view #自定义View
          --- webview #自定义webview
          --- widget #组件
```

## 二.依赖库
* 通信框架使用谷歌开源框架Volley.jar；
* 服务端与客户端数据加密采用自定义的security.jar
* 客户端Crash错误收集&上报采用开源的ACRA，开发者可自定义扩展
* Tae开放Android SDK，用以实现淘宝授权登录、商品下单、交易、支付等环节（云推送也是用此SDK）

## 三. 依赖组件
使用的组件位于代码中的src/com/taobao/tae/buyingdemo/**widget**中，目前用到了四个开源的组件，部分做了一些自定义的扩展：

* AutoScrollPager 自动滚动的PageView，用户展示首页的图片轮播
* ButtonMenu 底部菜单，参考：<https://github.com/tuenti/ButtonMenu>
* ViewPagerIndicator 页面指示器，参考：<http://viewpagerindicator.com/>
* pinterest 类pinterest的瀑布流实现，参考：<https://github.com/GDG-Korea/PinterestLikeAdapterView>

## 四、页面分析
### 1. 首页
* 首页双列瀑布流采用[PinterestLike组件](https://github.com/GDG-Korea/PinterestLikeAdapterView)，并做了自定义改造，比如添加底部的分页加载、顶部的加载更多和图片轮播，首页自定义View类为`IndexDefaultFragemtView`。涉及的Activity类：IndexActivity，IndexActivity调用`IndexDefaultFragment`进行默认Pager展示。<br>
* 图片轮播采用AutoScrollPager组件和自定义View（IndexAutoScrollView），然后将图片轮播View作为瀑布流ListView的Header添加进去（参考IndexDefaultFragemtView类的initAutoScrollView方法）。
* 瀑布流商品展示列表中ListView对应的数据Adapter为`IndexItemListAdapter`，此类继承自`DynamicBaseAdapter`（实现了数据的动态绑定）。瀑布流中的item分为三种：时间戳、有标题价格的商品、只有图片的活动图片，所以会对应三种不同的样式。

### 2. 商品详情页
* 详情页涉及的主要类有：ItemDetailActivity、DetailPicWordFragment。ItemDetailActivity负责展示商品的基本信息、商品SKU选择浮层等，DetailPicWordFragment负责商品详情页中的图文详情的动态加载，此控件的展示采用了懒加载，当滑动到底部时才会自动加载更多的图片，以减少用户的流量开销。
* ItemDetailActivity采用了边加载边展示的方式，这里面比较负责的逻辑为商品SKU属性的展示，可以参考`initSkuProperties(View view)`方法。
* 理解淘宝商品SKU的数据模型：举例分析，假设一个鞋子有两个大的属性，颜色和尺码，那将颜色和尺码分别添加ID进行表示，比如为 100和200；颜色下有2个颜色，为白色（标识id为101）和红色（标识为102）；尺寸有三个尺码，为大（标识id为201）、中（标识ID为202）、小（标识ID为203）；那么用户选择了红色、大尺码的鞋子，那么组合的商品SKUID为：【100:102,200:201】,然后淘宝会提供一个Map，将这个组合映射为一个唯一的SKUID，比如【100:102,200:201】对应的在Map中的Value为100102200201（举例），那么最终用户选择的商品SKU的ID就为100102200201。

### 3. 类目页面
* 类目页面主要有主分类和子分类两部分组成。设计的类有：CategoryActivity、ParentCategoryAdapter、ChildCategoryFragment、ParentCategoryListView。
* 左侧的主分类的大小一定，右侧展示的子分类的图片大小是根据屏幕尺寸计算而来。
* 用户点击子分类则会进入搜索结果页。


## 五. 代码说明 
### 1. Application 类：BuyingDemoApplication
在Application中的 **onCreate()**方法中完成TaeSDK、云推送、Crash上报的初始化；
### 2. App中界面上的可配置属性
在Demo示例中，所有的页面的元素的可配置信息均抽取到了*res/values/app_theme.xml* 和
*res/values/app_strings.xml*  中；

* *res/values/app_theme.xml* 中定义了App页面元素的颜色、大小尺寸、间距等信息，如果用户想替换Demo的皮肤的话，只需要替换此目录下的此文件即可。（皮肤存放位置：*assets/theme/*）
*  *res/values/app_strings.xml*中定义了App中的一些字符常量。

### 3. Activity 相关主要类：
根据Demo的页面主要范围以下几个Activity类（位于Activity包中）：

* MainActivity 初始化和控制底部菜单的展示和跳转
* IndexActivity 用户展示首页内容
* ItemDetailActivity 展示Native的商品详情页
* SearchResultSortActivity 用于展示商品的搜索结果的页面
* CategoryActivity 用于展示类目页面，包含一级类目&二级类目
* ItemWebviewActivity 用于打开用户自定义的H5页面
* MyActivity 用于展示用户登录后的个人中心
* SettingActivity 用于App的基础设置，例如“退出”

#### （1）. MainActivity 类的扩展和说明
采用TabActivity+ButtonMenu实现底部菜单功能；每一个菜单需要在初始化方法中添加到**tabHost**中;

* 切换菜单：*addBottomMenuListener()* 方法是对每个菜单用户点击事件的响应，比如从首页切换到类目页面，切换页面只需要以下代码即可：\<br />
```java
if (!tabHost.getCurrentTabTag().equals(indexTabTag)) {
                    tabHost.setCurrentTabByTag(indexTabTag);
                    indexMenuSelected(); 
              }
```
代码逻辑范围三部分：首先判断用户点击的菜单是否为当前已经选中的菜单，如果不为当前页面则进行跳转，通过 *tabHost.setCurrentTabByTag* 进行切换，最后设置底部菜单处于选中状态和非选中状态的字体颜色和背景。
#### （2）. IndexActivity 类的说明
IndexActivity 继承自FragmentActivity，为方便首页的扩展，首页采用PageView来组装，目前只使用了PageView的第一个Page（IndexDefaultFragment），IndexDefaultFragment继承自Fragment实现了瀑布流需要的下拉刷新、上滑加载更多接口。

* IndexDefaultFragment  在初始化方法*onCreateView()* 完成顶部图片轮播和内容区商品双列表的页面初始化工作；
  主要流程范围三步：
  
  ① 第一步请求服务端数据；
  
  ② 第二部解析Json数据；
  
  ③ 第三部使用数据渲染页面元素；
  
* IndexDefaultFragment 中添加对商品或活动的点击监听事件，无论是图片轮播还是瀑布流，每次点击都有三中类型（OpenType打开方式）：

 ① 商品的方式（*OpenType.ITEM.getType()*），跳转到Native实现的商品详情页；
 
 ② 搜索的方式（*OpenType.SEARCH.getType()*），跳转到商品搜索结果页；
 
 ③ 活动的方式（*OpenType.H5.getType()*），使用WebView打开Url活动页面；
 
* IndexDefaultFragment 中的瀑布流：
  瀑布流中的事件分为两种：下拉刷新(ActionType.PULL_REFRESH_ACTION)、上滑加载更多(ActionType.VIEW_MORE_ITEMS_ACTION)；
  
  ①下拉刷新的实现：发送上次最后一次更新的商品时间到服务端，请求比此时间更新的发布的商品，如果存在则将商品添加的头部，否则不添加；
  备注：商品列表采用链表**LinkedList**实现；
  
  ②上滑加载更多的实现：采用分页的方式，每次加载一定个数的商品；
  

#### （3）. ItemDetailActivity 类的说明
商品详情页的展示，包括商品SKU属性的选择(如果存在)

 * 商品主图：
    商品主图的展示采用自定义ImagView：**AutoAdjustHeightImageView** ，位于view目录下，AutoAdjustHeightImageView能够实现图片宽度自适应屏幕、高度自动伸缩的功能，但是为了防止图片的高宽比过大，用户无法在第一屏幕内看到商品基本的属性，添加了图片最大高度的限制，即图片、商品标题价格、商品购买按钮三者高度之和的最大高度为屏幕高度；如果在初始化AutoAdjustHeightImageView时传入的属性*isDetailMainImage* 为 **true**时表示采用这一策略。
    
 * 商品图文信息：
 商品图文信息为根据服务端返回的数据自动填充内容，由于服务端返回的数据可能为文本、也可能为图片，所以会有类型的判断。其次，考虑到每次用户进入商品详情页加载的流量限制，在图文信息加载时使用了懒加载，自定义View,**LazyScrollView** ，LazyScrollView继承自ScrollView，当滑动到底部时，会调用*DetailPicWordFragment* 类的*loadMore()*方法来添加View。
 * 商品SKU属性：
 商品SKU采用PopupWindow实现；商品SKU属性的排列为自动测量剩余区域是否可布局新的SKU属性，如果不可以布局，则新建一行进行布局；当用户确定了SKU属性的选择后，则将商品的openItemId、skuId、商品数量等参数传递给**TaeSDK.showOrder**方法，唤起官方的订单确认页面，用户可从此入口完成订单确认和支付环节。
 
#### （4）. SearchResultSortActivity 类的说明
默认大的排序分为三种：新旧程度、价格高低、销量好坏；每一种排序方式都可以有正排和倒排两种形式；具体排序的标识与服务端约定值请参考类*AppConfig*中的配置；