![这里写图片描述](http://img.blog.csdn.net/20170802011310672?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvWEdMMTU2OTM0OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)![这里写图片描述](http://img.blog.csdn.net/20170802011414894?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvWEdMMTU2OTM0OA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
## 这是一个轻量级天气安卓APP ##
　　
目标功能：

 - [x] 主界面提供多城市的未来多天天气查询。 
 - [x] 多城市的逐小时天气预报。
    - 半成品。用了WebView来实现，但速度太捉急，缓存机制也差劲。
 - [x] 提供另一个快捷入口，使用Toast进行推送。
 - [ ] 若变天，推送通知。
 - [ ] Material Design设计


----------

## TODO LIST ##

 - Java爬虫获得逐小时数据的API，从而改善数据质量，也可以摆脱WebView。
 - 提升WebView的体验（上一条若完成，本条作废），包括不仅限于缓存、提升加载速度、提升JS注入效率。
 - UI重新设计（真·直男审美）

----
这是一个为了练手而生的项目，暂时均采用原生控件与框架。
