# MyShopping
* 乐在购物Android应用[软件下载](https://github.com/ZhangHeng0805/happy_shopping/releases/download/V3.01/_V2-3.01.apk)
## 本项目的服务器[happy_shopping](https://github.com/ZhangHeng0805/happy_shopping)
* 注意：本项目中的资源文件```string.xml```因为涉及秘钥，所以我就没有上传
# 项目介绍
## 一、需求分析
随着网络的快速发展，网上购物商城以其方便、快捷的特点受到了更多用户的青睐。对比传统的商场销售，网上商城可以将商品详细分类，为用户提供了更多的选择空间;通过前台商品的展示，可以使顾客更好地了解商城的商品;并且自己也可以注册为商家，将自己的商品进行出售，网络购物车的实现使顾客真正实现了足不出户、网上购物的目的。
本项目对标淘宝的商品选购和美团外卖的快速选购，类似于咸鱼APP；主要特点是:用户既可以作为顾客，也可以作为商家；适用人群：需要出售闲置商品的大学生和淘购一些经济实惠的人群，类似于跳蚤市场。
|  竞品   |       主要特点        | 主要适用人群 |       项目特点       |
| :---: | :---------------: | :----: | :--------------: |
| 咸鱼App | 用户既可以作为顾客，也可以作为商家 |  大学生   | 以地点为核心，推荐好的商品给用户 |
  * 项目突出功能：
1. 位置共享：类似饿了吗、美团外卖直接获取地址功能 自动智能获取当前位置作为收货地址，还可以实时分享自己的详细位置，方便更好的送货上门，并且以后可以以自己位置为核心进行附近推荐。
2. 即时聊天：没有历史记录，保护聊天隐私，同时减少服务器的负载，还可以进行私聊，使聊天更安全，便捷。
3. 商家管理更方便，用户下单后，商家后台可以快捷直观的收到用户的订单，方便了商家处理订单的操作，同时也节省了时间。
4. 本项目的顾客账户安全更加可靠，因为顾客的账户密码均采用的我自制的加密算法后再保存于手机中，并且退出账户后直接清除手机中的账户所有信息，更加安全可靠。
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
* 图1-9：订单列表界:：展示自己的所有订单信息，点击每一个订单可以查看订单的详细商品。
* 图1-10：用户信息界面：用户登录后，在我的界面点击用户头像即可进入用户信息界面，用于展示用户的账户信息，可以修改用户头像、用户名、密码。
* 图1-11：位置共享界面：用于彼此同时展示用户的共享位置位置信息，点击黄色文字按钮可以刷新位置信息，点击右侧的按钮可以修改定位模式和地图样式，还可以缓存地图。
<div>
<img src="https://user-images.githubusercontent.com/74289276/123545318-05575400-d78a-11eb-955c-c858d3738bbc.jpg" width = "150" height = "300" alt="订单列表界" align=center />
<img src="https://user-images.githubusercontent.com/74289276/123545336-0be5cb80-d78a-11eb-889b-4ab801479c65.jpg" width = "150" height = "300" alt="用户信息界面" align=center />
<img src="https://user-images.githubusercontent.com/74289276/123545331-0a1c0800-d78a-11eb-85c4-2cd0fddd2dc8.jpg" width = "150" height = "300" alt="位置共享界面" align=center />
</div>

******
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

#### （2）网页界面设计
* 图2-1：登录界面:用于商家登录的界面
* 图2-2：注册界面:用于注册商家地界面
  <div>
  <img src="https://user-images.githubusercontent.com/74289276/123547270-b235cf00-d792-11eb-889d-2f8c6f7783c4.png" width = "350" height = "200" alt="登录界面" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123547280-b5c95600-d792-11eb-9f86-4e1445a5d179.png" width = "200" height = "200" alt="注册界面" align=center />
  </div>
---
* 图2-3：用户主页界面:用于展示商户店铺的盈利情况以及一些未处理的订单
* 图2-4：订单列表界面:展示商铺的所有的订单信息
 <div>
  <img src="https://user-images.githubusercontent.com/74289276/123547279-b530bf80-d792-11eb-8345-67e9c96631cb.png" width = "350" height = "200" alt="用户主页界面" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123547271-b366fc00-d792-11eb-9325-befad9c13ed4.png" width = "350" height = "200" alt="订单列表界面" align=center />
  </div>

---

* 图2-5：添加商品界面：可以为自己的店铺添加新的商品
* 图2-6：商品信息界面：展示商家店铺的所有商品信息，可以修改商品和删除商品
* 图2-7：修改商品信息界面：通过商品信息界面点击对应商品的修改按钮，即可进入该商品的修改界面，可以更改商品信息。
 <div>
  <img src="https://user-images.githubusercontent.com/74289276/123547278-b4982900-d792-11eb-9538-23d90fa8ead1.png" width = "300" height = "150" alt="添加商品界面" align=center />
  <img src="https://user-images.githubusercontent.com/74289276/123547274-b3ff9280-d792-11eb-86dc-181196d7d041.png" width = "300" height = "150" alt="商品信息界面" align=center />
 <img src="https://user-images.githubusercontent.com/74289276/123547269-b06c0b80-d792-11eb-8d16-40e2c13a7d4a.png" width = "300" height = "150" alt="修改商品信息界面" align=center />
  </div>

---
  
### 3、数据库设计
![数据库设计](https://user-images.githubusercontent.com/74289276/123547938-5b7dc480-d795-11eb-979e-f21d9caf5eb0.png)
```
1）customer表：保存顾客注册时的信息
2）merchants表：保存商家注册时的信息
3）stores表：保存店铺信息
4）goods表：保存商品信息
5）submit_goodslist表：保存顾客提交的订单信息
6）goods_list表：保存订单里面的商品信息
7）location表：保存共享位置信息
8）chatconfig表：保存聊天室的IP地址和端口号
9）phone_message表：保存用户手机信息
10）delete_image表：保存将要删除的图片信息
```
* 使用优化：
1.	数据库事务：多条SQL作为一个整体提交给数据库系统，要么全部执行，要么全部取消，是一个不可分割的逻辑单元；两个SQL语句，一个下单成功，另外一个减少库存，事务保证一起成功或者一起失败。
2.	锁：锁就是保证访问同一资源时，有个先后顺序管理，处理并发问题。
3.	字段可空：数值类型的，不要可空，in查询  not in 查询，会没有结果，一般给个默认值；尽量非空，看情况（时间不适合默认值）
4.	读写分离：二八原则：数据库中80%操作都是读，只有20%是写。实现原理：就是把读和写的压力分开，降低IO压力；一主多从，主库写从库读；数据同步的问题，从主库到从库（肯定是有延迟的，网速没问题，两三秒吧）

### 4、关键技术
#### 1）自制的加密和解密算法：
该算法系统的项目在我的GitHub上：[PassWordSystem](https://github.com/ZhangHeng0805/PassWordSystem)。该密码算法系统目前总共有三种模式：（摩斯密码，zh自制和随机秘钥）。
1.	摩斯密码：只能加密英文字符和数字，使用国际通用摩斯密码加密，自己添加了一些新字符的摩斯码；
2.	zh自制密码：可以加密任意字符，核心是通过字符的ASCII码进行加密计算，加密过后的密文就是该字符的加密过后的ASCII码值，目前一共有三种加密算法，只有加密和解密的算法模式选择一致才能解密出正确的明文。
3.	随机秘钥密码：就是在zh自制密码的基础上加上了由随机数字组成的随机秘钥，通过字符ASCII码值和随机秘钥的数字进行加密计算等到密文的ASCII码，再讲密文的ASCII码转译为密文字符，只有密文字符和秘钥经过我的解密算法才能得到正确的密文。
项目使用：本项目系统中的Android项目中的用户密码的保存使用了该加密算法的第二种加密模式(zh自制)，第二种模式(zh自制)主要是对字符的ASCII码进行操作计算，目前zh自制加密有3种计算方法(加密和对应的解密模式)，只有加密和解密的模式一致才能解出密码。
#### 2）Android网络请求框架：[OkHttp](https://github.com/square/okhttp)和[OkHttpUtils](https://github.com/hongyangAndroid/okhttputils)
1. OkHttp：一个处理网络请求的开源项目,是安卓端最火热的轻量级框架,由移动支付Square公司贡献，用于替代HttpUrlConnection和Apache HttpClient
* 功能：
* （1）get,post请求
* （2）文件的上传下载
* （3）加载图片(内部会图片大小自动压缩) 
* （4）支持请求回调，直接返回对象、对象集合 
* （5）支持session的保持
2. OkHttpUtils：对okhttp的封装类，方便简化了okhttp的使用
项目使用：在Android 项目中主要用于APP端和服务器端进行数据的交互
#### 3）json数据解析：[Gson](https://github.com/google/gson)
GSON：是Google提供的用来在Java对象和JSON数据之间进行映射的Java类库。可以将一个Json字符转成一个Java对象，或者将一个Java转化为Json字符串。
* 特点：
* （1）	快速、高效
* （2）	代码量少、简洁
* （3）	面向对象
* （4）	数据传递和解析方便
* 项目使用：在本项目中所有数据交互均采用JSON格式，所以只要有需要解析JSON数据的地方就有使用Gson
#### 4）Android图片加载框架：[Glide](https://github.com/bumptech/glide)
Glide：是谷歌推荐的一个图片加载库，代码简洁，可读性很好，加载的图片种类多。
* 项目使用：Android 项目中的所有网络图片加载均采用Gilde
#### 5）地图组件：[高德地图API](https://developer.amap.com/)
高德地图API：包含地图显示、室内外一体化地图查看、兴趣点搜索、地理编码、离线地图等功能。
* 项目使用：在Android项目中的位置共享和位置的获取以及地图的展示均采用高德地图API
#### 6）天气查询API：[聚合数据](https://www.juhe.cn/docs/api/id/73)
聚合数据：第三方数据提供者，提供数据查询服务。
* 项目使用：在Android项目中的天气查询使用了聚合数据的天气查询API
#### 7）聊天网络框架：[Netty](https://github.com/netty/netty)
Netty：是由JBOSS提供的一个java开源框架，现为 Github上的独立项目。Netty提供异步的、事件驱动的网络应用程序框架和工具，用以快速开发高性能、高可靠性的网络服务器和客户端程序。Netty 是一个基于NIO的客户、服务器端的编程框架，使用Netty 可以确保你快速和简单的开发出一个网络应用，例如实现了某种协议的客户、服务端应用。Netty相当于简化和流线化了网络应用的编程开发过程，例如：基于TCP和UDP的socket服务开发。
* 项目使用：在Android 项目中的聊天客户端和服务器的聊天服务端使用了Netty
## 四、测试报告
* 功能测试：确保测试对象的功能正常，其中包括业务流程、数据处理、边界值等功能。
* 用户界面 (UI) 测试：核实用户与软件之间的交互，确保用户界面会通过测试对象的功能来为用户提供相应的访问或浏览功能，确保 UI 中的对象按照预期的方式运行，确保各个窗口风格（包括颜色、字体、提示信息、图标、等等）都与需求保持一致，或符合可接受标准，能够保证用户界面的友好性、易操作性，而且符合用户操作习惯
* 流程测试：核实实际业务流程在系统中的完整正确实现。应确保各业务流程内部数据流转及流程之间接口数据的正确，确保角色权限对流程的操作的限制的正确性
* 安全性测试：确保用户、管理员的密码管理安全、应用程序级别与系统级别的安全的安全性
* 兼容性测试：确保系统在各种不同版本不同类项浏览器下均能正常实现其功能
## 五、安装及使用
### 1、开发、运行环境
#### 1）开发环境
```
服务器开发工具：IntelliJ IDEA 2019.1.1
Android开发工具：Android Studio 3.5.2
系统java环境：1.8.0_181
SpringBoot版本：2.3.1.RELEASE，
Maven版本：3.3.9
数据库及版本：MySql 5.5.35
Android Studio的gradle版本:3.5.2
```
#### 2）运行环境
```
运行java环境：java版本1.8及以上，
数据库及版本：MySql 5.5.35
需要将数据库的表创建完成后才能成功运行服务器，详细的建表SQL文件（jpa.sql）在服务器项目介绍里面
乐在购物App安装环境：SDk：24，手机Android版本7.0及以上
```
### 2、安装运行
#### 1）服务器运行：
因为服务器使用的是SpringBoot的架构，所以完成后的项目直接打包成一个可执行的jar包，直接通过java -jar的命令（前提是配置了java1.8及以上的环境）运行项目jar包，即可启动服务器。
* 核心服务器端口：8081
* 聊天服务器端口：8889
> 本服务器项目我已经上传至GitHub上，更多详细请前往https://github.com/ZhangHeng0805/File-Management-Server
#### 2）Android项目
> 项目我已经上传至GitHub上，请前往查看https://github.com/ZhangHeng0805/MyShopping
## 六、项目总结
本次项目让我体会到了一个项目开发的所有流程，同时也提高了我的Android 开发，网页前端和服务器后端的开发能力，主要是让我学会了很多框架，方便了开发过程。
* 在此过程中我学会了使用：
1.	GSON解析JSON数据
2.	MySQL数据库的使用和数据库的设计
3.	使用第三方API数据接口丰富项目的功能，
4.	学会Android中的图片加载技术Glide，
5.	使用高德地图组件完成了位置共享和位置设置功能，
6.	使用Android中的网络加载技术OkHttp完成了和服务器的数据交互，
7.	使用Netty框架完成了聊天功能，
8.	还学会了使用SpringBoot框架搭建服务器，
9.	以及使用thymeleaf页面模板引擎，
10.	使用BootStrap页面模板美化界面和简化页面开发。
> 目前项目已经完成了顾客选购商品下订单和订单查询的主要功能，商家添加商品和订单处理功能，以后还需要添加搜索商品，商家店铺和商品审核等功能，进一步完善项目。
* **未来版本的规划**：
1.	推出附近推荐功能，发现附近的商家，给一定的商家进行推广
2.	根据用户位置，购物习惯进行大数据分析，实现精准推荐
3.	推出同城交易，可以线下进行面谈。
