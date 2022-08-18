package org.fujiwara.shirakana.adminbot.configAndData


import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

public object ShirakanaDataGroupMember : AutoSavePluginData("Shirakana_AdminBot_GroupMember_Data") {
    @ValueName("GroupMembers")
    @ValueDescription("统计群员")
    val groupMembers: MutableSet<Long> by value()

    @ValueName("group_members_target")
    @ValueDescription("重复加群白名单/黑名单")
    val repeatList: MutableSet<Long> by value()

    @ValueName("KickTarget")
    @ValueDescription("统计群员（清洗目标）")
    val kickTarget: MutableSet<Long> by value()

    @ValueName("BigCleanWhiteList")
    @ValueDescription("大清洗的白名单")
    val bigCleanWhiteList: MutableSet<Long> by value()

    @ValueName("EnabledGroups")
    @ValueDescription("开启功能的群聊")
    val enabledGroups: MutableSet<Long> by value()

    @ValueName("BigCleanParanoia")
    @ValueDescription("偏执度（管理员San值）")
    var bigCleanParanoia: Long by value(0L)

    @ValueName("BlackList")
    @ValueDescription("黑名单")
    val blackList: MutableSet<Long> by value()
}