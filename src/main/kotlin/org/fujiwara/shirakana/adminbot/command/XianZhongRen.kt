package org.fujiwara.shirakana.adminbot.command

import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.util.ContactUtils.getContact
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.contact.announcement.OfflineAnnouncement
import net.mamoe.mirai.contact.announcement.buildAnnouncementParameters
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.fujiwara.shirakana.adminbot.*
import org.fujiwara.shirakana.adminbot.configAndData.*
import java.awt.Color
import java.awt.Font
import java.awt.image.*
import java.io.File
import java.net.URL
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.*


fun rotateImage(image: BufferedImage, theta: Double): BufferedImage? {
    val width = image.width
    val height = image.height
    val angle = theta * Math.PI / 180 // 度转弧度
    val resultImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    for (i in 0 until width) {
        for (j in 0 until height) {
            var x = i - width / 2
            var y = height / 2 - j
            val radius = sqrt((x * x + y * y).toDouble())
            var angle1: Double = if (y > 0) {
                acos(x / radius)
            } else {
                2 * Math.PI - acos(x / radius)
            }
            x = (radius * cos(angle1 - angle)).toInt()
            y = (radius * sin(angle1 - angle)).toInt()
            if ((x < width / 2) and (x > -(width / 2)) and (y < height / 2) and (y > -(height / 2))) {
                val rgb = image.getRGB(x + width / 2, height / 2 - y)
                resultImage.setRGB(i, j, rgb)
            } else {

            }
        }
    }
    return resultImage
}

object ShirakanaParanoia : CompositeCommand(
    ShirakanaAdminBot, "Paranoia",
    description = "偏执度相关的操作",
) {
    @SubCommand
    @Description("增加指定数量的偏执度")
    suspend fun CommandSender.add(Amount: Long) {
        if(Amount<=0){return}
        var tempParanoia = ShirakanaDataGroupMember.bigCleanParanoia
        if (tempParanoia < 50){
            tempParanoia+=Amount
            if (tempParanoia in 50..99){
                sendMessage("偏执度已经达到小清洗阈值，请决定是否开始小清洗，若决定为是则运行指令/Tutu SmallClean")
            }
            else{
                if(tempParanoia>=100){
                    sendMessage("偏执度已经达到小清洗阈值，请决定是否开始小清洗，若决定为是则运行指令/Tutu BigClean")
                    tempParanoia=0
                }
            }
        }
        if (tempParanoia in 50..99){
            tempParanoia+=Amount
            if(tempParanoia>=100){
                sendMessage("偏执度已经达到小清洗阈值，请决定是否开始小清洗，若决定为是则运行指令/Tutu BigClean")
                tempParanoia=100
            }
        }
        ShirakanaDataGroupMember.bigCleanParanoia = tempParanoia
        for(GroupID in ShirakanaDataGroupMember.selectedGroups){
            val tmpGroup = bot?.getGroup(GroupID.toLong())
            if(tmpGroup?.botAsMember!!.isAdministrator()||tmpGroup?.botAsMember!!.isOwner()){
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
    }
    @SubCommand
    @Description("减少指定数量的偏执度")
    suspend fun CommandSender.decrease(Amount: Long) {
        if(Amount<=0){return}
        var tempParanoia = ShirakanaDataGroupMember.bigCleanParanoia
        if (tempParanoia < 50){
            tempParanoia-=Amount
            if (tempParanoia <0){
                tempParanoia=0
            }
        }
        if (tempParanoia in 51..99){
            tempParanoia+=Amount
            if(tempParanoia < 50){
                tempParanoia=52
            }
        }
        ShirakanaDataGroupMember.bigCleanParanoia = tempParanoia
        for(GroupID in ShirakanaDataGroupMember.selectedGroups){
            val tmpGroup = bot?.getGroup(GroupID.toLong())
            if(tmpGroup?.botAsMember!!.isAdministrator()||tmpGroup?.botAsMember!!.isOwner()){
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
    }
    @SubCommand
    @Description("查看管理偏执度数值")
    suspend fun CommandSender.amount() {
        if(bot==null||subject==null){
            return
        }
        val group1 = bot?.getGroup(subject!!.id) ?: return
        subject!!.sendMessage(paranoiaImage(group1).uploadAsImage(group1))
    }

    @JvmStatic
    val stalinAKImg: BufferedImage = ImageIO.read(ShirakanaParanoia::class.java.classLoader.getResource("Img.png"))
    @JvmStatic
    val ParanoiaIMG: BufferedImage = ImageIO.read(ShirakanaParanoia::class.java.classLoader.getResource("Paranoia.png"))
    @JvmStatic
    val ParanoiaBg: BufferedImage = ImageIO.read(ShirakanaParanoia::class.java.classLoader.getResource("Paranoia-bg.png"))
    @JvmStatic
    val ParanoiaEmpty: BufferedImage = ImageIO.read(ShirakanaParanoia::class.java.classLoader.getResource("Paranoia-empty.png"))
    @JvmStatic
    val ParanoiaFull: BufferedImage = ImageIO.read(ShirakanaParanoia::class.java.classLoader.getResource("Paranoia-full.png"))
    @JvmStatic
    val ParanoiaTarget: BufferedImage = ImageIO.read(ShirakanaParanoia::class.java.classLoader.getResource("target.png"))


    fun paranoiaImage(group : Group): ExternalResource{
        val newImg = BufferedImage(1280, 800, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics = newImg.createGraphics()
        val newImg2 = BufferedImage(1230, 800, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics2 = newImg.createGraphics()

        graphics.drawImage(ParanoiaEmpty, 0, 0, null)
        val thisFileTmp = File("data/org.fujiwara.shirakana.adminbot.plugin/cleaned")
        var imageSetTmp = mutableSetOf<BufferedImage>()
        if (thisFileTmp.exists()){
            for(fileTmp in thisFileTmp.listFiles()){
                imageSetTmp.add(ImageIO.read(fileTmp))
            }
        }
        var it = imageSetTmp.iterator()
        if(it.hasNext()){
            graphics.drawImage(it.next(), 49, 281,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 211, 267,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 366, 262,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 80, 515,150,150, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 274, 499,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 858, 315,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 1135, 264,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 939, 548,140,140, null)
        }
        if(it.hasNext()){
            graphics.drawImage(it.next(), 1109, 608,140,140, null)
        }
        graphics.drawImage(ParanoiaTarget,0,0,null)
        graphics.drawImage(rotateImage(ParanoiaFull,-1.8*ShirakanaDataGroupMember.bigCleanParanoia), 490, 429, null)
        graphics.drawImage(ParanoiaBg, 0, 0, null)
        var font = Font("黑体", Font.PLAIN, 38)
        graphics.font = font
        graphics.drawString("管理偏执度",533,72)
        font = Font("黑体", Font.PLAIN, 31)
        graphics2.font = font
        val str = "一种怀疑和不信任的气氛笼罩着"+group.name+"。随着内务人民委员会对不计其数的怠工、"
        val str2 = "破坏及间谍行为的指控，"+group.owner.nick+"对群内派系斗争的担忧与日俱增"
        graphics2.drawString(str,25,155)
        graphics2.drawString(str2,25,186)
        graphics2.dispose()
        graphics.drawImage(newImg2,25,127,null)
        graphics.drawImage(rotateImage(ParanoiaIMG,-1.89*ShirakanaDataGroupMember.bigCleanParanoia),516,441,null)
        font = Font("黑体", Font.PLAIN, 39)
        graphics.font = font
        graphics.drawString("管理偏执度: "+ShirakanaDataGroupMember.bigCleanParanoia.toString()+"%",516,735)
        graphics.dispose()
        val uuidFileName="shirakana/"+UUID.randomUUID().toString().replace("-","")+".png"
        val outPutImage = File(uuidFileName)
        ImageIO.write(newImg, "png", outPutImage)
        return outPutImage.toExternalResource()
    }

    suspend fun GetImageTutu(target : Contact,group : Contact):Image{
        return KillGroupMembersImage(ImageIO.read(URL(target.avatarUrl))).uploadAsImage(group)
    }

    fun KillGroupMembersImage(imageMembers: BufferedImage): ExternalResource {
        val newImg = BufferedImage(600, 349, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics = newImg.createGraphics()
        graphics.drawImage(imageMembers, 29, 22, 154, 154, null)
        graphics.drawImage(stalinAKImg, 0, 0, null)
        graphics.dispose()
        val uuidFileName="shirakana/"+Date().toString().replace("-","")+UUID.randomUUID().toString().replace("-","")+".png"
        val outPutImage = File(uuidFileName)
        ImageIO.write(newImg, "png", outPutImage)
        return outPutImage.toExternalResource()
    }
}

object ShirakanaXianZhongRen : CompositeCommand(
    ShirakanaAdminBot, "Tutu",
    description = "开启献忠人模式",
) {
    @SubCommand
    @Description("小清洗")
    suspend fun CommandSender.SmallClean(GroupId : String){
        if(ShirakanaDataFlags.flagSmallCleanStart){
            sendMessage("另一个群已经开始清洗")
            return
        }
        if(ShirakanaDataGroupMember.selectedGroups.contains(GroupId)){
            val thisGroup = bot?.getGroup(GroupId.toLong())
            var i = 0
            var msgChain = buildMessageChain {
                +PlainText("当前群聊管理偏执度过高，准备开始清洗\n名单加急：\n")
            }
            if(thisGroup!=null){
                for(tempMember in thisGroup.members){
                    if(ShirakanaDataGroupMember.smallCleanTarget.contains(tempMember.id.toString())){
                        i++
                        msgChain += buildMessageChain {
                            +PlainText("[$i]、")
                            +tempMember.at()
                            +PlainText("\n")
                        }
                    }
                }
                if(i==0){
                    sendMessage("当前名单为空或指定群聊中无名单上群员")
                    return
                }
                msgChain.plus("请内务部选择要枪毙的群员（AT即可）\n清洗结束时请管理员输入：结束清洗")
                thisGroup.sendMessage(AtAll)
                thisGroup.sendMessage(msgChain)
                ShirakanaDataFlags.flagSmallCleanStart = true
                ShirakanaDataFlags.flagSmallCleanTarget = GroupId.toLong()
            }
        }else{
            sendMessage("该群未启用功能")
        }
    }
    @SubCommand
    @Description("大清洗")
    suspend fun CommandSender.BigClean(){
        sendMessage("输入/Tutu StartBigCleanStartBigClean <群号> <确认群号>来进行大清洗\n警告！！！！！！！\n大清洗功能会清理60天内未发言的所有群友（排除大清洗保护名单、有专属头衔、群备注中包含标准命名规则的人），该清洗可能会是几十甚至几百的量级且无法撤回，请仔细斟酌是否执行")
    }
    @SubCommand
    @Description("确认大清洗")
    suspend fun CommandSender.StartBigCleanStartBigClean(GroupTarget: String,ConfirmGroup: String) {
        if(ShirakanaBigCleanSetting.nameStandard==""){
            sendMessage("未配置ShirakanaBigCleanSetting.nameStandard（群名标准），请前往config设置")
            return
        }
        if(GroupTarget==""||ConfirmGroup==""||(GroupTarget!=ConfirmGroup)){
            sendMessage("请再次思考后输入正确的参数")
            return
        }else{
            if(!ShirakanaDataGroupMember.selectedGroups.contains(GroupTarget)){
                sendMessage("该群聊未启用")
                return
            }else{
                val currGroupThis = bot?.getGroup(GroupTarget.toLong())
                if (currGroupThis != null) {
                    for(currMember in currGroupThis.members){
                        if(currMember.lastSpeakTimestamp>=ShirakanaBigCleanSetting.bigCleanTargetTimeDay*24*60*60*100) {
                            if (ShirakanaDataGroupMember.bigCleanTarget.contains(currMember.id.toString())) {
                                continue
                            }
                            if (currMember.specialTitle != "") {
                                continue
                            }
                            if (currMember.nameCard.contains(ShirakanaBigCleanSetting.nameStandard)) {
                                continue
                            }
                            if(currMember.isAdministrator()||currMember.isOwner()){
                                continue
                            }
                            else{
                                currMember.kick("您由于过长时间不发言且未注明理由被请出群聊，如果误踢请重新加入，对此造成的麻烦我们深表歉意。")
                            }
                        }
                    }
                }
            }
            ShirakanaDataGroupMember.bigCleanParanoia=0L
        }
    }
    @SubCommand
    @Description("查看处于被清洗状态的QQ号码")
    suspend fun CommandSender.List(){
        sendMessage("被清洗的QQ："+ShirakanaDataGroupMember.ShirakanaBlackListGroup)
    }
    @SubCommand
    @Description("将一个账号添加至“被清洗”状态列表")
    suspend fun CommandSender.Add(QQId : Long){
        if(ShirakanaDataGroupMember.ShirakanaBlackListGroup.add(QQId.toString())){
            sendMessage(QQId.toString()+"已被添加至列表")
        }
    }
    @SubCommand
    @Description("将一个账号移除出“被清洗”状态列表")
    suspend fun CommandSender.Del(QQId : Long){
        if(ShirakanaDataGroupMember.ShirakanaBlackListGroup.remove(QQId.toString())){
            sendMessage(QQId.toString()+"已被移除出列表")
            val thisFileName = "data/org.fujiwara.shirakana.adminbot.plugin/cleaned/$QQId.png"
            val delPutImage = File(thisFileName)
            delPutImage.delete()
        }
    }
    @SubCommand
    @Description("清洗处于“被清洗”状态的成员")
    suspend fun CommandSender.kick(){
        for(groupId in ShirakanaDataGroupMember.selectedGroups){
            val groupThisShira = bot?.getGroup(groupId.toLong())
            for(BlackId in ShirakanaDataGroupMember.ShirakanaBlackListGroup){
                val thisGroupTarget = groupThisShira?.get(BlackId.toLong())
                if(thisGroupTarget==null){
                    continue
                }else{
                    if(thisGroupTarget.isOwner()||thisGroupTarget.isAdministrator()){
                        continue
                    }else{
                        val newImg = ImageIO.read(URL(thisGroupTarget.avatarUrl))
                        val thisFileName = "data/org.fujiwara.shirakana.adminbot.plugin/cleaned/"+thisGroupTarget.id.toString()+".png"
                        val outPutImage = File(thisFileName)
                        ImageIO.write(newImg, "png", outPutImage)
                        thisGroupTarget.kick("你已被清洗")
                    }
                }
            }
        }
        sendMessage("清洗完毕")
    }
    @SubCommand
    @Description("帮助")
    suspend fun CommandSender.help(){
        sendMessage("帮助如下：（<>代表参数）\n/Tutu SmallClean <群号>：在指定群开启清洗（清洗目标为CleanList smallclean指定的人，执行后可以从中选择任意人数进行清洗\n/Tutu BigClean：开启大清洗（会跳过CleanList bigclean指定的人、有群专属头衔的人、群备注包含ShirakanaBigCleanSetting.nameStandard的人）默认目标为2个月不发言的群员\n/Tutu List：查看被清洗者名单\n/Tutu Add <QQ>：将某人加入已被清洗名单\n/Tutu Del <QQ>：将某人删除出已被清洗名单\n/Tutu kick：在所有的群踢出“已被清洗”名单中的群员")
    }
}

object QuickCleanRepeatTarget : SimpleCommand(
    ShirakanaAdminBot, "CleanRepeat",
    description = "清理被禁止重复加群的群员（将其保留在指定群，其他群踢出）"
) {
    @Handler
    suspend fun CommandSender.quickCleanRepeatTarget(GroupTarget: String) {
        if(ShirakanaDataGroupMember.selectedGroups.contains(GroupTarget)){
            if(ShirakanaBigCleanSetting.repeat_join_clean_mode){
                val thisGroupTemp = bot?.getGroup(GroupTarget.toLong())
                if(thisGroupTemp!=null){
                    for(memberId in ShirakanaDataGroupMember.groupMembersTarget){
                        if(thisGroupTemp.members.contains(memberId.toLong())){
                            for(groupId in ShirakanaDataGroupMember.selectedGroups){
                                if(groupId == GroupTarget){
                                    continue
                                }else{
                                    val tmpThisTargetMember = bot?.getGroup(groupId.toLong())?.get(memberId.toLong())
                                    if (tmpThisTargetMember != null) {
                                        if(tmpThisTargetMember.isAdministrator()||tmpThisTargetMember.isOwner()){
                                            continue
                                        }else{
                                            tmpThisTargetMember.kick("你重复加入了另一个群")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else {
                if(ShirakanaDataGroupMember.selectedGroups.contains(GroupTarget)) {
                    val tmpThisGroup= bot?.getGroup(GroupTarget.toLong()) ?: return
                    for(membersThisTmp in tmpThisGroup.members) {
                        if(membersThisTmp.isAdministrator()||membersThisTmp.isOwner()||ShirakanaDataGroupMember.groupMembersTarget.contains(membersThisTmp.id.toString())){
                            continue
                        }
                        for (groupId in ShirakanaDataGroupMember.selectedGroups) {
                            if (groupId == GroupTarget) {
                                continue
                            } else {
                                val tmpTargetGroup = bot?.getGroup(groupId.toLong())
                                if(tmpTargetGroup!=null){
                                    if(tmpTargetGroup.members.contains(membersThisTmp.id)){
                                        membersThisTmp.kick("你重复加入了其他群聊")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }else{
            sendMessage("该群未启用功能")
        }
        sendMessage("清理完毕")
    }
}

object ListBigCleanTargets : SimpleCommand(
    ShirakanaAdminBot, "ListBigCleanTargets",
    description = "测试指令"
) {
    @Handler
    suspend fun CommandSender.listBigCleanTargets(GroupTarget: Long) {
        var msg = "以下群友包括在大清洗跳过名单中：\n"
        val membersListTmp = bot?.getGroup(GroupTarget)?.members
        if (membersListTmp != null) {
            for(member in membersListTmp){
                if(ShirakanaDataGroupMember.bigCleanTarget.contains(member.id.toString())||member.specialTitle != ""||member.nameCard.contains(ShirakanaBigCleanSetting.nameStandard)){
                    msg+="昵称：["+member.specialTitle+"] "+member.nick+"\n"
                }
            }
        }
        sendMessage(msg)
    }
}