package org.fujiwara.shirakana.adminbot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.announcement.OfflineAnnouncement
import net.mamoe.mirai.contact.announcement.buildAnnouncementParameters
import net.mamoe.mirai.contact.isAdministrator
import net.mamoe.mirai.contact.isOwner
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
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
                    if(ShirakanaDataGroupMember.bigCleanParanoia>100){
                        ShirakanaDataGroupMember.bigCleanParanoia = 100
                    }
                    for (GroupID in ShirakanaDataGroupMember.enabledGroups) {
                        val tmpGroup = bot.getGroup(GroupID.toLong())
                        if (tmpGroup?.botAsMember!!.isAdministrator() || tmpGroup.botAsMember.isOwner()) {
                            if (ShirakanaDataFlags.announcementsList.contains(GroupID.toLong())) {
                                ShirakanaDataFlags.announcementsList[GroupID.toLong()]?.let {
                                    tmpGroup.announcements.delete(
                                        it
                                    )
                                }
                            }
                            val imageAnnouncementTmp =
                                tmpGroup.announcements.uploadImage(ShirakanaParanoia.paranoiaImage(tmpGroup))
                            val parameterTmp = buildAnnouncementParameters {
                                image = imageAnnouncementTmp
                                sendToNewMember = false
                                isPinned = false
                                showEditCard = false
                                showPopup = false
                                requireConfirmation = false
                            }
                            val announcementTemp = OfflineAnnouncement.create("管理偏执度", parameterTmp)
                            if (!ShirakanaDataFlags.announcementsList.contains(GroupID.toLong())) {
                                ShirakanaDataFlags.announcementsList.put(
                                    GroupID.toLong(),
                                    announcementTemp.publishTo(tmpGroup).fid
                                )
                            } else {
                                ShirakanaDataFlags.announcementsList[GroupID.toLong()] =
                                    announcementTemp.publishTo(tmpGroup).fid
                            }
                        }
                    }
                    if (ShirakanaDataGroupMember.bigCleanParanoia == 50L) {
                        bot.getGroup(ShirakanaAdministratorList.adminGroup)
                            ?.sendMessage("偏执度已达到50，可以发动点名清洗（/Tutu SmallClean <群号>）")
                    }
                    if (ShirakanaDataGroupMember.bigCleanParanoia == 100L) {
                        bot.getGroup(ShirakanaAdministratorList.adminGroup)
                            ?.sendMessage("偏执度已达到100，可以发动大清洗（/Tutu BigClean）")
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
                    for(file in dire.listFiles()!!){
                        file.delete()
                    }
                }
            }
        }, startTime, 1000*30*60)
        println("定时任务：缓存清理——启动完毕")
    }
    @EventHandler
    internal suspend fun MemberJoinRequestEvent.handle() {
        if(ShirakanaDataGroupMember.enabledGroups.contains(group?.id)) {
            if (ShirakanaDataGroupMember.blackList.contains(fromId)) {
                reject(false, "你已被清洗，无法再次加入群聊，如有异议可找管理协商。")
                return
            }
            if (ShirakanaBigCleanSetting.refuseSwitch) {
                if (ShirakanaBigCleanSetting.refuseMode) {
                    if (ShirakanaDataGroupMember.groupMembers.contains(fromId) && ShirakanaDataGroupMember.repeatList.contains(fromId)
                    ) {
                        reject(false, "你已经加入了另一个群，且不处于跨群白名单内，如有异议可找管理协商。")
                        return
                    }
                } else {
                    if (ShirakanaDataGroupMember.groupMembers.contains(fromId) && !ShirakanaDataGroupMember.repeatList.contains(fromId)
                    ) {
                        reject(false, "你已经加入了另一个群，且不处于跨群白名单内，如有异议可找管理协商。")
                        return
                    }
                }
            }
            if (ShirakanaDataFlags.memberJoinRequestID.contains(fromId)) {
                reject(false, "你已经发送加入另一个群的请求，请耐心等待处理结果。")
                return
            }
            ShirakanaDataFlags.memberJoinRequestID.add(fromId)
        }
    }
    @EventHandler
    internal suspend fun MemberJoinEvent.handle() {
        if(ShirakanaDataGroupMember.enabledGroups.contains(groupId)) {
            ShirakanaDataGroupMember.groupMembers.add(member.id)
            ShirakanaDataFlags.memberJoinRequestID.remove(member.id)
        }
    }
    @EventHandler
    internal suspend fun MemberLeaveEvent.handle() {
        if(ShirakanaDataGroupMember.enabledGroups.contains(groupId)) {
            for(groupId in ShirakanaDataGroupMember.enabledGroups){
                val groupThis = bot.getGroup(groupId.toLong()) ?: continue
                if(groupThis.members[member.id] == null) return
            }
            ShirakanaDataGroupMember.groupMembers.remove(member.id)
            ShirakanaDataGroupMember.bigCleanWhiteList.remove(member.id)
            ShirakanaDataGroupMember.kickTarget.remove(member.id)
        }
    }
    @EventHandler
    internal suspend fun BotOnlineEvent.handle(){
        ShirakanaDataGroupMember.groupMembers.clear()
        for(group_id in ShirakanaDataGroupMember.enabledGroups){
            val selectedGroup = bot.getGroup(group_id.toLong())?.members
            if (selectedGroup != null) {
                for(member in selectedGroup){
                    ShirakanaDataGroupMember.groupMembers.add(member.id)
                }
            }
        }
        ShirakanaAdminBot.logger.info { "群员信息重载完毕" }
    }
}




