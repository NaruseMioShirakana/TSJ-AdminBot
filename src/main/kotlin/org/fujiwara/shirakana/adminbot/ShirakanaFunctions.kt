package org.fujiwara.shirakana.adminbot

import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.announcement.AnnouncementParameters
import net.mamoe.mirai.contact.announcement.AnnouncementParametersBuilder
import net.mamoe.mirai.contact.announcement.OfflineAnnouncement
import net.mamoe.mirai.contact.announcement.buildAnnouncementParameters
import net.mamoe.mirai.contact.isAdministrator
import net.mamoe.mirai.contact.isOwner
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.info
import org.fujiwara.shirakana.adminbot.command.ShirakanaParanoia
import org.fujiwara.shirakana.adminbot.configAndData.ShirakanaAdministratorList
import org.fujiwara.shirakana.adminbot.configAndData.ShirakanaBigCleanSetting
import org.fujiwara.shirakana.adminbot.configAndData.ShirakanaDataFlags
import org.fujiwara.shirakana.adminbot.configAndData.ShirakanaDataGroupMember
import java.io.File
import java.net.URL
import java.util.*
import javax.imageio.ImageIO


public object ShirakanaEventListener : SimpleListenerHost() {
    @EventHandler
    internal suspend fun BotOnlineEvent.handle2() {
        val t1 = Timer()
        val startCalendar = Calendar.getInstance()
        startCalendar[Calendar.HOUR_OF_DAY] = 20 // 控制时
        startCalendar[Calendar.MINUTE] = 40 // 控制分
        startCalendar[Calendar.SECOND] = 0 // 控制秒
        val startTime = startCalendar.time
        t1.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                launch{
                    ShirakanaDataGroupMember.bigCleanParanoia += 2
                    for(GroupID in ShirakanaDataGroupMember.selectedGroups){
                        val tmpGroup = bot.getGroup(GroupID.toLong())
                        if(tmpGroup?.botAsMember!!.isAdministrator()||tmpGroup.botAsMember.isOwner()){
                            if(ShirakanaDataFlags.shirakanaAnnouncements.contains(GroupID.toLong())){
                                ShirakanaDataFlags.shirakanaAnnouncements[GroupID.toLong()]?.let {
                                    tmpGroup.announcements.delete(
                                        it
                                    )
                                }
                            }
                            val imageAnnouncementTmp = tmpGroup.announcements.uploadImage(ShirakanaParanoia.paranoiaImage(tmpGroup))
                            val parameterTmp = buildAnnouncementParameters {
                                image = imageAnnouncementTmp
                                sendToNewMember = false
                                isPinned = false
                                showEditCard = false
                                showPopup = true
                                requireConfirmation = false
                            }
                            val announcementTemp = OfflineAnnouncement.create("管理偏执度", parameterTmp)
                            if(!ShirakanaDataFlags.shirakanaAnnouncements.contains(GroupID.toLong())){
                                ShirakanaDataFlags.shirakanaAnnouncements.put(GroupID.toLong(), announcementTemp.publishTo(tmpGroup).fid)
                            }else{
                                ShirakanaDataFlags.shirakanaAnnouncements[GroupID.toLong()] = announcementTemp.publishTo(tmpGroup).fid
                            }
                        }
                    }
                    if(ShirakanaDataGroupMember.bigCleanParanoia == 50L){
                        bot.getGroup(ShirakanaAdministratorList.adminGroup)?.sendMessage("偏执度已达到50，可以发动点名清洗（/Tutu SmallClean <群号>）")
                    }
                    if(ShirakanaDataGroupMember.bigCleanParanoia == 100L){
                        bot.getGroup(ShirakanaAdministratorList.adminGroup)?.sendMessage("偏执度已达到100，可以发动大清洗（/Tutu BigClean）")
                    }
                }
            }
        }, startTime, 1000*24*60*60)
        println("定时任务：偏执度——启动完毕")
    }
    @EventHandler
    internal suspend fun BotOnlineEvent.handle1() {
        val newDire = File("shirakana")
        if(!newDire.exists()){
            newDire.mkdir()
        }
        val newDire2 = File("data/org.fujiwara.shirakana.adminbot.plugin/cleaned")
        if(!newDire2.exists()){
            newDire2.mkdir()
        }
        val t2 = Timer()
        val startCalendar = Calendar.getInstance()
        startCalendar[Calendar.HOUR_OF_DAY] = 0 // 控制时
        startCalendar[Calendar.MINUTE] = 0 // 控制分
        startCalendar[Calendar.SECOND] = 0 // 控制秒
        val startTime = startCalendar.time
        t2.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val dire = File("shirakana")
                if (dire.exists()){
                    for(file in dire.listFiles()){
                        file.delete()
                    }
                }
            }
        }, startTime, 1000*30*60)
        println("定时任务：缓存清理——启动完毕")
    }
    @EventHandler
    internal suspend fun MemberJoinRequestEvent.handle() {
        if(ShirakanaBigCleanSetting.repeat_join_clean){
            if(ShirakanaDataGroupMember.selectedGroups.contains(groupId.toString())){
                if(ShirakanaDataGroupMember.selectedGroupMembers.contains(fromId.toString())){
                    if(ShirakanaDataGroupMember.groupMembersTarget.contains(fromId.toString())){
                        reject(false,"你已经加入了另一个群，且不处于跨群白名单内，如有异议可找管理协商。")
                        return
                    }
                }
            }
        }
        if(ShirakanaDataGroupMember.ShirakanaBlackListGroup.contains(fromId.toString())){
            reject(false,"你已被清洗，无法再次加入群聊，如有异议可找管理协商。")
        }
    }
    @EventHandler
    internal suspend fun MemberJoinEvent.handle() {
        if(ShirakanaDataGroupMember.selectedGroups.contains(groupId.toString())) {
            ShirakanaDataGroupMember.selectedGroupMembers.add(member.id.toString())
        }
    }
    @EventHandler
    internal suspend fun GroupMessageEvent.handle() {
        if(ShirakanaDataFlags.flagSmallCleanStart) {
            if(group.id==ShirakanaDataFlags.flagSmallCleanTarget&&sender.isAdministrator()){
                if(ShirakanaDataGroupMember.smallCleanTarget.isEmpty()){
                    group.sendMessage("列表中无成员,清洗自动结束")
                    ShirakanaDataFlags.flagSmallCleanStart = false
                    ShirakanaDataFlags.flagSmallCleanTarget = 0L
                    return
                }
                if(message.contentToString() == "结束清洗"){
                    ShirakanaDataFlags.flagSmallCleanStart = false
                    ShirakanaDataFlags.flagSmallCleanTarget = 0L
                    group.sendMessage("清洗已经结束")
                    return
                }
                for(memberId in ShirakanaDataGroupMember.smallCleanTarget){
                    if(message.contentToString()== At(memberId.toLong()).contentToString()){
                        val member = group.get(memberId.toLong())
                        if(member!=null){
                            if(member.isAdministrator()||member.isOwner()){
                                group.sendMessage("你无权清洗管理员")
                                continue
                            }
                            val msgChain = buildMessageChain {
                                +PlainText("群友："+member.nick+"已被清洗\n")
                                +ShirakanaParanoia.GetImageTutu(member,group)
                                +PlainText("清洗结束后，记得输入“结束清洗”来结束大清洗")
                            }
                            val newImg = ImageIO.read(URL(member.avatarUrl))
                            val thisFileName = "data/org.fujiwara.shirakana.adminbot.plugin/cleaned/"+member.id.toString()+".png"
                            val outPutImage = File(thisFileName)
                            ImageIO.write(newImg, "png", outPutImage)
                            for(groupID in ShirakanaDataGroupMember.selectedGroups){
                                bot.getGroup(groupID.toLong())?.sendMessage(msgChain)
                                bot.getGroup(groupID.toLong())?.get(memberId.toLong())?.kick("你已被清洗")
                            }
                            ShirakanaDataGroupMember.ShirakanaBlackListGroup.add(memberId)
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    internal suspend fun MemberLeaveEvent.handle() {
        if(ShirakanaDataGroupMember.selectedGroups.contains(groupId.toString())) {
            for(groupId in ShirakanaDataGroupMember.selectedGroups){
                val groupThis = bot.getGroup(groupId.toLong())
                if(groupThis!=null){
                    for(memberThis in groupThis.members){
                        if(member.id==memberThis.id){
                            return
                        }
                    }
                }
            }
            ShirakanaDataGroupMember.selectedGroupMembers.remove(member.id.toString())
            ShirakanaDataGroupMember.bigCleanTarget.remove(member.id.toString())
            ShirakanaDataGroupMember.smallCleanTarget.remove(member.id.toString())
        }
    }
    @EventHandler
    internal suspend fun BotOnlineEvent.handle(){
        ShirakanaDataGroupMember.selectedGroupMembers.clear()
        for(group_id in ShirakanaDataGroupMember.selectedGroups){
            val selectedGroup = bot.getGroup(group_id.toLong())?.members
            if (selectedGroup != null) {
                for(member in selectedGroup){
                    ShirakanaDataGroupMember.selectedGroupMembers.add(member.id.toString())
                }
            }
        }
        ShirakanaAdminBot.logger.info { "群员信息重载完毕" }
    }
    /*@EventHandler
    internal suspend fun GroupMessageEvent.handle1() {
        if(ShirakanaBigCleanSetting.big_clean_switch&&ShirakanaDataGroupMember.bigCleanParanoia>=100L){
            ShirakanaDataGroupMember.bigCleanParanoia = 0
            val tmpThisGroup = bot.getGroup(ShirakanaAdministratorList.adminGroup)
            tmpThisGroup.sendMessage()
        }
        if(ShirakanaBigCleanSetting.small_clean_switch&&ShirakanaDataGroupMember.bigCleanParanoia==50L){

        }
    }
     */
}




