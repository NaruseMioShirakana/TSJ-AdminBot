# Torishirojima-admin-bot
---
## 插件介绍：
本插件实现的功能主要有：
* 1、指定启用的群聊。
* 2、识别所有启用的群聊中非重复成员并统计之。
* 3、用一个可以由拥有相关权限用户操作的“黑名单”来实现自动清理重复加群的用户（防止同一个人为了报复在多个群里实行炸群行动）。
* 4、大清洗，清洗名单（警告作用），名单加急（开始清洗），群内点名枪毙公决（踢出群）。
* 5、清理指定时间内未发言的群员（清理定时炸弹），这个过程排除处于保护列表或拥有专属头衔亦或是在群备注中注明了指定语句的群员。
* 6、(TODO)XX偏执度系统，创建一个XX偏执度变量，该变量逐日自增，也可由管理员控制，到达50开始小清洗，到达100进行大清洗并清零，XX偏执度会通过图片的方式发送，如同下图：
![d455e54e-cb9f-48af-8884-4b5a784c5cdb-image.png](/assets/uploads/files/1653292473519-d455e54e-cb9f-48af-8884-4b5a784c5cdb-image.png) 
* 7、(TODO)根据群友以及社区的建议写的其他功能。
* 8、~~彩蛋功能~~。

---
## 插件指令及其作用

* ([]括起来的代表可选参数，<>括起来的代表必填参数)：


#### org.fujiwara.shirakana.adminbot.plugin:command.selectgroups
    /SelectGroups <add/del> <群号>：添加/删除功能启用的群聊
    /SelectGroups list：查看启用功能的群
    /SelectGroups reload：重载非重复群员统计
    /SelectGroups amount：查看非重复群员个数
#### org.fujiwara.shirakana.adminbot.plugin:command.cleanlist
    /CleanList bigclean <add/del/list> <GroupId> [GroupMembersTarget]
    #大清洗保护名单操作，将群聊GroupId的指定群员GroupMembersTarget添加/删除入功能5的例外名单（保护名单）
    /CleanList repeat <add/del/list> <GroupId> [GroupMembersTarget]    
    #禁止重复加群用户名单操作，将群聊GroupId的指定群员GroupMembersTarget添加/删除入禁止重复加群的名单
    #（在此名单内的用户无法添加一个以上的插件启用群聊）
    /CleanList smallclean <add/del/list> <GroupId> [GroupMembersTarget]
    #小清洗列表操作，将群聊GroupId的指定群员GroupMembersTarget添加/删除入清洗名单
    #注：GroupMembersTarget为QQ号，可指定多个QQ，中间用空格隔开
    /CleanList help
    #查看插件的总帮助
#### org.fujiwara.shirakana.adminbot.plugin:command.tutu
~~全都给图图乐~~

    /Tutu SmallClean <群号>：
    #在指定群开启小清洗（清洗目标为CleanList smallclean指定的人，执行后可以从中选择
    #任意人数进行清洗
    /Tutu BigClean：
    #开启大清洗（会跳过CleanList bigclean指定的人、有群专属头衔的人、群备注包含
    #ShirakanaBigCleanSetting.nameStandard的人）默认目标为2个月不发言的群员
    /Tutu List：
    #查看被清洗者名单
    /Tutu Add <QQ>：
    #将某人加入已被清洗名单
    /Tutu Del <QQ>：
    #将某人删除出已被清洗名单
    /Tutu kick：
    #在所有的群踢出“已被清洗”名单中的群员
    /Tutu help：
    #查看帮助
    #注：使用小清洗指令踢出的群员会被加入“已被清洗”名单（相当于本群黑名单）
    #此名单中用户无法加入任何启用该插件的群（可以找管理协商解决问题）
#### org.fujiwara.shirakana.adminbot.plugin:command.Paranoid

    /Paranoid add <Amount>：添加指定数量的偏执度
    /Paranoid decrease <Amount>：减少指定数量的偏执度
    /Paranoid amount：查看当前偏执度
    注意：偏执度会于每天的20:30 +2 ，并在启用群的群公告发送。
    
效果图：![image](https://user-images.githubusercontent.com/40709280/171452455-40132406-a100-473c-9212-cb570ff0ea46.png)

#### org.fujiwara.shirakana.adminbot.plugin:command.cleanrepeat

    /CleanRepeat <GroupTarget>：若禁止重复加群名单中的用户已添加指定群聊（GroupTarget）
    则将其从插件启用的其他群聊中踢出
#### ~~数字恶臭化工具（彩蛋）~~
    /To114514 <input>
    #将input（Long）转换为一个114514序列组成的表达式
##### 实现方法:[我在B站的文章](https://www.bilibili.com/read/cv16725546)
---
## 配置文件及DATA
#### \config\org.fujiwara.shirakana.adminbot.plugin\Shirakana_AdminBot_Setting
```
# 大清洗开关（偏执度达到100大清洗）
big_clean_switch: true //偏执度系统功能（未实现）
# 小清洗开关（偏执度为50时小清洗）
small_clean_switch: true //偏执度系统功能（未实现）
# 重复加群自动拒绝及清理开关
repeat_join_clean: true //偏执度系统功能（未实现）
# 重复加群白名单/黑名单（True为黑名单模式，False为白名单模式）
repeat_join_clean_mode: true //重复加群清理的模式，黑名单模式会清理名单中重复加群的人，白名单模式则会清理不在名单中的人
# 命名标准（群备注包含此字符串的不会被大清洗）
Standard_Name: （无）//大清洗时，如果群员的群备注中有此字符串，则不会被清理
# 大清洗时间，最后一次发言时间在这个数值（天）之前的群友会被清洗
Standard_Time: '' //大清洗目标最后一次发言的时间（默认为60天，目前无法改变）
```
#### \data\org.fujiwara.shirakana.adminbot.plugin\Shirakana_AdminBot_GroupMember_Data.yml
```
# 统计群员
selected_group_members: [] //该列表存储非重复群员的QQ号
# 统计群员（重复加群重点清理目标）
group_members_target: [] //当重复加群设置为黑名单时，清理该列表中的群员，为白名单时，清理该名单之外的群员
# 统计群员（管理指定的小清洗目标）
small_clean_target: [] //该列表为小清洗指定的群员
# 统计群员（管理指定的大清洗目标）
big_clean_target: [] //该列表为大清洗的保护名单（在此名单中的人不会被大清洗）
# 统计开启功能的群聊
selected_groups: []
# 偏执度（管理员San值）
big_clean_paranoia: 0 //管理偏执度
# 黑名单
black_list: [] //黑名单（即上文提到的“被清洗状态”名单）
```
#### Flag文件建议不要动，也不需要动

#### \data\org.fujiwara.shirakana.adminbot.plugin
```
请将123改为你的管理员内部群
```

---
清洗发动时的效果图：

![![4a947612-1a67-4088-90ec-cafc540fcf8f-R9_]I2G81QW{_1V6JH{_~53.png](/assets/uploads/files/1653468707954-4a947612-1a67-4088-90ec-cafc540fcf8f-r9_-i2g81qw-_1v6jh-_-53.png) 

---
第一次学着写mirai插件，还是用一个不太熟悉的语言（），~~希望社区的各位狠狠的批评~~，来促使我改进。
Mirai框架的许多内容还有我未了解的地方，许多类和函数的使用方法也不是太了解，所以插件可能写的不怎么好，希望各位能够指导我改进，~~以后可能会搞一大堆奇奇怪怪功能的插件~~。
至于为什么某些指令需要指定群号，这样做的目的主要是为了能够在一个外部群或者是机器人私聊时操作（）
5月25日更新：修复了许多致命的BUG

---
Language : Kotlin等。
Tag: 群管，其他功能。
