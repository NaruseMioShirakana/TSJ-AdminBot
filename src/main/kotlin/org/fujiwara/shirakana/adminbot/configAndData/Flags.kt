package org.fujiwara.shirakana.adminbot.configAndData

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.contact.announcement.AnnouncementParametersBuilder

public object ShirakanaDataFlags : AutoSavePluginData("Shirakana_AdminBot_Flags_Data") {
    @ValueName("SmallCleanStartFlag")
    @ValueDescription("小清洗状态")
    var smallCleanStartFlag: Boolean by value(false)

    @ValueName("CleanTarget")
    @ValueDescription("小清洗目标群")
    var cleanTarget: Long by value(0L)

    @ValueName("AnnouncementsList")
    @ValueDescription("储存公告ID")
    var announcementsList: MutableMap<Long,String> by value()

    @ValueName("MemberJoinRequestID")
    @ValueDescription("储存请求ID")
    var memberJoinRequestID = mutableSetOf<Long>()
}