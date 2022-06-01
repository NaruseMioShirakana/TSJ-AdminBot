package org.fujiwara.shirakana.adminbot.configAndData

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.contact.announcement.AnnouncementParametersBuilder

public object ShirakanaDataFlags : AutoSavePluginData("Shirakana_AdminBot_Flags_Data") {
    @ValueName("flagSmallCleanStart")
    @ValueDescription("小清洗状态")
    var flagSmallCleanStart: Boolean by value(false)

    @ValueName("flagSmallCleanTarget")
    @ValueDescription("小清洗目标群")
    var flagSmallCleanTarget: Long by value(0L)

    @ValueName("shirakanaAnnouncements")
    @ValueDescription("储存公告ID")
    var shirakanaAnnouncements: MutableMap<Long,String> by value()
}