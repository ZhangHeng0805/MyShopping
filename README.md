# MyShopping
* 乐在购物Android应用[软件下载](https://github.com/ZhangHeng0805/MyShopping/releases/download/V5.5/MyShopping_V1-5.5.apk)
## 本项目的服务器[File-Management-Server](https://github.com/ZhangHeng0805/File-Management-Server)
* 注意：本项目中的资源文件```string.xml```因为涉及秘钥，所以我就没有上传
# 项目介绍
## 一、需求分析
随着网络的快速发展，网上购物商城以其方便、快捷的特点受到了更多用户的青睐。对比传统的商场销售，网上商城可以将商品详细分类，为用户提供了更多的选择空间;通过前台商品的展示，可以使顾客更好地了解商城的商品;并且自己也可以注册为商家，将自己的商品进行出售，网络购物车的实现使顾客真正实现了足不出户、网上购物的目的。
本项目对标淘宝的商品选购和美团外卖的快速选购，类似于咸鱼APP；主要特点是:用户既可以作为顾客，也可以作为商家；适用人群：需要出售闲置商品的大学生和淘购一些经济实惠的人群，类似于跳蚤市场。
|  竞品   |       主要特点        | 主要适用人群 |       项目特点       |
| :---: | :---------------: | :----: | :--------------: |
| 咸鱼App | 用户既可以作为顾客，也可以作为商家 |  大学生   | 以地点为核心，推荐好的商品给用户 |
  * 项目突出功能：
      1.位置共享：类似饿了吗、美团外卖直接获取地址功能 自动智能获取当前位置作为收货地址，还可以实时分享自己的详细位置，方便更好的送货上门，并且以后可以以自己位置为核心进行附近推荐。
        2.即时聊天：没有历史记录，保护聊天隐私，同时减少服务器的负载，还可以进行私聊，使聊天更安全，便捷。
        3.商家管理更方便，用户下单后，商家后台可以快捷直观的收到用户的订单，方便了商家处理订单的操作，同时也节省了时间。
        4.本项目的顾客账户安全更加可靠，因为顾客的账户密码均采用的我自制的加密算法后再保存于手机中，并且退出账户后直接清除手机中的账户所有信息，更加安全可靠。
  ## 二、概要设计
  本项目分为Android App，网页形式的前端和服务器后台；Android APP作为顾客使用的前端，网页形式作为商家使用的前端，而后台服务器使用的是SpringBoot的架构。
  ![商品系统](https://user-images.githubusercontent.com/74289276/123544918-2f0f7b80-d788-11eb-9e5a-133feca87a3a.png)
## 三、详细设计
### 1、系统用例图
![购物系统的用例图](https://user-images.githubusercontent.com/74289276/123545027-ac3af080-d788-11eb-9b82-978dfd4d99ef.png)
### 2、界面设计
#### （1）Android 界面设计
* 图1-1：启动页：启动页倒计时3秒钟，可以手动跳过，界面中心为App图标和标题，界面底部为本人信息。
* 图1-2：主页界面：用于购买商品的页面，在该页面可以分类查询商品，可以显示购物车中的商品，点击左上角的城市天气可以进入天气界面查看详细的天气情况（需要开启定位功能）。
* 图1-3：聊天界面：实时聊天大厅，在此页面可以一起沟通交流，因为是实时聊天，所以没有历史记录保护了隐私，并且进行私聊，使聊天更加安全，点击左上角的位置共享还能实时分享自己的位置信息。
* 图1-4：我的界面：点击账户头像可以修改账户信息，点击位置设置可以去设置收货地址，点击注册商家可以去浏览器注册商家，点击订单列表可以查看本账户的所有订单信息，点击应用版本可以检查应用更新，点击退出退出登录后即可退出账户的登录，退出后点击登录即可去登录。
  <div>
  <img src="https://user-images.githubusercontent.com/74289276/123545326-07b9ae00-d78a-11eb-966c-30a18774d570.jpg" width = "150" height = "300" alt="启动页" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123545337-0c7e6200-d78a-11eb-85cf-e2436f1a1332.jpg" width = "150" height = "300" alt="主页" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123545325-07211780-d78a-11eb-8744-8cb8a650b3c5.jpg" width = "150" height = "300" alt="聊天" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123545334-0ab49e80-d78a-11eb-9c51-73df85f17e97.jpg" width = "150" height = "300" alt="我的" align=center />
  </div>
---
* 图1-5：天气界面:：可以查询实时天气和近5天的天气情况
* 图1-6：登录界面:：当应用没有登录时，在我的界面点击登录即可进入登录界面，用于顾客的登录，在该界面如果没有账号，则可以点击去注册进行账号的注册。
* 图1-7：注册界面：用于APP顾客账号的注册。
* 图1-8：位置设置界面：设置收货地址，可以手动输入地址，也可以点击获取位置进行快捷输入，进行快捷输入后也可以修改地址，点击提交保存就设置完成了收货地址。
  <div>
  <img src="https://user-images.githubusercontent.com/74289276/123545329-09837180-d78a-11eb-9cf6-185d0980b24a.jpg" width = "150" height = "300" alt="天气查询页" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123545317-038d9080-d78a-11eb-8ce8-d17277f7be71.jpg" width = "150" height = "300" alt="登录页" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123545338-0d16f880-d78a-11eb-8dbe-e0ce8391de14.jpg" width = "150" height = "300" alt="注册页" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123545328-08eadb00-d78a-11eb-81d4-4b1431428601.jpg" width = "150" height = "300" alt="设置位置" align=center />
  </div>
---
图1-9：订单列表界:：展示自己的所有订单信息，点击每一个订单可以查看订单的详细商品。
图1-10：用户信息界面：用户登录后，在我的界面点击用户头像即可进入用户信息界面，用于展示用户的账户信息，可以修改用户头像、用户名、密码。
图1-11：位置共享界面：用于彼此同时展示用户的共享位置位置信息，点击黄色文字按钮可以刷新位置信息，点击右侧的按钮可以修改定位模式和地图样式，还可以缓存地图。
<div>
<img src="https://user-images.githubusercontent.com/74289276/123545318-05575400-d78a-11eb-955c-c858d3738bbc.jpg" width = "150" height = "300" alt="订单列表界" align=center />
<img src="https://user-images.githubusercontent.com/74289276/123545336-0be5cb80-d78a-11eb-889b-4ab801479c65.jpg" width = "150" height = "300" alt="用户信息界面" align=center />
<img src="https://user-images.githubusercontent.com/74289276/123545331-0a1c0800-d78a-11eb-85c4-2cd0fddd2dc8.jpg" width = "150" height = "300" alt="位置共享界面" align=center />
</div>
---
* 订单提交
* 订单提交完成
* 商品分类
* 选购商品
  <div>
  <img src="https://user-images.githubusercontent.com/74289276/123545319-05efea80-d78a-11eb-9490-39b5f67bff8d.jpg" width = "150" height = "300" alt="订单提交" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123545320-06888100-d78a-11eb-912c-fa1151aff1a4.jpg" width = "150" height = "300" alt="订单提交完成" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123545327-08524480-d78a-11eb-8b72-00f4504b2fe7.jpg" width = "150" height = "300" alt="商品分类" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123545335-0b4d3500-d78a-11eb-875a-922c719d985b.jpg" width = "150" height = "300" alt="选购商品" align=center />
  </div>
---
####（2）网页界面设计
* 图2-1：登录界面:用于商家登录的界面
* 图2-2：注册界面:用于注册商家地界面
<div>
  <img src="https://user-images.githubusercontent.com/74289276/123545319-05efea80-d78a-11eb-9490-39b5f67bff8d.jpg" width = "150" height = "300" alt="订单提交" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123545320-06888100-d78a-11eb-912c-fa1151aff1a4.jpg" width = "150" height = "300" alt="订单提交完成" align=center />
  </div>
---