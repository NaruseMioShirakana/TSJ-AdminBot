package org.fujiwara.shirakana.adminbot.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.SimpleCommand
import org.fujiwara.shirakana.adminbot.configAndData.*
import org.fujiwara.shirakana.adminbot.*



object ShirakanaSelectGroups : CompositeCommand(
    ShirakanaAdminBot, "SelectGroups",
    description = "在指定群聊中启用/关闭功能",
) {
    @SubCommand
    @Description("在指定群中启用")
    suspend fun CommandSender.add(vararg GroupTarget: String) {
        if (bot == null) {
            sendMessage("未指定机器人")
            return
        }
        var addedGroups = ""
        for(group_id in GroupTarget){
            if(group_id==""){
                continue
            }
            if(bot?.getGroup(group_id.toLong())!=null){
                if(ShirakanaDataGroupMember.enabledGroups.add(group_id.toLong())) {
                    addedGroups += group_id
                }
            }
        }
        if(addedGroups==""){
            sendMessage("指定的群都已被添加或添加失败")
        }else{
            sendMessage("群号:"+addedGroups+"添加完毕")
        }
        sendMessage("当前指定的群号："+ShirakanaDataGroupMember.enabledGroups)
    }
    @SubCommand
    @Description("在指定群中禁用")
    suspend fun CommandSender.del(vararg GroupTarget: String) {
        if (bot == null) {
            sendMessage("未指定机器人")
            return
        }
        var addedGroups = ""
        for(group_id in GroupTarget){
            if(group_id==""){
                continue
            }
            if(ShirakanaDataGroupMember.enabledGroups.remove(group_id.toLong())) {
                addedGroups += group_id
            }
        }
        if(addedGroups==""){
            sendMessage("指定的群都不在列表中或删除失败")
        }else{
            sendMessage("群号:"+addedGroups+"删除完毕")
        }
        sendMessage("当前指定的群号："+ShirakanaDataGroupMember.enabledGroups)
    }
    @SubCommand
    @Description("列出启用的群")
    suspend fun CommandSender.list() {
        if (bot == null) {
            sendMessage("未指定机器人")
            return
        }
        var addedGroups = ""
        for(group_id in ShirakanaDataGroupMember.enabledGroups){
            val thisGroup=bot?.getGroup(group_id.toLong())
            if(thisGroup!=null) {
                addedGroups += "群名称："+thisGroup.name+" 群号："+group_id+"\n"
            }
        }
        if(addedGroups==""){
            sendMessage("当前没有群启用功能")
        }else{
            sendMessage("以下群聊启用了功能:\n$addedGroups")
        }
    }
    @SubCommand
    @Description("不重复群员数量")
    suspend fun CommandSender.amount() {
        var tempI =0
        for(Temp_id in ShirakanaDataGroupMember.groupMembers){
            tempI++
        }
        sendMessage("所有功能启用群聊的总人数：$tempI")
    }
    @SubCommand
    @Description("重载不重复群员信息")
    suspend fun CommandSender.reload() {
        if (bot == null) {
            sendMessage("未指定机器人")
            return
        }
        ShirakanaDataGroupMember.groupMembers.clear()
        for(group_id in ShirakanaDataGroupMember.enabledGroups){
            val selectedGroup = bot?.getGroup(group_id.toLong())?.members
            if (selectedGroup != null) {
                for(member in selectedGroup){
                    ShirakanaDataGroupMember.groupMembers.add(member.id)
                }
            }
        }
        sendMessage("重载群成员完毕")
    }
}

object AddRemTarget : CompositeCommand(
    ShirakanaAdminBot, "CleanList",
    description = "添加/删除重复排查名单",
) {
    @SubCommand
    @Description("重复加群黑白名单操作")
    suspend fun CommandSender.repeat(mode : String ,vararg MembersTarget: String) {
        if (bot == null) {
            sendMessage("未指定机器人")
            return
        }
        if(mode=="add"){
            //var message="以下群友被加入禁止重复加群名单："
            var targets=""
            for(target in MembersTarget) {
                for (groupId in ShirakanaDataGroupMember.enabledGroups) {
                    val tmpGroup = bot?.getGroup(groupId) ?: continue
                    val memberTmp = tmpGroup.members[target.toLong()] ?: continue
                    if(ShirakanaDataGroupMember.repeatList.add(target.toLong())) {
                        targets += "昵称：" + memberTmp.nick + " QQ号：" + memberTmp.id + "\n"
                    }
                    break
                }
            }
            if (targets==""){
                sendMessage("列表中无新增成员")
            }else{
                sendMessage("以下群友被加入重复名单：\n$targets")
            }
        }
        if(mode=="del"){
                //var message="以下群友被加入禁止重复加群名单："
            var targets=""
            for(target in MembersTarget) {
                for (groupId in ShirakanaDataGroupMember.enabledGroups) {
                    val tmpGroup = bot?.getGroup(groupId) ?: continue
                    val memberTmp = tmpGroup.members[target.toLong()] ?: continue
                    if(ShirakanaDataGroupMember.repeatList.remove(target.toLong())) {
                        targets += "昵称：" + memberTmp.nick + " QQ号：" + memberTmp.id + "\n"
                    }
                    break
                }
            }
            if (targets==""){
                sendMessage("列表中无减少成员")
            }else{
                sendMessage("以下群友被移除出重复名单：\n$targets")
            }
        }
        if(mode=="list"){
            sendMessage(ShirakanaDataGroupMember.repeatList.toString())
        }
        else{
            sendMessage("<mode>处参数错误，正确的参数：add（添加）/del（删除）/list（查看列表）")
        }
    }
    @SubCommand
    @Description("大清洗保护名单操作")
    suspend fun CommandSender.bigclean(mode : String ,vararg MembersTarget: String) {
        if (bot == null) {
            sendMessage("未指定机器人")
            return
        }
        if(mode=="add"){
            //var message="以下群友被加入禁止重复加群名单："
            var targets=""
            for(target in MembersTarget) {
                for (groupId in ShirakanaDataGroupMember.enabledGroups) {
                    val tmpGroup = bot?.getGroup(groupId) ?: continue
                    val memberTmp = tmpGroup.members[target.toLong()] ?: continue
                    if(ShirakanaDataGroupMember.bigCleanWhiteList.add(target.toLong())) {
                        targets += "昵称：" + memberTmp.nick + " QQ号：" + memberTmp.id + "\n"
                    }
                    break
                }
            }
            if (targets==""){
                sendMessage("列表中无新增成员")
            }else{
                sendMessage("以下群友被加入白名单：\n$targets")
            }
        }
        if(mode=="del"){
            //var message="以下群友被加入禁止重复加群名单："
            var targets=""
            for(target in MembersTarget) {
                for (groupId in ShirakanaDataGroupMember.enabledGroups) {
                    val tmpGroup = bot?.getGroup(groupId) ?: continue
                    val memberTmp = tmpGroup.members[target.toLong()] ?: continue
                    if(ShirakanaDataGroupMember.bigCleanWhiteList.remove(target.toLong())) {
                        targets += "昵称：" + memberTmp.nick + " QQ号：" + memberTmp.id + "\n"
                    }
                    break
                }
            }
            if (targets==""){
                sendMessage("列表中无减少成员")
            }else{
                sendMessage("以下群友被移除出白名单：\n$targets")
            }
        }
        if(mode=="list"){
            sendMessage(ShirakanaDataGroupMember.repeatList.toString())
        }
        else{
            sendMessage("<mode>处参数错误，正确的参数：add（添加）/del（删除）/list（查看列表）")
        }
    }
}

